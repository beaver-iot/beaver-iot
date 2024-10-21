package com.milesight.iab.data.jpa.repository;

import com.milesight.iab.data.api.BaseRepository;
import com.milesight.iab.data.filterable.Filterable;
import com.milesight.iab.data.jpa.support.SpecificationConverter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author leon
 */
@NoRepositoryBean
public interface BaseJpaRepository<T,ID extends Serializable> extends JpaRepository<T,ID>, BaseRepository<T,ID> , JpaSpecificationExecutor {

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
    <S extends T> List<S> saveAll(Iterable<S> entities);

    @Override
    List<T> findAll();

    @Override
    List<T> findAll(Sort sort);

    @Override
    Page<T> findAll(Pageable pageable);


    @Override
    default List<T> findAll(Consumer<Filterable> consumer){
        return findAll(SpecificationConverter.toSpecification(consumer));
    }

    @Override
    default Page<T> findAll(Consumer<Filterable> filterable, Pageable pageable){
        return findAll(SpecificationConverter.toSpecification(filterable), pageable);
    }

    @Override
    default T findUniqueOne(Consumer<Filterable> filterable){
        return findOne(filterable).orElseThrow(() -> new EmptyResultDataAccessException(1));
    }

    @Override
    default Optional<T> findOne(Consumer<Filterable> filterable){
        List<T> all = findAll(SpecificationConverter.toSpecification(filterable));
        return CollectionUtils.isEmpty(all) ? Optional.empty() : Optional.of(all.get(0));
    }

}
