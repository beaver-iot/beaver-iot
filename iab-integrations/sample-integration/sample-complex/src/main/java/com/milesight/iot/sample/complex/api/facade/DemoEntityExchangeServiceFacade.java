package com.milesight.iot.sample.complex.api.facade;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * POST /api/v1/device
 * POST /api/v1/entity/meta      {id:,value:}
 * PUT  /api/v1/entity/meta
 * GET  /api/v1/entity/{id}/history
 * GET  /api/v1/entity/{id}/latest
 * POST /api/v1/entity/{id}/data?exchange
 * PUT  /api/v1/entity/{id}/exchange
 *
 * @author leon
 */
@RestController
public interface DemoEntityExchangeServiceFacade {


    //下行
    @GetMapping("/api/v1/entity/update")
    public String exchangeDown(String message);

    //保存实体

    //更新实体
}
