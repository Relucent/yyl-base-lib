package com.github.relucent.base.common.jdbc;

import java.util.regex.Pattern;

import com.github.relucent.base.common.constant.StringConstant;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * SQL工具类
 */
public class SqlUtil {
    // ==============================Fields===========================================
    private static final Pattern ORDER_BY_PATTERN = Pattern.compile(//
            "order\\s+by\\s+[^,\\s]+(\\s+asc|\\s+desc)?(\\s*,\\s*[^,\\s]+(\\s+asc|\\s+desc)?)*", //
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE//
    );

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
     * @param value 需要转义的字符串
     * @return 新的字符串，已经转义
     */
    public static String escapeSql(String value) {
        if (value == null) {
            return null;
        }
        return StringUtil.replace(value, "'", "''");
    }

    /**
     * 移除表达式的排序语句
     * @param sql 查询表达式
     * @return 去除排序语句的查询表达式
     */
    public static String removeOrderByExpression(String sql) {
        if (sql == null) {
            return null;
        }
        // 跳过单引号/双引号字符串字面量以及 -- 与 /* */ 注释内的内容，
        // 避免字符串/注释中出现的 "order by" 被误删导致 SQL 错位。
        StringBuilder result = new StringBuilder(sql.length());
        StringBuilder outside = new StringBuilder();
        int length = sql.length();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;
        int i = 0;
        while (i < length) {
            char c = sql.charAt(i);
            // 块注释 /* ... */
            if (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBlockComment
                    && c == '/' && i + 1 < length && sql.charAt(i + 1) == '*') {
                flushOutside(result, outside);
                inBlockComment = true;
                result.append(c);
                i++;
                continue;
            }
            if (inBlockComment) {
                result.append(c);
                if (c == '*' && i + 1 < length && sql.charAt(i + 1) == '/') {
                    inBlockComment = false;
                    result.append('/');
                    i += 2;
                    continue;
                }
                i++;
                continue;
            }
            // 行注释 -- ...
            if (!inSingleQuote && !inDoubleQuote && !inLineComment
                    && c == '-' && i + 1 < length && sql.charAt(i + 1) == '-') {
                flushOutside(result, outside);
                inLineComment = true;
                result.append(c);
                i++;
                continue;
            }
            if (inLineComment) {
                result.append(c);
                if (c == '\n') {
                    inLineComment = false;
                }
                i++;
                continue;
            }
            // 单引号字符串字面量（SQL 中以 '' 转义单引号）
            if (!inDoubleQuote && c == '\'') {
                if (inSingleQuote && i + 1 < length && sql.charAt(i + 1) == '\'') {
                    // 字符串内的转义引号，原样保留并保持在字符串内
                    result.append("''");
                    i += 2;
                    continue;
                }
                flushOutside(result, outside);
                inSingleQuote = !inSingleQuote;
                result.append(c);
                i++;
                continue;
            }
            // 双引号字符串字面量（SQL 中以 "" 转义双引号）
            if (!inSingleQuote && c == '"') {
                if (inDoubleQuote && i + 1 < length && sql.charAt(i + 1) == '"') {
                    result.append("\"\"");
                    i += 2;
                    continue;
                }
                flushOutside(result, outside);
                inDoubleQuote = !inDoubleQuote;
                result.append(c);
                i++;
                continue;
            }
            // 引号/注释之内的内容原样保留，不做 order by 移除
            if (inSingleQuote || inDoubleQuote || inBlockComment || inLineComment) {
                result.append(c);
                i++;
                continue;
            }
            // 引号/注释之外的内容，按段收集后再做 order by 移除
            outside.append(c);
            i++;
        }
        flushOutside(result, outside);
        return result.toString();
    }

    /**
     * 将引号/注释之外收集到的文本做 order by 移除后追加到结果中
     * @param result 结果构建器
     * @param outside 引号/注释之外的文本段
     */
    private static void flushOutside(StringBuilder result, StringBuilder outside) {
        if (outside.length() == 0) {
            return;
        }
        result.append(ORDER_BY_PATTERN.matcher(outside.toString()).replaceAll(StringConstant.EMPTY));
        outside.setLength(0);
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
