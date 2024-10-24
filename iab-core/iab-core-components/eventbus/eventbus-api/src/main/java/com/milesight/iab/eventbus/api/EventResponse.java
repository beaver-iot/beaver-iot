package com.milesight.iab.eventbus.api;

import lombok.Getter;

/**
 * @author leon
 */
@Getter
public class EventResponse {

    private String key;
    private Object value;

    private EventResponse(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public static EventResponse of(String key, Object value){
       return new EventResponse(key,value);
    }
}
