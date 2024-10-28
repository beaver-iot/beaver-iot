package com.milesight.iab.sample.complex.listener;


import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.builder.EntityBuilder;
import com.milesight.iab.context.integration.builder.IntegrationBuilder;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;
import com.milesight.iab.eventbus.api.EventResponse;
import com.milesight.iab.sample.complex.entity.DemoMscServiceEntities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author leon
 */
@Component
@Slf4j
public class DemoEntityEventSubscribe {

    @Autowired
    private IntegrationServiceProvider integrationServiceProvider;

    // save properties
    @EventSubscribe(payloadKeyExpression ="demo-complex-integration.integration.setting.*")
    public EventResponse subscribeSaveSetting(Event<DemoMscServiceEntities.DemoMscSettingEntities> event) {
        DemoMscServiceEntities.DemoMscSettingEntities payload = event.getPayload();
        log.debug("DemoEntityEventSubscribe subscribeSaveSetting:{}", payload);
        log.info("url :{}",payload.getUrl());
        log.info("accessKey: {}",payload.getAccessKey());
        log.info("secretKey :{}",payload.getSecretKey());
        return EventResponse.of("integrationSettingResponse", "success");
    }

    // sync event
    @EventSubscribe(payloadKeyExpression ="demo-complex-integration.integration.syncDevice")
    public void subscribeSyncEntity(ExchangeEvent event) {
        log.debug("DemoEntityEventSubscribe subscribeSyncEntity:{}",event);
        //1.call msc url

        //2. save device\entity metadata
        Entity entityConfig = new EntityBuilder()
                .property("prop1", AccessMod.W)
                    .children()
                        .property("prop11", AccessMod.W).end()
                    .children()
                        .property("prop12", AccessMod.W).end()
                .build();
        Integration integrationConfig = new IntegrationBuilder()
                .integration("name", "description")
                    .initialDevice("name","description")
                        .entity(entityConfig)
                        .entity(entityConfig)
                        .end()
                    .initialDevice()
                        .name("a")
                        .identifier("b")
                        .entity(entityConfig)
                    .end()
                .build();
        integrationServiceProvider.save(integrationConfig);

    }
    @EventSubscribe(payloadKeyExpression ="demo-complex-integration.integration.syncHistory")
    public void subscribeHistory(ExchangeEvent event) {
        //方案1： 采用api调用 + 编码
        //方案2： 采用规则引擎编排 + timer
        log.debug("DemoEntityEventSubscribe subscribeHistory:{}",event);
    }

    //exchange up ? async?
    @EventSubscribe(payloadKeyExpression ="demo-complex-integration.integration.*", eventType = ExchangeEvent.EventType.DOWN)
    public void subscribeDeviceExchangeUp(ExchangeEvent event) {
        // 同上
        log.debug("DemoEntityEventSubscribe subscribeDeviceExchangeUp:{}",event);
    }
}
