package com.github.relucent.base.util.collection;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.junit.Assert;
import org.junit.Test;

public class EnumerationIteratorTest {

    @Test
    public void testIterator() {
        Enumeration<?> enumeration = new StringTokenizer("1 2 3 4 5 6");
        int count = 0;
        for (Iterator<?> iterator = new EnumerationIterator<>(enumeration).iterator(); iterator.hasNext();) {
            iterator.next();
            count++;
        }
        Assert.assertEquals(count, 6);
    }
}
