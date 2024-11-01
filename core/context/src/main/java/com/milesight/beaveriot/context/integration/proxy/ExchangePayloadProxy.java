package com.milesight.beaveriot.context.integration.proxy;

import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import lombok.SneakyThrows;

/**
 * @author leon
 */
public class ExchangePayloadProxy extends MapExchangePayloadProxy{
    private final Class<? extends ExchangePayload> parameterTypes;
    private final ExchangePayload exchangePayload;

    public ExchangePayloadProxy(ExchangePayload exchangePayload, Class<? extends ExchangePayload> parameterTypes) {
        super(exchangePayload.getAllPayloads(), parameterTypes);
        this.exchangePayload = exchangePayload;
        this.parameterTypes = parameterTypes;
    }

    @Override
    @SneakyThrows
    protected Object newInstance(Class<? extends ExchangePayload> parameterTypes) {
        ExchangePayload newInstance = parameterTypes.newInstance();
        newInstance.setContext(exchangePayload.getContext());
        newInstance.setTimestamp(exchangePayload.getTimestamp());
        return newInstance;
    }
}