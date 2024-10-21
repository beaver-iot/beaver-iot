package com.milesight.iab.device.repository;

import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import com.milesight.iab.device.po.DevicePO;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface DeviceRepository extends BaseJpaRepository<DevicePO, Long> {
    public List<DevicePO> findByIdIn(List<Long> ids);
}
