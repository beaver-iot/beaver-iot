package com.milesight.iab.demo.repository;

import com.milesight.iab.data.jpa.repository.BaseJpaRepository;
import com.milesight.iab.demo.entity.DeviceDemoEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @author leon
 */
public interface DeviceDemoRepository extends BaseJpaRepository<DeviceDemoEntity, Long> {



}
