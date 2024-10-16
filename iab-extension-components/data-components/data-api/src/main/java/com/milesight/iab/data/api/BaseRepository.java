package com.milesight.iab.data.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author leon
 */
public interface BaseRepository<T,ID extends Serializable> {
    T getOne(ID id);

    <S extends T> S save(S entity);

    Optional<T> findById(ID id);

    void deleteById(ID id);

    void delete(T entity);

    void deleteAllById(Iterable<? extends ID> ids);

    List<T> findAll();

    List<T> findAll(Sort sort);

    Page<T> findAll(Pageable pageable);

}
