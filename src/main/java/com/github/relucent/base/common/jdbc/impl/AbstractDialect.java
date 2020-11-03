package com.github.relucent.base.common.jdbc.impl;

import com.github.relucent.base.common.jdbc.Dialect;
import com.github.relucent.base.common.jdbc.parser.CountSqlHelper;

public abstract class AbstractDialect implements Dialect {
    @Override
    public String getCountSql(String sql) {
        return CountSqlHelper.getCountSql(sql);
    }
}
