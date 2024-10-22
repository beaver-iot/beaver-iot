package com.milesight.iab.eventbus.subscribe;


import com.milesight.iab.eventbus.EventBus;

/**
 * @author leon
 */
public class EventBusFactory {

    public static EventBus disruptor(){
        return DisruptorEventBus.getInstance();
    }

}
