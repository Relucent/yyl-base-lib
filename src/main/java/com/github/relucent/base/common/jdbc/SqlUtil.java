package com.github.relucent.base.common.jdbc;

import java.util.regex.Pattern;

import com.github.relucent.base.common.lang.StringUtil;

/**
 * SQL工具类
 */
public class SqlUtil {
    // ==============================Fields===========================================
    private static final Pattern ORDER_BY_PATTERN = Pattern.compile("\\s+ORDER\\s+BY\\s+\\S+(\\s*(ASC|DESC)?)(\\s*,\\s*\\S+(\\s*(ASC|DESC)?))*\\s*$",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected SqlUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 转义字符串中的字符，使其适合传递给SQL查询.
     * 
     * <pre>
     * statement.executeQuery("SELECT * FROM MOVIES WHERE TITLE='" + SqlUtil.escapeSql("McHale's Navy") + "'");
     * </pre>
     * 
     * @param str 需要转义的字符串
     * @return 新的字符串，已经转义
     */
    public static String escapeSql(String value) {
        if (value == null) {
            return null;
        }
        return StringUtil.replace(value, "'", "''");
    }

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
    public static String buildMarkers(int size) {
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
