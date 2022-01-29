package com.github.relucent.base.common.collection;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IteratorEnumerationTest {

    @Test
    public void testIterator() {
        List<?> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        int index = 0;
        for (Enumeration<?> en = new IteratorEnumeration<>(list.iterator()); en.hasMoreElements();) {
            Object expected = list.get(index);
            Object actual = en.nextElement();
            Assert.assertEquals(expected, actual);
            index++;
        }
        Assert.assertEquals(list.size(), index);
    }
}
