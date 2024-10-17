package com.milesight.iab.demo.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.page.GenericPageRequest;
import com.milesight.iab.base.page.GenericPageResult;
import com.milesight.iab.base.response.ResponseBody;
import com.milesight.iab.base.response.ResponseBuilder;
import com.milesight.iab.demo.entity.DeviceDemoEntity;
import com.milesight.iab.demo.model.DemoQuery;
import com.milesight.iab.demo.repository.DeviceDemoRepository;
import com.milesight.iab.demo.service.DeviceDemoService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * @author leon
 */
@RestController
@RequestMapping("/device")
@Slf4j
public class DemoController {

    @Autowired
    private DeviceDemoService deviceDemoService;

    @PostMapping("/error")
    public ResponseBody<Page<DeviceDemoEntity>> findAllError(@RequestBody DemoQuery query){
        Page<DeviceDemoEntity> all = deviceDemoService.findAll(query);
        if(new Random(2).nextInt() == 1){
            throw new ServiceException(ErrorCode.DATA_NO_FOUND);
        }else{
            throw ServiceException.with(ErrorCode.DATA_NO_FOUND).detailMessage("eg: No data found").args(query).build();
        }
    }
    @PostMapping("/search")
    public ResponseBody<Page<DeviceDemoEntity>> findAll(@RequestBody DemoQuery query){
        return ResponseBuilder.success(deviceDemoService.findAll(query));
    }

    @PostMapping("/paging")
    public ResponseBody<Page<DeviceDemoEntity>> findAll(@RequestBody GenericPageRequest pageRequest){
        return ResponseBuilder.success(deviceDemoService.paging(pageRequest.toPageable()));
    }

    @GetMapping("/{id}")
    public DeviceDemoEntity getOne(@PathVariable("id") Long id){
        return deviceDemoService.getOne(id);
    }

    @PostMapping("")
    public DeviceDemoEntity save(@RequestBody DeviceDemoEntity entity){
        return deviceDemoService.save(entity);
    }

}
