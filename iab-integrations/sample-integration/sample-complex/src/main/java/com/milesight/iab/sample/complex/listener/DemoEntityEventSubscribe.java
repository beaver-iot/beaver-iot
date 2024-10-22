package com.milesight.iab.sample.complex.listener;


import com.milesight.iab.eventbus.annotations.EventSubscribe;
import com.milesight.iab.eventbus.api.Event;
import com.milesight.iab.sample.complex.entity.DemoMscSettingEntities;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.builder.EntityBuilder;
import com.milesight.iab.context.integration.builder.IntegrationBuilder;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.context.integration.model.event.ExchangeEvent;

/**
 * @author leon
 */
public class DemoEntityEventSubscribe {

    private DeviceServiceProvider deviceServiceProvider;
    private IntegrationServiceProvider integrationServiceProvider;

    // save properties
    @EventSubscribe(payloadKeyExpression ="msc-integration.integration.connect")
    public void subscribeSaveSetting(Event<DemoMscSettingEntities> event) {
    }

    // sync event
    @EventSubscribe(payloadKeyExpression ="msc-integration.integration.syncDevice")
    public void subscribeSyncEntity(ExchangeEvent event) {
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
    @EventSubscribe(payloadKeyExpression ="${integration.name}.integration.syncHistory")
    public void subscribeHistory(ExchangeEvent event) {
        //方案1： 采用api调用 + 编码
        //方案2： 采用规则引擎编排 + timer
    }

    //exchange up ? async?
    @EventSubscribe(payloadKeyExpression ="${integration.name}.integration.*", eventType = ExchangeEvent.EventType.UP)
    public void subscribeDeviceExchangeUp(ExchangeEvent event) {
        // 同上
    }
}
