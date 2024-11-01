package com.milesight.beaveriot.context.integration.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.milesight.beaveriot.base.utils.JsonUtils;
import com.milesight.beaveriot.context.integration.entity.annotation.AnnotationEntityCache;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import lombok.SneakyThrows;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author leon
 */
public class MapExchangePayloadProxy {
    private final Class<? extends ExchangePayload> parameterTypes;
    private final Map<String,?> allPayloads;

    public MapExchangePayloadProxy(Map<String, ?> allPayloads, Class<? extends ExchangePayload> parameterTypes) {
        this.allPayloads = allPayloads;
        this.parameterTypes = parameterTypes;
    }

    public ExchangePayload proxy() {
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(newInstance(parameterTypes));
        factory.addAdvice((MethodInterceptor) invocation -> {
            Method method = invocation.getMethod();
            if ("toString".equals(method.getName())) {
                return allPayloads.toString();
            }else if("hashCode".equals(method.getName()) || "equals".equals(method.getName())){
                return invocation.proceed();
            }
            String cacheEntityKey = AnnotationEntityCache.INSTANCE.getEntityKeyByMethod(method);
            if(allPayloads.containsKey(cacheEntityKey)){
                return parserValue(allPayloads.get(cacheEntityKey), method.getReturnType());
            }
            return invocation.proceed();
        });
        return (ExchangePayload) factory.getProxy();
    }

    protected Object parserValue(Object value, Class<?> returnType) {
        if (!ObjectUtils.isEmpty(value) && value instanceof JsonNode) {
            return JsonUtils.cast(value, returnType);
        }
        return value;
    }

    @SneakyThrows
    protected Object newInstance(Class<? extends ExchangePayload> parameterTypes) {
        ExchangePayload newInstance = parameterTypes.newInstance();
        return newInstance;
    }
}