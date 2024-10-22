package com.milesight.iab.demo.controller;

import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.page.GenericPageRequest;
import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.base.response.ResponseBuilder;
import com.milesight.iab.demo.entity.DeviceDemoEntity;
import com.milesight.iab.demo.model.DemoQuery;
import com.milesight.iab.demo.service.DeviceDemoService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

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
