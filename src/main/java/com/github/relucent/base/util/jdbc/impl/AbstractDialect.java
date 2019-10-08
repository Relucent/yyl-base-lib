package com.github.relucent.base.util.jdbc.impl;

import com.github.relucent.base.util.jdbc.Dialect;
import com.github.relucent.base.util.jdbc.SqlUtil;

public abstract class AbstractDialect implements Dialect {
    @Override
    public String getCountSql(String sql) {
        return "SELECT COUNT(*) AS COUNT___Y FROM (" + SqlUtil.removeOrderByExpression(sql) + ") T___Y ";
    }
}
