package com.milesight.iab.eventbus;

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
        return matchEventType(eventType, requestEventType) && matchPayloadKeyExpression(payloadKey.trim(), requestPayloadKey.trim());
    }

    private boolean matchPayloadKeyExpression(String payloadKeyPattern, String payloadKey) {
        if(payloadKeyPattern.equals(payloadKey)){
            return true;
        }
        return matchPattern(payloadKeyPattern, payloadKey);
    }

    private boolean matchPattern(String pattern, String str) {
        return matchPattern(pattern, str, 0, 0);
    }

    private boolean matchPattern(String pattern, String str, int pIndex, int sIndex) {
        // If the pattern has been processed, check if the string has also been processed
        if (pIndex == pattern.length()) {
            return sIndex == str.length();
        }
        // If the string is processed but the pattern is not, check to see if only * is left in the pattern
        if (sIndex == str.length()) {
            return allStars(pattern, pIndex);
        }

        if (pattern.charAt(pIndex) == '*' || pattern.charAt(pIndex) == str.charAt(sIndex)) {
            if (pattern.charAt(pIndex) == '*') {
                return matchPattern(pattern, str, pIndex + 1, sIndex) || matchPattern(pattern, str, pIndex, sIndex + 1);
            } else {
                return matchPattern(pattern, str, pIndex + 1, sIndex + 1);
            }
        }
        return false;
    }

    private boolean allStars(String pattern, int pIndex) {
        for (int i = pIndex; i < pattern.length(); i++) {
            if (pattern.charAt(i) != '*') {
                return false;
            }
        }
        return true;
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
