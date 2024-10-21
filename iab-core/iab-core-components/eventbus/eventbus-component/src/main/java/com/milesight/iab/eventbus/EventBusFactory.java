package com.milesight.iab.eventbus;


/**
 * @author leon
 */
public class EventBusFactory {

    public static EventBus disruptor(){
        return DisruptorEventBus.getInstance();
    }

}
