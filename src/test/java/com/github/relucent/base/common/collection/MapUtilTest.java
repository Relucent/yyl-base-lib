package com.github.relucent.base.common.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Assert;
import org.junit.Test;

public class MapUtilTest {

    @Test
    public void testNewMapIfPossible() {

        Class<?>[] types = { //
                Map.class, // HashMap
                HashMap.class, // HashMap
                TreeMap.class, // TreeMap
                SortedMap.class, // TreeMap
                ConcurrentMap.class, // ConcurrentHashMap
                ConcurrentHashMap.class // ConcurrentHashMap
        };

        for (Class<?> expectedType : types) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Class<?> actualType = MapUtil.newMapIfPossible((Class<? extends Map>) expectedType).getClass();
            Assert.assertTrue(expectedType.isAssignableFrom(actualType));
        }

        Assert.assertTrue(Map.class.isAssignableFrom(MapUtil.newMapIfPossible(null).getClass()));
    }

}
