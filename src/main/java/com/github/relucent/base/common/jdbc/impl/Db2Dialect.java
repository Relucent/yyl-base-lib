package com.github.relucent.base.common.jdbc.impl;

import com.github.relucent.base.common.jdbc.Dialect;

/**
 * JDBC查询方言DB2实现，主要用于提供分页查询<br>
 */
public class Db2Dialect extends AbstractDialect implements Dialect {

    public static final Db2Dialect INSTANCE = new Db2Dialect();

    @Override
    public String getLimitSql(String sql, long offset, long limit) {
        // 分页参数校验：offset 负数归零，limit 非正抛异常
        if (offset < 0) {
            offset = 0;
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("The limit must be greater than 0");
        }
        int startOfSelect = indexOfKeyword(sql, "select");
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100)//
                .append(sql.substring(0, startOfSelect))// add the comment
                .append("select * from ( select ") // nest the main query in an outer select
                .append(getRowNumber(sql)); // add the rownnumber bit into the outer query select list
        if (hasDistinct(sql)) {
            pagingSelect.append(" row_.* from ( ") // add another (inner) nested select
                    .append(sql.substring(startOfSelect)) // add the main query
                    .append(" ) as row_"); // close off the inner nested select
        } else {
            pagingSelect.append(sql.substring(startOfSelect + 6)); // add the main query
        }
        pagingSelect.append(" ) as temp_ where rownumber_ ");
        // add the restriction to the outer select
        if (offset > 0) {
            pagingSelect.append("between " + (offset + 1) + " and " + (offset + limit));
        } else {
            pagingSelect.append("<= " + limit);
        }
        return pagingSelect.toString();
    }

    private static String getRowNumber(String sql) {
        StringBuilder rownumber = new StringBuilder(50).append("rownumber() over(");
        int orderByIndex = indexOfKeyword(sql, "order by");
        if (orderByIndex > 0 && !hasDistinct(sql)) {
            rownumber.append(sql.substring(orderByIndex));
        }
        rownumber.append(") as rownumber_,");
        return rownumber.toString();
    }

    private static boolean hasDistinct(String sql) {
        return indexOfKeyword(sql, "select distinct") >= 0;
    }

    /**
     * 在字符串字面量（单/双引号）与注释（行注释 -- 与块注释）之外查找关键字， 避免字符串/注释内包含的关键字造成解析错位。要求关键字两端为词边界。
     * @param sql     原始SQL
     * @param keyword 待查找关键字（忽略大小写）
     * @return 关键字起始位置，未找到返回 -1
     */
    private static int indexOfKeyword(String sql, String keyword) {
        String lower = sql.toLowerCase();
        String key = keyword.toLowerCase();
        int n = sql.length();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;
        int i = 0;
        while (i < n) {
            char c = sql.charAt(i);
            // 块注释 /* ... */
            if (!inSingleQuote && !inDoubleQuote && !inLineComment && !inBlockComment && c == '/' && i + 1 < n
                    && sql.charAt(i + 1) == '*') {
                inBlockComment = true;
                i += 2;
                continue;
            }
            if (inBlockComment) {
                if (c == '*' && i + 1 < n && sql.charAt(i + 1) == '/') {
                    inBlockComment = false;
                    i += 2;
                    continue;
                }
                i++;
                continue;
            }
            // 行注释 -- ...
            if (!inSingleQuote && !inDoubleQuote && !inLineComment && c == '-' && i + 1 < n
                    && sql.charAt(i + 1) == '-') {
                inLineComment = true;
                i += 2;
                continue;
            }
            if (inLineComment) {
                if (c == '\n') {
                    inLineComment = false;
                }
                i++;
                continue;
            }
            // 字符串字面量
            if (!inDoubleQuote && c == '\'') {
                inSingleQuote = !inSingleQuote;
                i++;
                continue;
            }
            if (!inSingleQuote && c == '"') {
                inDoubleQuote = !inDoubleQuote;
                i++;
                continue;
            }
            // 引号/注释之外：尝试匹配关键字（带词边界）
            if (!inSingleQuote && !inDoubleQuote && lower.startsWith(key, i)) {
                int end = i + key.length();
                boolean beforeOk = (i == 0) || !isWordChar(lower.charAt(i - 1));
                boolean afterOk = (end >= n) || !isWordChar(lower.charAt(end));
                if (beforeOk && afterOk) {
                    return i;
                }
            }
            i++;
        }
        return -1;
    }

    private static boolean isWordChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_';
    }

    @Override
    public String testQuery() {
        return "SELECT 1 FROM SYSIBM.SYSDUMMY1";
    }
}
