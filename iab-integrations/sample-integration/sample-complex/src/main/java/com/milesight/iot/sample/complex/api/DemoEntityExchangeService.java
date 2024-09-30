package com.milesight.iot.sample.complex.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leon
 */
public interface DemoEntityExchangeService {

    public String exchangeDown(String message);

}
