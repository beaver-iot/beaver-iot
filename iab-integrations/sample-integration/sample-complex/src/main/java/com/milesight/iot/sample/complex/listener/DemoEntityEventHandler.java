package com.milesight.iot.sample.complex.listener;


import com.milesight.iot.sample.complex.entity.DemoDeviceEntities;
import com.milesight.iot.tmp.core.eventbus.annotation.EventHandler;
import com.milesight.iot.tmp.spec.base.event.Event;
import com.milesight.iot.tmp.spec.entity.model.ExchangePayload;

/**
 * @author leon
 */
public class DemoEntityEventHandler {

    //仅支持精确匹配
    @EventHandler(payloadKey="${integration.name}.integration.connect")
    public DemoDeviceEntities handleService(Event<DemoDeviceEntities> event) {
        return null;
    }

    //设备ID如何处理？？
    @EventHandler(payloadKey="${integration.name}.device.demoSN.changeStatus")
    public DemoDeviceEntities handleDeviceService(Event<DemoDeviceEntities> event) {
        return null;
    }

    //设备ID如何处理？？
    @EventHandler(payloadKey="${integration.name}.device.demoSN.demo.groupProperties")
    public DemoDeviceEntities handleDeviceServices(Event<ExchangePayload> event) {
        return null;
    }

}
