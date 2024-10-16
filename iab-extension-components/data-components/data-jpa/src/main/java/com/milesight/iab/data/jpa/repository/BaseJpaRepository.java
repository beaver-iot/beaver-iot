package com.milesight.iab.data.jpa.repository;

import com.milesight.iab.data.api.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author leon
 */
@NoRepositoryBean
public interface BaseJpaRepository<T,ID extends Serializable> extends JpaRepository<T,ID>, BaseRepository<T,ID> {

    @Override
    T getOne(ID id);

    @Override
    <S extends T> S save(S entity);

    @Override
    Optional<T> findById(ID id);

    @Override
    void deleteById(ID id);

    @Override
    void delete(T entity);

    @Override
    void deleteAllById(Iterable<? extends ID> ids);

    @Override
    List<T> findAll();

    @Override
    List<T> findAll(Sort sort);

    @Override
    Page<T> findAll(Pageable pageable);

}
