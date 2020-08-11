package com.github.relucent.base.common.jdbc.impl;

import com.github.relucent.base.common.jdbc.Dialect;

/**
 * JDBC查询方言Oracle实现，主要用于提供分页查询<br>
 */
public class OracleDialect extends AbstractDialect implements Dialect {

    public static final OracleDialect INSTANCE = new OracleDialect();

    @Override
    public String getLimitSql(String sql, long offset, long limit) {
        return " " //
                + "SELECT T__X.* FROM  ( " //
                + " SELECT ROWNUM as ROWNO__L, T__Y.* FROM (" + sql + ") T__Y  WHERE ROWNUM <=" + (offset + limit) //
                + ") T__X " //
                + "WHERE T__X.ROWNO__L > " + offset + " ";
    }

    @Override
    public String testQuery() {
        return "select 1 from dual";
    }
}
