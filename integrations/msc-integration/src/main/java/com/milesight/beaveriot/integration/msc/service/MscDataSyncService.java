package com.milesight.beaveriot.integration.msc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.milesight.beaveriot.context.api.EntityValueServiceProvider;
import com.milesight.cloud.sdk.client.model.DeviceDetailResponse;
import com.milesight.cloud.sdk.client.model.DeviceSearchRequest;
import com.milesight.beaveriot.context.api.DeviceServiceProvider;
import com.milesight.beaveriot.context.api.EntityServiceProvider;
import com.milesight.beaveriot.context.api.ExchangeFlowExecutor;
import com.milesight.beaveriot.context.integration.model.Device;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.context.integration.model.event.ExchangeEvent;
import com.milesight.beaveriot.eventbus.annotations.EventSubscribe;
import com.milesight.beaveriot.eventbus.api.Event;
import com.milesight.beaveriot.integration.msc.constant.MscIntegrationConstants;
import com.milesight.beaveriot.integration.msc.entity.MscConnectionPropertiesEntities;
import com.milesight.beaveriot.integration.msc.entity.MscServiceEntities;
import com.milesight.beaveriot.integration.msc.model.IntegrationStatus;
import com.milesight.beaveriot.integration.msc.util.MscTslUtils;
import com.milesight.msc.sdk.utils.TimeUtils;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
public class MscDataSyncService {

    @Lazy
    @Autowired
    private IMscClientProvider mscClientProvider;

    @Lazy
    @Autowired
    private MscDeviceService mscDeviceService;

    @Autowired
    private DeviceServiceProvider deviceServiceProvider;

    @Autowired
    private EntityValueServiceProvider entityValueServiceProvider;

    @Autowired
    private ExchangeFlowExecutor exchangeFlowExecutor;

    private Timer timer;

    private int periodSeconds = 0;

    // Only two existing tasks allowed at a time (one running and one waiting)
    private static final ExecutorService syncAllDataExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1),
            (r, executor) -> {
                throw new RejectedExecutionException("Another task is running.");
            });

    private static final ExecutorService concurrentSyncDeviceDataExecutor = new ThreadPoolExecutor(2, 4,
            300L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static final ConcurrentHashMap<String, Object> deviceIdentifierToTaskLock = new ConcurrentHashMap<>(128);

    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.scheduled_data_fetch.*", eventType = ExchangeEvent.EventType.DOWN)
    public void onScheduledDataFetchPropertiesUpdate(Event<MscConnectionPropertiesEntities.ScheduledDataFetch> event) {
        periodSeconds = event.getPayload().getPeriod();
        restart();
    }

    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.openapi_status", eventType = ExchangeEvent.EventType.DOWN)
    public void onOpenapiStatusUpdate(Event<MscConnectionPropertiesEntities> event) {
        val status = event.getPayload().getOpenapiStatus();
        if (IntegrationStatus.READY.name().equals(status)) {
            try {
                syncAllDataExecutor.submit(this::syncDeltaData);
            } catch (RejectedExecutionException  e) {
                log.error("Task rejected: ", e);
            }
        }
    }

    @SneakyThrows
    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.sync_device", eventType = ExchangeEvent.EventType.DOWN)
    public void onSyncDevice(Event<MscServiceEntities.SyncDevice> event) {
        syncAllDataExecutor.submit(this::syncAllData).get();
    }


    public void restart() {
        stop();
        start();
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        log.info("timer stopped");
    }

    public void init() {
        start();
    }

    public void start() {
        log.info("timer starting");
        if (timer != null) {
            return;
        }
        if (periodSeconds == 0) {
            val scheduledDataFetchSettings = entityValueServiceProvider.findValuesByKey(
                    MscConnectionPropertiesEntities.getKey(MscConnectionPropertiesEntities.Fields.scheduledDataFetch),
                    MscConnectionPropertiesEntities.ScheduledDataFetch.class);
            if (scheduledDataFetchSettings.isEmpty()) {
                periodSeconds = -1;
                return;
            }
            if (!Boolean.TRUE.equals(scheduledDataFetchSettings.getEnabled())
                    || scheduledDataFetchSettings.getPeriod() == null
                    || scheduledDataFetchSettings.getPeriod() == 0) {
                // not enabled or invalid period
                periodSeconds = -1;
            } else if (scheduledDataFetchSettings.getPeriod() > 0) {
                periodSeconds = scheduledDataFetchSettings.getPeriod();
            }
        }
        if (periodSeconds < 0) {
            return;
        }
        timer = new Timer();

        // setup timer
        val periodMills = periodSeconds * 1000L;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    syncAllDataExecutor.submit(() -> syncDeltaData());
                } catch (RejectedExecutionException e) {
                    log.error("Task rejected: ", e);
                }
            }
        }, periodMills, periodMills);

        log.info("timer started");
    }

    /**
     * Pull data from MSC, all devices and part of history data which created after the last execution will be added to local storage.
     */
    private void syncDeltaData() {
        log.info("Fetching delta data from MSC");
        try {
            syncAllDeviceData(true);
        } catch (Exception e) {
            log.error("Error while fetching delta data from MSC", e);
        }
    }

    /**
     * Pull all devices and all history data.
     */
    private void syncAllData() {
        log.info("Fetching all data from MSC");
        try {
            syncAllDeviceData(false);
        } catch (Exception e) {
            log.error("Error while fetching all data from MSC", e);
        }
    }

    private Object markDeviceTaskRunning(String identifier, boolean force) {
        var lock = deviceIdentifierToTaskLock.get(identifier);
        if (force && lock != null) {
            return lock;
        } else if (lock == null) {
            lock = new Object();
            val previous = deviceIdentifierToTaskLock.putIfAbsent(identifier, lock);
            if (previous == null) {
                return lock;
            } else {
                // put value failed
                if (force) {
                    return previous;
                }
                return null;
            }
        } else {
            return null;
        }
    }

    private void markDeviceTaskFinished(String identifier, Object lock) {
        deviceIdentifierToTaskLock.remove(identifier, lock);
    }

    @SneakyThrows
    private void syncAllDeviceData(boolean delta) {
        if (mscClientProvider == null || mscClientProvider.getMscClient() == null) {
            log.warn("MscClient not initiated.");
            return;
        }
        syncDevicesFromMsc();
        syncDeviceHistoryDataFromMsc(delta);
    }

    private void syncDevicesFromMsc() throws IOException {
        log.info("Sync devices from MSC.");
        val mscClient = mscClientProvider.getMscClient();
        val allDevices = deviceServiceProvider.findAll(MscIntegrationConstants.INTEGRATION_IDENTIFIER);
        log.info("Found {} devices from local.", allDevices.size());
        val existingDevices = allDevices.stream().map(Device::getIdentifier).collect(Collectors.toSet());
        long pageNumber = 1;
        long pageSize = 10;
        long total = 0;
        long fetched = -1;
        while (fetched < total) {
            val response = mscClient.device().searchDetails(new DeviceSearchRequest()
                            .pageSize(pageSize)
                            .pageNumber(pageNumber))
                    .execute()
                    .body();
            if (response == null || response.getData() == null || response.getData().getTotal() == null) {
                log.warn("Response is empty: {}", response);
                return;
            }
            val list = response.getData().getContent();
            if (list == null || list.isEmpty()) {
                log.warn("Content is empty.");
                return;
            }
            fetched += pageSize;
            total = response.getData().getTotal();

            val syncDeviceTasks = list.stream().map(details -> {
                val identifier = details.getSn();
                if (identifier == null) {
                    return CompletableFuture.completedFuture(null);
                }
                var type = Task.Type.ADD_LOCAL_DEVICE;
                if (existingDevices.contains(identifier)) {
                    existingDevices.remove(identifier);
                    type = Task.Type.UPDATE_LOCAL_DEVICE;
                }
                return syncDeviceData(new Task(type, identifier, details));
            }).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(syncDeviceTasks);
        }
        log.info("Pull devices from MSC finished, total devices: {}", total);

        val removeDevicesTasks = existingDevices.stream()
                .map(identifier -> syncDeviceData(new Task(Task.Type.REMOVE_LOCAL_DEVICE, identifier, null)))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(removeDevicesTasks);
    }

    private void syncDeviceHistoryDataFromMsc(boolean delta) {
        log.info("Sync device history data from MSC.");
        val allDevices = deviceServiceProvider.findAll(MscIntegrationConstants.INTEGRATION_IDENTIFIER);
        log.info("Found {} devices from local.", allDevices.size());
        allDevices.forEach(device -> {
            try {
                int lastSyncTime = 0;
                if (delta) {
                    lastSyncTime = getAndUpdateLastSyncTime(device);
                }
                syncPropertiesHistory(device, lastSyncTime);
                // events and services are not supported yet
            } catch (Exception e) {
                log.error("Error occurs while syncing device history data from MSC, device key: {}", device.getKey(), e);
            }
        });
        log.info("Sync device history data from MSC finished, total devices: {}", allDevices.size());
    }

    public CompletableFuture<Boolean> syncDeviceData(Task task) {
        // if fetching or removing data, then return
        val lock = markDeviceTaskRunning(task.identifier, task.type == Task.Type.REMOVE_LOCAL_DEVICE);
        if (lock == null) {
            log.info("Skip execution because device task is running: {}", task.identifier);
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                Device device = null;
                switch (task.type) {
                    case REMOVE_LOCAL_DEVICE -> device = removeLocalDevice(task.identifier);
                    case ADD_LOCAL_DEVICE -> device = addLocalDevice(task);
                    case UPDATE_LOCAL_DEVICE -> device = updateLocalDevice(task);
                }

                if (task.type != Task.Type.REMOVE_LOCAL_DEVICE && device == null) {
                    log.warn("Add or update local device failed: {}", task.identifier);
                    return false;

                }
                return true;
            } catch (Exception e) {
                log.error("Error while syncing local device data.", e);
                return false;
            } finally {
                markDeviceTaskFinished(task.identifier, lock);
            }
        }, concurrentSyncDeviceDataExecutor);
    }

    private int getAndUpdateLastSyncTime(Device device) {
        // update last sync time
        val timestamp = TimeUtils.currentTimeSeconds();
        val lastSyncTimeKey = MscIntegrationConstants.InternalPropertyIdentifier.getLastSyncTimeKey(device.getKey());
        val lastSyncTime = Optional.ofNullable(entityValueServiceProvider.findValueByKey(lastSyncTimeKey))
                .map(JsonNode::intValue)
                .orElse(0);
        exchangeFlowExecutor.syncExchangeDown(ExchangePayload.create(lastSyncTimeKey, timestamp));
        return lastSyncTime;
    }

    @SneakyThrows
    private void syncPropertiesHistory(Device device, int lastSyncTime) {
        // deviceId should not be null
        val deviceId = (String) device.getAdditional().get(MscIntegrationConstants.DeviceAdditionalDataName.DEVICE_ID);
        long time24HoursBefore = TimeUtils.currentTimeSeconds() - TimeUnit.DAYS.toSeconds(1);
        long startTime = Math.max(lastSyncTime, time24HoursBefore) * 1000;
        long endTime = TimeUtils.currentTimeMillis();
        long pageSize = 100;
        String pageKey = null;
        boolean hasNextPage = true;
        val isLatestData = new AtomicBoolean(true);
        while (hasNextPage) {
            val page = mscClientProvider.getMscClient()
                    .device()
                    .getPropertiesHistory(deviceId, startTime, endTime, pageSize, pageKey, null)
                    .execute()
                    .body();
            if (page == null || page.getData() == null || page.getData().getList() == null) {
                log.warn("Response is empty.");
                break;
            }
            pageKey = page.getData().getNextPageKey();
            hasNextPage = pageKey != null;
            page.getData().getList().forEach(item -> {
                val objectMapper = mscClientProvider.getMscClient().getObjectMapper();
                val properties = objectMapper.convertValue(item.getProperties(), JsonNode.class);
                saveHistoryData(device.getKey(), null, properties, item.getTs() == null ? TimeUtils.currentTimeMillis() : item.getTs(), isLatestData.get());
                if (isLatestData.get()) {
                    isLatestData.set(false);
                }
            });
        }
    }

    public void saveHistoryData(String deviceKey, String eventId, JsonNode data, long timestampMs, boolean isLatestData) {
        val payload = eventId == null
                ? MscTslUtils.convertJsonNodeToExchangePayload(deviceKey, data)
                : MscTslUtils.convertJsonNodeToExchangePayload(String.format("%s.%s", deviceKey, eventId), data, false);
        if (payload == null || payload.isEmpty()) {
            return;
        }
        payload.setTimestamp(timestampMs);
        log.debug("Save device history data: {}", payload);
        if (!isLatestData) {
            entityValueServiceProvider.saveHistoryRecord(payload, payload.getTimestamp());
        } else {
            exchangeFlowExecutor.asyncExchangeUp(payload);
        }
    }

    @SneakyThrows
    private Device updateLocalDevice(Task task) {
        log.info("Update local device: {}", task.identifier);
        val details = getDeviceDetails(task);
        val deviceId = details.getDeviceId();
        val thingSpec = mscDeviceService.getThingSpec(String.valueOf(deviceId));
        return mscDeviceService.updateLocalDevice(task.identifier, String.valueOf(deviceId), thingSpec);
    }

    @SneakyThrows
    private Device addLocalDevice(Task task) {
        log.info("Add local device: {}", task.identifier);
        val details = getDeviceDetails(task);
        val deviceId = details.getDeviceId();
        val thingSpec = mscDeviceService.getThingSpec(String.valueOf(deviceId));
        return mscDeviceService.addLocalDevice(task.identifier, details.getName(), String.valueOf(deviceId), thingSpec);
    }

    @SuppressWarnings("ConstantConditions")
    private DeviceDetailResponse getDeviceDetails(Task task)
            throws IOException, NullPointerException, IndexOutOfBoundsException {

        var details = task.details;
        if (details == null) {
            details = mscClientProvider.getMscClient().device().searchDetails(DeviceSearchRequest.builder()
                            .sn(task.identifier)
                            .pageNumber(1L)
                            .pageSize(1L)
                            .build())
                    .execute()
                    .body()
                    .getData()
                    .getContent()
                    .get(0);
        }
        return details;
    }

    private Device removeLocalDevice(String identifier) {
        // delete is unsupported currently
        return null;
    }


    public record Task(@Nonnull Type type, @Nonnull String identifier, @Nullable DeviceDetailResponse details) {

        public enum Type {
            ADD_LOCAL_DEVICE,
            UPDATE_LOCAL_DEVICE,
            REMOVE_LOCAL_DEVICE,
            ;
        }

    }

}
