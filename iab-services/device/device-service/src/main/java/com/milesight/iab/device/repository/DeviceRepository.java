package com.milesight.iab.device.repository;

import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import com.milesight.iab.device.po.DevicePO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends BaseJpaRepository<DevicePO, Long> {
    public List<DevicePO> findByIdIn(List<Long> ids);

    @Query("SELECT r.integration, COUNT(r) FROM device r WHERE r.integration IN :integrations GROUP BY r.integration")
    List<Object[]> countByIntegrations(List<String> integrations);
}
