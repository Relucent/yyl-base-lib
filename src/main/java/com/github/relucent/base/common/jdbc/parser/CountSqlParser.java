package com.github.relucent.base.common.jdbc.parser;

/**
 * SQL解析类，构建 COUNT 查询SQL
 */
public interface CountSqlParser {

    /**
     * 获取 CountSQL
     * @param sql 原始SQL
     * @return CountSQL
     */
    String getCountSql(String sql);
}
