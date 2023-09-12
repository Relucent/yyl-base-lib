package com.github.relucent.base.common.jdbc.parser;

import com.github.relucent.base.common.jdbc.SqlUtil;
import com.github.relucent.base.common.lang.CharSequenceUtil;

public class CountSqlSimpleParser implements CountSqlParser {

    /** SQL COUNT 处理器实例 */
    public static final CountSqlSimpleParser INSTANCE = new CountSqlSimpleParser();

    /**
     * 获取查总数的SQL
     * @param sql 原始SQL
     * @return 分页 COUNT SQL
     */
    public String getCountSql(String sql) {

        // 特殊SQL不需要去掉order by时，使用注释前缀
        if (CharSequenceUtil.startWithIgnoreCase(sql, KEEP_ORDERBY)) {
            return getSimpleCountSql(sql);
        }

        // 去掉 Order By表达式
        return "SELECT COUNT(*) FROM (" + SqlUtil.removeOrderByExpression(sql) + ") TABLE_COUNT_ALIAS__YL";
    }

    /**
     * 获取 COUNT SQL
     * @param sql 原始SQL
     * @return COUNT SQL
     */
    protected String getSimpleCountSql(String sql) {
        return "SELECT COUNT(*) FROM (" + sql + ") TABLE_COUNT_ALIAS__YL";
    }
}
