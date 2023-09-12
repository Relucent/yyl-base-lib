package com.github.relucent.base.common.jdbc.parser;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.plugin.jsqlparser.CountSqlJsqlParser;

public class CountSqlHelperTest {

    @Test
    public void testCountSqlSimpleParser() {
        CountSqlHelper.setCountSqlParser(CountSqlSimpleParser.INSTANCE);
        String sql = "select id,name,value from user where name like '%admin%' order by id";
        String actual = CountSqlHelper.getCountSql(sql).toLowerCase().trim();
        String expected = "select count(*) from (select id,name,value from user where name like '%admin%' ) table_count_alias__yl";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCountSqlJsqlParser() {
        CountSqlHelper.setCountSqlParser(CountSqlJsqlParser.INSTANCE);
        String sql = "select id,name,value from user where name like '%admin%' order by id";
        String actual = CountSqlHelper.getCountSql(sql).toLowerCase().trim();
        String expected = "select count(*) from user where name like '%admin%'";
        Assert.assertEquals(expected, actual);
    }
}
