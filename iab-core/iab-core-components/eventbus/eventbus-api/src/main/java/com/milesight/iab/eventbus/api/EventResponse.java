package com.milesight.iab.eventbus.api;

import lombok.Getter;

import java.util.LinkedHashMap;

/**
 * @author leon
 */
@Getter
public class EventResponse extends LinkedHashMap<String,Object> {

    public static EventResponse of(String key, Object value){
        EventResponse eventResponse = new EventResponse();
        eventResponse.put(key,value);
        return eventResponse;
    }
}
