package com.github.relucent.base.common.jdbc.parser;

import org.junit.Assert;
import org.junit.Test;

public class CountSqlHelperTest {
    @Test
    public void testGetCountSql() {
        String sql = "select id,name,value from user where name like '%admin%' order by id";
        String actual = CountSqlHelper.getCountSql(sql).toLowerCase().trim();
        String expected = "select count(*) from user where name like '%admin%'";
        Assert.assertEquals(expected, actual);
    }
}
