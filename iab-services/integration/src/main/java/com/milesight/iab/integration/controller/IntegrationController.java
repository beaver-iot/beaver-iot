package com.milesight.iab.integration.controller;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.base.response.ResponseBuilder;
import com.milesight.iab.integration.model.request.SearchIntegrationRequest;
import com.milesight.iab.integration.model.response.IntegrationDetailData;
import com.milesight.iab.integration.model.response.SearchIntegrationResponseData;
import com.milesight.iab.integration.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/integration")
public class IntegrationController {
    @Autowired
    IntegrationService integrationService;

    @PostMapping("/search")
    public ResponseBody<List<SearchIntegrationResponseData>> searchIntegration(@RequestBody SearchIntegrationRequest searchDeviceRequest) {
        return ResponseBuilder.success(integrationService.searchIntegration(searchDeviceRequest));
    }

    @GetMapping("/{integrationId}")
    public ResponseBody<IntegrationDetailData> getIntegrationDetail(@PathVariable("integrationId") String integrationId) {
        return ResponseBuilder.success(integrationService.getDetailData(integrationId));
    }
}
