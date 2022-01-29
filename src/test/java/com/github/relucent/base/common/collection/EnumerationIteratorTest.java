package com.github.relucent.base.common.collection;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.junit.Assert;
import org.junit.Test;

public class EnumerationIteratorTest {

    @Test
    public void testIterator() {
        Object[] expecteds = { "1", "2", "3", "4", "5", "6" };
        Enumeration<?> enumeration = new StringTokenizer("1 2 3 4 5 6");
        int index = 0;
        for (Iterator<?> iterator = new EnumerationIterator<>(enumeration); iterator.hasNext();) {
            Object expected = expecteds[index];
            Object actual = iterator.next();
            Assert.assertEquals(expected, actual);
            index++;
        }
        Assert.assertEquals(expecteds.length, index);
    }
}
