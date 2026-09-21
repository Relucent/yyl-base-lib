package com.github.relucent.base.common.jdbc.impl;

import com.github.relucent.base.common.jdbc.Dialect;

/**
 * JDBC查询方言mysql实现，主要用于提供分页查询<br>
 */
public class MySqlDialect extends AbstractDialect implements Dialect {

    public static final MySqlDialect INSTANCE = new MySqlDialect();

    @Override
    public String getLimitSql(String sql, long start, long limit) {
        // 分页参数校验：offset 负数归零，limit 非正抛异常
        if (start < 0) {
            start = 0;
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("The limit must be greater than 0");
        }
        return sql + " limit " + start + "," + limit;
    }

    @Override
    public String testQuery() {
        return "select 1";
    }
}
