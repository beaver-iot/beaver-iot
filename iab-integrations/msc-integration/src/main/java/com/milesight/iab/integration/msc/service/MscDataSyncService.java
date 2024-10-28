package com.milesight.iab.integration.msc.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.milesight.cloud.sdk.client.model.DeviceDetailResponse;
import com.milesight.cloud.sdk.client.model.DeviceSearchRequest;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.integration.model.Device;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.integration.msc.constant.MscIntegrationConstants;
import com.milesight.iab.integration.msc.entity.MscConnectionPropertiesEntities;
import com.milesight.iab.integration.msc.util.MscTslUtils;
import com.milesight.msc.sdk.utils.TimeUtils;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    private EntityServiceProvider entityServiceProvider;

    private Timer timer;

    private int periodSeconds = 0;

    // only two tasks allowed at a time (one running and one waiting)
    private static final ExecutorService syncAllDataExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1),
            (r, executor) -> log.info("Task exists. Ignored."));

    private static final ExecutorService concurrentSyncDeviceDataExecutor = new ThreadPoolExecutor(2, 10,
            300L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static final ConcurrentHashMap<String, Object> deviceIdentifierToTaskLock = new ConcurrentHashMap<>(128);

    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.scheduled-data-fetch", eventType = ExchangeEvent.EventType.UP)
    public void onScheduledDataFetchPropertiesUpdate(Event<MscConnectionPropertiesEntities.ScheduledDataFetch> event) {
        periodSeconds = event.getPayload().getPeriod();
        restart();
    }

    @EventSubscribe(payloadKeyExpression = "msc-integration.integration.openapiStatus", eventType = ExchangeEvent.EventType.UP)
    public void onOpenapiStatusUpdate(Event<MscConnectionPropertiesEntities> event) {
        val status = event.getPayload().getOpenapiStatus();
        if (MscIntegrationConstants.IntegrationStatus.READY.equals(status)) {
            syncAllDataExecutor.submit(this::syncAllData);
        }
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
            val scheduledDataFetchSettings = entityServiceProvider.findExchangeByKey(
                    MscConnectionPropertiesEntities.getKey(MscConnectionPropertiesEntities.Fields.scheduledDataFetch),
                    MscConnectionPropertiesEntities.ScheduledDataFetch.class);
            if (scheduledDataFetchSettings == null) {
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
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                syncAllDataExecutor.submit(() -> syncAllData());
            }
        }, 0, periodSeconds * 1000L);

        log.info("timer started");
    }

    private void syncAllData() {
        log.info("Fetching data from MSC");
        try {
            syncAllDeviceData();
        } catch (Exception e) {
            log.error("Error while fetching data from MSC", e);
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
    private void syncAllDeviceData() {
        if (mscClientProvider == null || mscClientProvider.getMscClient() == null) {
            log.warn("MscClient not initiated.");
            return;
        }

        val mscClient = mscClientProvider.getMscClient();

        val allDevices = deviceServiceProvider.findAll(MscIntegrationConstants.INTEGRATION_IDENTIFIER);
        log.info("Found {} devices.", allDevices.size());
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

            val removeDevicesTasks = existingDevices.stream()
                    .map(identifier -> syncDeviceData(new Task(Task.Type.REMOVE_LOCAL_DEVICE, identifier, null)))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(removeDevicesTasks);
        }
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

                if (task.type != Task.Type.REMOVE_LOCAL_DEVICE) {
                    if (device == null) {
                        log.warn("Add or update local device failed: {}", task.identifier);
                        return false;
                    }
                    int lastSyncTime = getAndUpdateLastSyncTime(device);
                    syncPropertiesHistory(device, lastSyncTime);
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
        val lastSyncTimeKey = MscIntegrationConstants.InternalPropertyKey.getLastSyncTimeKey(device.getKey());

        int lastSyncTime = 0;
        val lastSyncTimeObj = entityServiceProvider.findExchangeByKey(lastSyncTimeKey, ExchangePayload.class)
                .getPayload(MscIntegrationConstants.InternalPropertyKey.LAST_SYNC_TIME);
        if (lastSyncTimeObj instanceof Number t) {
            lastSyncTime = t.intValue();
        }
        entityServiceProvider.saveExchange(ExchangePayload.create(lastSyncTimeKey, timestamp));
        return lastSyncTime;
    }

    @SneakyThrows
    private void syncPropertiesHistory(Device device, int lastSyncTime) {
        // deviceId should not be null
        val deviceId = (String) device.getAdditional().get("deviceId");
        long t24HoursBefore = TimeUtils.currentTimeSeconds() - TimeUnit.DAYS.toSeconds(1);
        long startTime = Math.max(lastSyncTime, t24HoursBefore);
        long endTime = TimeUtils.currentTimeSeconds();
        long pageSize = 100;
        String pageKey = null;
        boolean hasNextPage = true;
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
                saveHistoryData(device.getKey(), properties, item.getTs() == null ? TimeUtils.currentTimeMillis() : item.getTs());
            });
        }
    }

    public void saveHistoryData(String deviceKey, JsonNode properties, long timestampMs) {
        // todo support event and service
        val payload = MscTslUtils.convertJsonNodeToExchangePayload(deviceKey, properties);
        if (payload == null || payload.isEmpty()) {
            return;
        }
        payload.setTimestamp(timestampMs);
        entityServiceProvider.saveExchangeHistory(payload);
    }

    @SneakyThrows
    private Device updateLocalDevice(Task task) {
        val details = getDeviceDetails(task);
        val deviceId = details.getDeviceId();
        val thingSpec = mscDeviceService.getThingSpec(String.valueOf(deviceId));
        return mscDeviceService.updateLocalDevice(task.identifier, details.getName(), String.valueOf(deviceId), thingSpec);
    }

    @SneakyThrows
    private Device addLocalDevice(Task task) {
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
        val device = deviceServiceProvider.findByIdentifier(identifier, MscIntegrationConstants.INTEGRATION_IDENTIFIER);
        if (device != null) {
            deviceServiceProvider.deleteById(device.getId());
        }
        return device;
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
