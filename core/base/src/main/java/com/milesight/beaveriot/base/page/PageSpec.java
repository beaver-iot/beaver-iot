package com.milesight.beaveriot.base.page;


/**
 * Paging request interface specification
 * @author leon
 */
public interface PageSpec  {

    /**
     * 是否分页
     * @return
     */
    default boolean isPaged() {
        return true;
    }

    /**
     * Whether to count, by default, count statistics will not be performed when the total value is submitted by the current end (applicable to scenarios with large amounts of data)
     * @return
     */
    default boolean isCount(){
        return true;
    }

    /**
     * 起始值
     * @return
     */
    Long getOffset();

    /**
     * 分页limit
     * @return
     */
    Integer getLimit();

    /**
     * 单页数量
     * @return
     */
    Integer getPageSize();

    /**
     * The paging page number starts from 1 (note: Spring pageable starts from 0)
     * @return
     */
    Integer getPageNumber();

    /**
     * 排序信息
     * @return
     */
    Sorts getSort();

    /**
     * 获取total总数
     * @return
     */
    Long getTotal();
}
