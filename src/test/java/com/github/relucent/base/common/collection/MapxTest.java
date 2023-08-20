package com.github.relucent.base.common.collection;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.collection.Mapx;

public class MapxTest {

    @Test
    public void testMap() {
        Date now = new Date();
        Mapx sample = new Mapx();
        sample.put("int", Integer.MAX_VALUE);
        sample.put("long", Long.MAX_VALUE);
        sample.put("boolean-true", Boolean.TRUE);
        sample.put("boolean-false", Boolean.FALSE);
        sample.put("string", "hello");
        sample.put("date-now", now);
        sample.put("date-string", now.toString());
        sample.put(null, "NULL_STRING");
        Assert.assertEquals(sample.getInteger("int"), Integer.valueOf(Integer.MAX_VALUE));
        Assert.assertEquals(sample.getLong("long"), Long.valueOf(Long.MAX_VALUE));
        Assert.assertEquals(sample.getBoolean("boolean-true"), Boolean.TRUE);
        Assert.assertEquals(sample.getBoolean("boolean-false"), Boolean.FALSE);
        Assert.assertEquals(sample.getString("string"), "hello");
        Assert.assertEquals(sample.getString("nonexistent", "Default"), "Default");
        Assert.assertEquals(sample.getDate("date-now"), now);
        
        System.out.println(sample.getString(null));
        Assert.assertEquals(sample.getString(null), "NULL_STRING");
    }
}
