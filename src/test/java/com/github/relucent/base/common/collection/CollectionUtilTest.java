package com.github.relucent.base.common.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CollectionUtilTest {

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(CollectionUtil.isEmpty(null));
        Assert.assertTrue(CollectionUtil.isEmpty(new ArrayList<>()));
        Assert.assertFalse(CollectionUtil.isEmpty(Arrays.asList("A", "B", "C")));
    }

    @Test
    public void testGetFirst() {
        Assert.assertEquals(CollectionUtil.getFirst(Arrays.asList("A", "B", "C")), "A");
        Assert.assertNull(CollectionUtil.getFirst(Arrays.asList(null, null, null)));
        Assert.assertNull(CollectionUtil.getFirst(null));
        Assert.assertNull(CollectionUtil.getFirst(new ArrayList<>()));
    }

    @Test
    public void testAddAll() {
        Collection<Object> collection = new HashSet<>();
        Assert.assertFalse(CollectionUtil.addAll(collection, new Object[0]));
        Assert.assertTrue(CollectionUtil.addAll(collection, new Object[] { "A", "B", "C" }));
        Assert.assertFalse(CollectionUtil.addAll(collection, new Object[] { "A", "B", "C" }));
    }

    @Test
    public void testIndexOfType() {
        List<?> collection = Arrays.asList(null, "string", 3L);
        Assert.assertEquals(CollectionUtil.indexOfType(collection, String.class), 1);
        Assert.assertEquals(CollectionUtil.indexOfType(collection, CharSequence.class), 1);
        Assert.assertEquals(CollectionUtil.indexOfType(collection, Map.class), -1);

    }

    @Test
    public void testToArray() {
        List<?> list = Arrays.asList("hello", "world");
        Object[] array = CollectionUtil.toArray(list, Object.class);
        Assert.assertArrayEquals(array, new Object[] { "hello", "world" });
    }

    @Test
    public void testToArrayGenerator() {
        List<?> list = Arrays.asList("hello", "world");
        Object[] array = CollectionUtil.toArray(list, Object[]::new);
        Assert.assertArrayEquals(array, new Object[] { "hello", "world" });
    }
}
