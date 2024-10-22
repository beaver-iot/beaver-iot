package com.milesight.iab.entity.controller;

import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.base.response.ResponseBuilder;
import com.milesight.iab.entity.model.request.EntityHistoryQuery;
import com.milesight.iab.entity.model.request.EntityQuery;
import com.milesight.iab.entity.model.request.ServiceCallRequest;
import com.milesight.iab.entity.model.request.UpdatePropertyEntityRequest;
import com.milesight.iab.entity.model.response.EntityHistoryResponse;
import com.milesight.iab.entity.model.response.EntityLatestResponse;
import com.milesight.iab.entity.model.response.EntityResponse;
import com.milesight.iab.entity.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseBody<Page<EntityResponse>> search(@RequestBody EntityQuery entityQuery) {
        Page<EntityResponse> entityResponse = entityService.search(entityQuery);
        return ResponseBuilder.success(entityResponse);
    }

    @PostMapping("/history/search")
    public ResponseBody<Page<EntityHistoryResponse>> historySearch(@RequestBody EntityHistoryQuery entityHistoryQuery) {
        Page<EntityHistoryResponse> entityHistoryResponse = entityService.historySearch(entityHistoryQuery);
        return ResponseBuilder.success(entityHistoryResponse);
    }

    @GetMapping("/{entityId}/status")
    public ResponseBody<EntityLatestResponse> getEntityStatus(@PathVariable("entityId") Long entityId) {
        EntityLatestResponse entityLatestResponse = entityService.getEntityStatus(entityId);
        return ResponseBuilder.success(entityLatestResponse);
    }

    @PostMapping("/property/update")
    public ResponseBody<Void> updatePropertyEntity(@RequestBody UpdatePropertyEntityRequest updatePropertyEntityRequest) {
        entityService.updatePropertyEntity(updatePropertyEntityRequest);
        return ResponseBuilder.success();
    }

    @PostMapping("/service/call")
    public ResponseBody<Void> serviceCall(@RequestBody ServiceCallRequest serviceCallRequest) {
        entityService.serviceCall(serviceCallRequest);
        return ResponseBuilder.success();
    }

}
