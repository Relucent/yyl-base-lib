package com.github.relucent.base.util.jdbc;

import java.sql.Connection;

/**
 * JDBC方言委派类
 * @author YYL
 */
public class DelegatingDialect implements Dialect {

    /** JDBC方言线程变量 */
    private final ThreadLocal<Dialect> dialectHolder = new ThreadLocal<>();

    /**
     * 根据数据库连接切换相应的数据库方言
     * @param conn 数据库连接
     */
    public void route(Connection conn) {
        try {
            Dialect dialect = DialectRouteUtil.getDialect(conn);
            dialectHolder.set(dialect);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        dialectHolder.remove();
    }

    @Override
    public String getLimitSql(String sql, long start, long limit) {
        return dialectHolder.get().getLimitSql(sql, start, limit);
    }

    @Override
    public String getCountSql(String sql) {
        return dialectHolder.get().getCountSql(sql);
    }

    @Override
    public String testQuery() {
        return dialectHolder.get().testQuery();
    }
}
