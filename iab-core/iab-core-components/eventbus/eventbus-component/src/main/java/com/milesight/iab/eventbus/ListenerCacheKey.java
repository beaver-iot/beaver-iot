package com.milesight.iab.eventbus;

import com.milesight.iab.eventbus.support.KeyPatternMatcher;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author leon
 */
@Data
public class ListenerCacheKey {

    private String payloadKey;

    private String eventType;

    public ListenerCacheKey(String payloadKey, String eventType) {
        this.payloadKey = payloadKey;
        this.eventType = eventType;
    }

    public boolean expressionMatch(String requestPayloadKey, String requestEventType) {
        return matchEventType(eventType, requestEventType) && KeyPatternMatcher.match(payloadKey.trim(), requestPayloadKey.trim());
    }

    private boolean matchEventType(String eventTypeConfig, String requestEventType) {
        if (!StringUtils.hasText(eventTypeConfig)) {
            return true;
        }
        return eventTypeConfig.equals(requestEventType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListenerCacheKey that = (ListenerCacheKey) o;
        return Objects.equals(payloadKey, that.payloadKey) && Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payloadKey, eventType);
    }

}
