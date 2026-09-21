package com.github.relucent.base.common.jdbc;

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
        return requireDialect().getLimitSql(sql, start, limit);
    }

    @Override
    public String getCountSql(String sql) {
        return requireDialect().getCountSql(sql);
    }

    @Override
    public String testQuery() {
        return requireDialect().testQuery();
    }

    /**
     * 获取当前线程已路由的方言，未初始化时抛出明确异常而非 NPE
     * @return 方言实例
     */
    private Dialect requireDialect() {
        Dialect dialect = dialectHolder.get();
        if (dialect == null) {
            throw new IllegalStateException("Dialect not initialized, please call route(...)");
        }
        return dialect;
    }
}
