package com.github.relucent.base.common.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.collection.CollectionUtil;

public class CollectionUtilTest {

    @Test
    public void testGetFirst() {
        Assert.assertEquals(CollectionUtil.getFirst(Arrays.asList("A", "B", "C")), "A");
        Assert.assertNull(CollectionUtil.getFirst(Arrays.asList(null, null, null)));
        Assert.assertNull(CollectionUtil.getFirst(null));
        Assert.assertNull(CollectionUtil.getFirst(new ArrayList<>()));
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
        List<?> collection = Arrays.asList("hello", "world");
        Object[] array = CollectionUtil.toArray(collection, Object.class);
        Assert.assertArrayEquals(array, new Object[] {"hello", "world"});
    }
}
