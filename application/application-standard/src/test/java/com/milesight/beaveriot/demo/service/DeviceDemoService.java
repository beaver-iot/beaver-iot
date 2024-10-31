package com.milesight.beaveriot.demo.service;

import com.milesight.beaveriot.demo.entity.DeviceDemoEntity;
import com.milesight.beaveriot.demo.model.DemoQuery;
import com.milesight.beaveriot.demo.repository.DeviceDemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author leon
 */
@Service
public class DeviceDemoService<T> {

    @Autowired
    private DeviceDemoRepository deviceDemoRepository;

    public Page<DeviceDemoEntity> findAllBySpec(DemoQuery demoQuery) {

        //分页查询
        Page<DeviceDemoEntity> all = deviceDemoRepository.findAll(f -> f.eq(DeviceDemoEntity.Fields.name, demoQuery.getName())
                .like(DeviceDemoEntity.Fields.key, demoQuery.getKey())
                .between(DeviceDemoEntity.Fields.id, 1,2)
                .in(DeviceDemoEntity.Fields.name, new String[]{"a","b"})
                .or(f2 -> f2
                        .like(DeviceDemoEntity.Fields.name, demoQuery.getName())
                        .eq(DeviceDemoEntity.Fields.key, demoQuery.getKey())),
                demoQuery.toPageable());

        //唯一值查询（不可空）
        DeviceDemoEntity uniqueOne = deviceDemoRepository.findUniqueOne(f -> f.eq(DeviceDemoEntity.Fields.name, demoQuery.getName()));
        //唯一值查询（可空）
        Optional<DeviceDemoEntity> one = deviceDemoRepository.findOne(f -> f.eq(DeviceDemoEntity.Fields.name, demoQuery.getName()));
        //列表查询
        List<DeviceDemoEntity> all1 = deviceDemoRepository.findAll(f -> f.eq(DeviceDemoEntity.Fields.name, demoQuery.getName()));

        return all;
    }

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
