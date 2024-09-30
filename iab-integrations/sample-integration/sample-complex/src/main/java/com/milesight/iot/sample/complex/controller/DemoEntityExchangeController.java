package com.milesight.iot.sample.complex.controller;

import com.milesight.iot.sample.complex.api.DemoEntityExchangeService;
import com.milesight.iot.sample.complex.api.facade.DemoEntityExchangeServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author leon
 */
public class DemoEntityExchangeController implements DemoEntityExchangeServiceFacade {

    private DemoEntityExchangeService entityExchangeService;
    @Override
    public String exchangeDown(String message) {
        return entityExchangeService.exchangeDown(message);
    }
}
