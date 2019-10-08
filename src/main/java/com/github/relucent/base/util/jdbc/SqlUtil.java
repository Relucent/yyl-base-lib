package com.github.relucent.base.util.jdbc;

import java.util.regex.Pattern;

/**
 * SQL工具类
 */
public class SqlUtil {
    // ==============================Fields===========================================
    private static final Pattern ORDER_BY_PATTERN =
            Pattern.compile("\\s+ORDER\\s+BY\\s+\\S+(\\s*(ASC|DESC)?)(\\s*,\\s*\\S+(\\s*(ASC|DESC)?))*\\s*$",
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    // ==============================Methods==========================================
    /**
     * 移除表达式末尾的排序语句
     * @param sql 查询表达式
     * @return 去除末尾排序语句的查询表达式
     */
    public static String removeOrderByExpression(String sql) {
        return ORDER_BY_PATTERN.matcher(sql).replaceAll(" ");
    }

    /**
     * 构建参数占位符
     * @param size 占位符个数
     * @return 占位符字符串
     */
    protected static String buildParameterMarkers(int size) {
        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        return sql.toString();
    }
}
