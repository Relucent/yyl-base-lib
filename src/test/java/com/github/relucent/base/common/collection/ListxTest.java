package com.github.relucent.base.common.collection;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.collection.Listx;

public class ListxTest {

    @Test
    public void testList() {
        Date now = new Date();// sample
        Listx sample = new Listx();
        sample.add(Integer.MAX_VALUE);// 0
        sample.add(Long.MAX_VALUE);// 1
        sample.add(Boolean.TRUE);// 2
        sample.add("string");// 3
        sample.add(now);// 4
        sample.add(null);// 5
        Assert.assertEquals(sample.getInteger(0), Integer.valueOf(Integer.MAX_VALUE));
        Assert.assertEquals(sample.getLong(1), Long.valueOf(Long.MAX_VALUE));
        Assert.assertEquals(sample.getBoolean(2), Boolean.TRUE);
        Assert.assertEquals(sample.getString(3), "string");
        Assert.assertEquals(sample.getDate(4), now);
        Assert.assertEquals(sample.getString(5), null);
        Assert.assertEquals(sample.getString(5, "DEFAULT"), "DEFAULT");
    }
}
