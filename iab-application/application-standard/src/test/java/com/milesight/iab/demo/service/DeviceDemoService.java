package com.milesight.iab.demo.service;

import com.milesight.iab.demo.entity.DeviceDemoEntity;
import com.milesight.iab.demo.model.DemoQuery;
import com.milesight.iab.demo.repository.DeviceDemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<DeviceDemoEntity> findAll(DemoQuery demoQuery) {
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", match -> match.endsWith())  // name like %?0
//                .withMatcher("address", match -> match.startsWith());
//        Example<DemoQuery> demoQueryExample = Example.of(demoQuery);
        return deviceDemoRepository.findAll( demoQuery.toPageable());
    }

    public List<DeviceDemoEntity> findAll() {
        return deviceDemoRepository.findAll();
    }

    public Page<DeviceDemoEntity> paging(Pageable pageable) {
        return deviceDemoRepository.findAll(pageable);
    }

    public DeviceDemoEntity getOne(Long id) {
        return deviceDemoRepository.findById(id).orElseThrow();
    }

    public DeviceDemoEntity save(DeviceDemoEntity entity) {
        return deviceDemoRepository.save(entity);
    }
}
