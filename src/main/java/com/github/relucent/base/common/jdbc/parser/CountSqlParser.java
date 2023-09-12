package com.github.relucent.base.common.jdbc.parser;

/**
 * SQL解析类，构建 COUNT 查询SQL
 */
@FunctionalInterface
public interface CountSqlParser {

    /** 保持 OrderBy注释 （特殊sql不需要去掉order by时，使用注释前缀） */
    String KEEP_ORDERBY = "/*keep orderby*/";

    /**
     * 获取 CountSQL
     * @param sql 原始SQL
     * @return CountSQL
     */
    String getCountSql(String sql);
}
