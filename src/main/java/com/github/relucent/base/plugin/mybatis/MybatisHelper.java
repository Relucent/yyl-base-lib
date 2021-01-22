package com.github.relucent.base.plugin.mybatis;

import java.util.List;

import com.github.relucent.base.common.page.Pagination;
import com.github.relucent.base.common.page.SimplePage;

/**
 * _Mybatis 分页工具类
 * @author _yyl
 */
public class MybatisHelper {

    /**
     * 分页查询
     * @param <T> 查询的实体类型
     * @param pagination 分页条件
     * @param select 查询方法
     * @return 分页查询结果
     */
    public static <T> SimplePage<T> selectPage(Pagination pagination, Select<T> select) {
        try {
            MybatisPageContext context = MybatisPageContextHolder.getContext();
            context.setOffset(pagination.getOffset());
            context.setLimit(pagination.getLimit());
            context.setTotal(-1);
            context.setCount(true);
            List<T> records = select.get();
            long offset = pagination.getOffset();
            long limit = pagination.getLimit();
            long total = context.getTotal();
            return new SimplePage<T>(offset, limit, records, total);
        } finally {
            MybatisPageContextHolder.clearContext();
        }
    }

    /** 查询方法 */
    public static interface Select<T> {
        List<T> get();
    }
}
