package com.github.relucent.base.common.jdbc.parser;

import com.github.relucent.base.plugin.jsqlparser.CountSqlJsqlParser;

public class CountSqlHelper {

    private static final CountSqlParser DEFAULT_COUNT_SQL_PARSER;
    static {
        CountSqlParser parser = null;
        if (parser == null) {
            try {
                Class.forName(net.sf.jsqlparser.parser.CCJSqlParserUtil.class.getName());
                parser = new CountSqlJsqlParser();
            } catch (Throwable e) {
                /* Ignore */
            }
        }
        if (parser == null) {
            parser = new CountSqlSimpleParser();
        }
        DEFAULT_COUNT_SQL_PARSER = parser;
    }

    public static String getCountSql(String sql) {
        return DEFAULT_COUNT_SQL_PARSER.getCountSql(sql);
    }
}
