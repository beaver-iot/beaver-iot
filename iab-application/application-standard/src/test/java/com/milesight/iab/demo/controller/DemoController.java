package com.milesight.iab.demo.controller;

import com.milesight.iab.demo.entity.DeviceDemoEntity;
import com.milesight.iab.demo.repository.DeviceDemoRepository;
import com.milesight.iab.demo.service.DeviceDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author leon
 */
@RestController
@RequestMapping("/device")
@Slf4j
public class DemoController {

    @Autowired
    private DeviceDemoService deviceDemoService;

    @GetMapping("/search")
    public List<DeviceDemoEntity> findAll(){
        return deviceDemoService.findAll();
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
