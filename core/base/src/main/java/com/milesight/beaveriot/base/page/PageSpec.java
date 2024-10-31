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
     * 是否count, 默认当前端有提交total值时则不进行count统计（适用于数据量较大的场景）
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
     * 分页页号, 从1开始算(注意：Spring pageable从0开始)
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
