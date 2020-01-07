package com.github.relucent.base.common.collection;

import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.relucent.base.common.collection.ConcurrentHashSet;

public class ConcurrentHashSetTest {

    private Set<String> sample = new ConcurrentHashSet<>();

    @Before
    public void before() {
        for (int i = 0; i < 100; i++) {
            sample.add(String.valueOf(i));
        }
    }

    @Test
    public void testIteratorRemove() {
        for (Iterator<String> it = sample.iterator(); it.hasNext();) {
            it.next();
            it.remove();
        }
        Assert.assertTrue(sample.isEmpty());
    }

    @Test
    public void testForEachRemove() {
        for (String value : sample) {
            sample.remove(value);
        }
    }
}
