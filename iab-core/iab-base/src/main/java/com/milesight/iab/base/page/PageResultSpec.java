package com.milesight.iab.base.page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 分页响应接口规范
 * 为兼容Spring Pageable/Page接口， 此规范实现Spring Page接口。 具体不需要JSON序列化的属性可通过@JsonIgnore设置，详见{@link GenericPageResult}
 * @author leon
 */
public interface PageResultSpec<T>  {

    /**
     * 单页数量
     * @return
     */
    Integer getPageSize();

    /**
     * 分页页号, 从1开始算
     * @return
     */
    Integer getPageNumber();

    /**
     * 分页总数
     * @return
     */
    Long getTotal();

    /**
     * 返回集合
     * @return
     */
    List<T> getContent();

    /**
     * 总页数
     * @return
     */
    default int getTotalPages() {
        return getPageSize() == 0 ? 0 : (int)Math.ceil((double)getTotal() / (double)getPageSize());
    }

}
