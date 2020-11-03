package com.github.relucent.base.common.jdbc;

import org.junit.Assert;
import org.junit.Test;

public class SqlUtilTest {
    @Test
    public void testGetCountSql() {
        String sql = "select id,name,value from user where name like '%admin%' order by id";
        String actual = SqlUtil.removeOrderByExpression(sql).toLowerCase().trim();
        String expected = "select id,name,value from user where name like '%admin%'";
        Assert.assertEquals(expected, actual);
    }
}
