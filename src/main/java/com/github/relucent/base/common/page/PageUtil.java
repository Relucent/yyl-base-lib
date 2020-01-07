package com.github.relucent.base.common.page;

/**
 * 分页查询帮助类，提供一些分页查询需要的计算方法.
 * @author YYL
 * @version 0.1 2012-10-08
 */
public class PageUtil {

    /**
     * 根据当前页第一条记录数和每页最大记录数计算出当前页数
     * @param offset 当前页第一条记录的索引
     * @param limit 每页最大记录数
     * @return 当前页数
     */
    public static long getCurrent(long offset, long limit) {
        return (offset / limit) + 1L;
    }

    /**
     * 计算本页第一条记录的索引
     * @param current 页数
     * @param limit 每页最大记录数
     * @return 本页第一条记录的索引
     */
    public static long getOffset(long current, long limit) {
        if ((current < 1L) || (limit < 1L)) {
            return -1L;
        } else {
            return (current - 1L) * limit;
        }
    }

    /**
     * 计算最大页数
     * @param total 总记录数
     * @param limit 每页最大记录数
     * @return 最大页数
     */
    public static long getPageTotal(long total, long limit) {
        if ((total < 0L) || (limit < 1L)) {
            return -1L;
        } else {
            return ((total - 1L) / limit) + 1L;
        }
    }
}
