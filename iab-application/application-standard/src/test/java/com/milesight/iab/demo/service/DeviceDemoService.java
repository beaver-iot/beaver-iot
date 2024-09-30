package com.milesight.iab.demo.service;

import com.milesight.iab.demo.entity.DeviceDemoEntity;
import com.milesight.iab.demo.repository.DeviceDemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author leon
 */
@Service
public class DeviceDemoService {

    @Autowired
    private DeviceDemoRepository deviceDemoRepository;

    public List<DeviceDemoEntity> findAll() {
        return deviceDemoRepository.findAll();
    }

    public DeviceDemoEntity getOne(Long id) {
        return deviceDemoRepository.findById(id).orElseThrow();
    }

    public DeviceDemoEntity save(DeviceDemoEntity entity) {
        return deviceDemoRepository.save(entity);
    }
}
