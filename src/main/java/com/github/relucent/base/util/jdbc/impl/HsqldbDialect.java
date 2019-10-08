package com.github.relucent.base.util.jdbc.impl;

import com.github.relucent.base.util.jdbc.Dialect;

/**
 * JDBC查询方言HSQLDB实现，主要用于提供分页查询<br>
 */
public class HsqldbDialect extends AbstractDialect implements Dialect {

    public static final HsqldbDialect INSTANCE = new HsqldbDialect();

    @Override
    public String getLimitSql(String sql, long offset, long limit) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public String testQuery() {
        return "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";
    }
}
