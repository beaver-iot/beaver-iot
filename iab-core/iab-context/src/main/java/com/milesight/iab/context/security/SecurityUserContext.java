package com.milesight.iab.context.security;

import lombok.Builder;

import java.util.Map;

/**
 * @author leon
 */
public class SecurityUserContext {

    private static final ThreadLocal<SecurityUser> securityUserThreadLocal = new ThreadLocal<>();

    public static SecurityUser getSecurityUser() {
        return securityUserThreadLocal.get();
    }

    public static void setSecurityUser(SecurityUser securityUser) {
        securityUserThreadLocal.set(securityUser);
    }

    public static void clear() {
        securityUserThreadLocal.remove();
    }

    @Builder
    public static class SecurityUser {
        private String accessToken;
        private Map<String, Object> header;
        private Map<String, Object> payload;
    }

}
