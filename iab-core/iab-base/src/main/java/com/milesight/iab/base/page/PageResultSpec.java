package com.milesight.iab.base.page;

import java.util.List;

/**
 * Pagination response interface specification
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
