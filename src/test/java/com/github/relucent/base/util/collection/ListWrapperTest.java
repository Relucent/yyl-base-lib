package com.github.relucent.base.util.collection;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ListWrapperTest {

    private ListWrapper<String> sample;

    @Before
    public void before() {
        List<String> origin = new ArrayList<>();
        origin.add("A");// 0
        origin.add("B");// 1
        origin.add("C");// 2
        sample = new ListWrapper<>(origin);
    }

    @Test
    public void testAdd() {
        sample.add(3, "D");
        sample.add(2, "C-");
        sample.add(5, "V");
        Assert.assertEquals(sample.get(0), "A");
        Assert.assertEquals(sample.get(1), "B");
        Assert.assertEquals(sample.get(2), "C-");
        Assert.assertEquals(sample.get(3), "C");
        Assert.assertEquals(sample.get(4), "D");
        Assert.assertEquals(sample.get(5), "V");
        Assert.assertEquals(sample.size(), 6);
    }

    @Test
    public void testSet() {
        sample.set(2, "C=");
        sample.set(5, "V");
        Assert.assertEquals(sample.get(0), "A");
        Assert.assertEquals(sample.get(1), "B");
        Assert.assertEquals(sample.get(2), "C=");
        Assert.assertEquals(sample.get(3), null);
        Assert.assertEquals(sample.get(4), null);
        Assert.assertEquals(sample.get(5), "V");
        Assert.assertEquals(sample.size(), 6);
    }

    @Test
    public void testRemove() {
        Assert.assertEquals(sample.remove(1), "B");
        Assert.assertEquals(sample.remove(5), null);// undefined
        Assert.assertEquals(sample.size(), 2);
    }
}
