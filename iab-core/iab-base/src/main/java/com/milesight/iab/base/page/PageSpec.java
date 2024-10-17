package com.milesight.iab.base.page;


/**
 * 分页请求接口规范
 * todo: 待确认
 * 1）先前sort是否会由前端传递构建？
 * 2）是否前端传递query查询条件，条件规则为fq打头，如?fqXm=abc&
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
