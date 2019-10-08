package com.github.relucent.base.util.jdbc.impl;

import com.github.relucent.base.util.jdbc.Dialect;

/**
 * JDBC查询方言Informix实现，主要用于提供分页查询<br>
 */
public class InformixDialect extends AbstractDialect implements Dialect {

    public static final InformixDialect INSTANCE = new InformixDialect();

    // SELECT SKIP M FIRST N FROM TABLENAME WHERE 1=1 ORDER BY COL;
    @Override
    public String getLimitSql(String sql, long offset, long limit) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 40);
        sqlBuilder.append("SELECT ");
        if (offset > 0) {
            sqlBuilder.append(" SKIP ");
            sqlBuilder.append(offset);
        }
        if (limit > 0) {
            sqlBuilder.append(" FIRST ");
            sqlBuilder.append(limit);
        }
        sqlBuilder.append(" * FROM ( ");
        sqlBuilder.append(sql);
        sqlBuilder.append(" ) TEMP_Y_TABLE");
        return sqlBuilder.toString();
    }

    @Override
    public String testQuery() {
        return "select count(*) from systables";
    }
}
