package com.github.relucent.base.util.jdbc.impl;

import com.github.relucent.base.util.jdbc.Dialect;

/**
 * JDBC查询方言实现，主要用于提供分页查询<br>
 * 适用于 PostgreSql, GreenPlum <br>
 */
public class PostgreSqlDialect extends AbstractDialect implements Dialect {

    public static final PostgreSqlDialect INSTANCE = new PostgreSqlDialect();

    @Override
    public String getLimitSql(String sql, long offset, long limit) {
        return sql + " limit " + limit + " offset " + offset;
    }

    @Override
    public String testQuery() {
        return "select version()";
    }
}
