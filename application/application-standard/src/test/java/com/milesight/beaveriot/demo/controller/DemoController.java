package com.milesight.beaveriot.demo.controller;

import com.milesight.beaveriot.base.enums.ErrorCode;
import com.milesight.beaveriot.base.exception.ServiceException;
import com.milesight.beaveriot.base.page.GenericPageRequest;
import com.milesight.beaveriot.base.response.ResponseBody;
import com.milesight.beaveriot.base.response.ResponseBuilder;
import com.milesight.beaveriot.demo.entity.DeviceDemoEntity;
import com.milesight.beaveriot.demo.model.DemoQuery;
import com.milesight.beaveriot.demo.service.DeviceDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author leon
 */
@RestController
@RequestMapping("/demo-device")
@Slf4j
public class DemoController {

    @Autowired
    private DeviceDemoService deviceDemoService;

    @PostMapping("/findSpec")
    public ResponseBody<Page<DeviceDemoEntity>> findAllError(@RequestBody DemoQuery demoQuery){

        if(!StringUtils.hasText(demoQuery.getName())){
            ServiceException.with(ErrorCode.PARAMETER_VALIDATION_FAILED).build();
//            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("eg: No data found").args(query).build();
            throw new ServiceException(ErrorCode.DATA_NO_FOUND);
        }

        return ResponseBuilder.success(deviceDemoService.findAllBySpec(demoQuery));
    }
    @PostMapping("/search")
    public ResponseBody<Page<DeviceDemoEntity>> search(@RequestBody DemoQuery query){
        return ResponseBuilder.success(deviceDemoService.findAll(query));
    }

    @PostMapping("/paging")
    public ResponseBody<Page<DeviceDemoEntity>> paging(@RequestBody GenericPageRequest pageRequest){
        return ResponseBuilder.success(deviceDemoService.paging(pageRequest.toPageable()));
    }

    @GetMapping("/{id}")
    public ResponseBody<DeviceDemoEntity> getOne(@PathVariable("id") Long id){
        return ResponseBuilder.success(deviceDemoService.getOne(id));
    }

    @PostMapping("")
    public ResponseBody<DeviceDemoEntity> save(@RequestBody DeviceDemoEntity entity){
        return ResponseBuilder.success(deviceDemoService.save(entity));
    }

}
