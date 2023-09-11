package com.github.relucent.base.common.jdbc.parser;

import java.util.concurrent.atomic.AtomicReference;

public class CountSqlHelper {

    private static final AtomicReference<CountSqlParser> COUNT_SQL_PARSER = new AtomicReference<>();

    public static String getCountSql(String sql) {
        return getCountSqlParser().getCountSql(sql);
    }

    public static void setCountSqlParser(CountSqlParser parser) {
        COUNT_SQL_PARSER.set(parser);
    }

    private static CountSqlParser getCountSqlParser() {
        CountSqlParser parser = COUNT_SQL_PARSER.get();
        return parser == null ? CountSqlSimpleParser.INSTANCE : parser;
    }
}
