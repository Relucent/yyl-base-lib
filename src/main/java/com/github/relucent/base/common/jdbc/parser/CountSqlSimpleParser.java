package com.github.relucent.base.common.jdbc.parser;

import com.github.relucent.base.common.jdbc.SqlUtil;

public class CountSqlSimpleParser implements CountSqlParser {

    public static final CountSqlSimpleParser INSTANCE = new CountSqlSimpleParser();

    @Override
    public String getCountSql(String sql) {
        return "SELECT COUNT(*) AS COUNT___Y FROM (" + SqlUtil.removeOrderByExpression(sql) + ") T___Y ";
    }
}
