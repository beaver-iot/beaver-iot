package com.milesight.iab.entity.controller;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.base.response.ResponseBuilder;
import com.milesight.iab.entity.model.request.CallServiceRequest;
import com.milesight.iab.entity.model.request.EntityHistoryQuery;
import com.milesight.iab.entity.model.request.EntityQuery;
import com.milesight.iab.entity.model.request.UpdateEntityRequest;
import com.milesight.iab.entity.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author loong
 * @date 2024/10/16 14:21
 */
@RestController
@RequestMapping("/entity")
public class EntityController {

    @Autowired
    EntityService entityService;

    @PostMapping("/search")
    public ResponseBody search(@RequestBody EntityQuery entityQuery) {
        return entityService.search(entityQuery);
    }

    @PostMapping("/history/search")
    public ResponseBody historySearch(@RequestBody EntityHistoryQuery entityHistoryQuery) {
        entityService.historySearch(entityHistoryQuery);
        return ResponseBuilder.success();
    }

    @GetMapping("/{entityId}/status")
    public ResponseBody getEntityStatus(@PathVariable("entityId") Long entityId) {
        entityService.getEntityStatus(entityId);
        return ResponseBuilder.success();
    }

    @PostMapping("/update")
    public ResponseBody updateEntity(@RequestBody UpdateEntityRequest updateEntityRequest) {
        entityService.updateEntity(updateEntityRequest);
        return ResponseBuilder.success();
    }

    @PostMapping("/call-service")
    public ResponseBody callService(@RequestBody CallServiceRequest callServiceRequest) {
        entityService.callService(callServiceRequest);
        return ResponseBuilder.success();
    }

}
