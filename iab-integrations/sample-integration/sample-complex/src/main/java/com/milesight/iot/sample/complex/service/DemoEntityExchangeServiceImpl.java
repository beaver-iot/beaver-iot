package com.milesight.iot.sample.complex.service;

import com.milesight.iot.sample.complex.api.DemoEntityExchangeService;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author leon
 */
public class DemoEntityExchangeServiceImpl implements DemoEntityExchangeService {

    private ProducerTemplate producerTemplate;
    @Override
    public String exchangeDown(String message) {
        producerTemplate.send("direct:exchangeDown", exchange -> exchange.getIn().setBody(message));
        return null;
    }
}
