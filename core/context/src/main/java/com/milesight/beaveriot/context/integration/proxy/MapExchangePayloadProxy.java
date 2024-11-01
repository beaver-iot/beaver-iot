package com.milesight.beaveriot.context.integration.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.milesight.beaveriot.base.utils.JsonUtils;
import com.milesight.beaveriot.context.integration.entity.annotation.AnnotationEntityCache;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import lombok.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author leon
 */
public class MapExchangePayloadProxy<T extends ExchangePayload> {
    private final Class<T> parameterType;
    private final Map<String, ?> allPayloads;

    private static final ConversionService conversionService = ApplicationConversionService.getSharedInstance();

    public <P> MapExchangePayloadProxy(Map<String, P> allPayloads, Class<T> parameterType) {
        this.allPayloads = allPayloads;
        this.parameterType = parameterType;
    }

    public T proxy() {
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(newInstance(parameterType));
        factory.addAdvice((MethodInterceptor) invocation -> {
            Method method = invocation.getMethod();
            if ("toString".equals(method.getName())) {
                return allPayloads.toString();
            } else if ("hashCode".equals(method.getName()) || "equals".equals(method.getName())) {
                return invocation.proceed();
            }
            String cacheEntityKey = AnnotationEntityCache.INSTANCE.getEntityKeyByMethod(method);
            if (allPayloads.containsKey(cacheEntityKey)) {
                return parserValue(allPayloads.get(cacheEntityKey), method.getReturnType());
            }
            return invocation.proceed();
        });
        //noinspection unchecked
        return (T) factory.getProxy();
    }

    protected Object parserValue(Object value, Class<?> returnType) {
        if (!ObjectUtils.isEmpty(value) && value instanceof JsonNode) {
            return JsonUtils.cast(value, returnType);
        }
        return conversionService.convert(value, returnType);
    }

    @SneakyThrows
    protected T newInstance(Class<T> parameterType) {
        return parameterType.getDeclaredConstructor().newInstance();
    }
}
