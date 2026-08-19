package com.github.relucent.base.common.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link WeakConcurrentMap} 和 {@link ReferenceConcurrentMap} 单元测试
 */
public class WeakConcurrentMapTest {

    @Test
    public void testPutAndGet() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("one", 1);
        map.put("two", 2);
        Assert.assertEquals(Integer.valueOf(1), map.get("one"));
        Assert.assertEquals(Integer.valueOf(2), map.get("two"));
    }

    @Test
    public void testSize() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        Assert.assertEquals(0, map.size());
        map.put("a", 1);
        Assert.assertEquals(1, map.size());
        map.put("b", 2);
        Assert.assertEquals(2, map.size());
    }

    @Test
    public void testIsEmpty() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        Assert.assertTrue(map.isEmpty());
        map.put("a", 1);
        Assert.assertFalse(map.isEmpty());
    }

    @Test
    public void testContainsKey() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("key", 42);
        Assert.assertTrue(map.containsKey("key"));
        Assert.assertFalse(map.containsKey("missing"));
    }

    @Test
    public void testContainsValue() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("key", 42);
        Assert.assertTrue(map.containsValue(42));
        Assert.assertFalse(map.containsValue(99));
    }

    @Test
    public void testRemove() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Assert.assertEquals(Integer.valueOf(1), map.remove("a"));
        Assert.assertFalse(map.containsKey("a"));
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testClear() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.clear();
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testPutIfAbsent() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        Assert.assertNull(map.putIfAbsent("key", 1));
        Assert.assertEquals(Integer.valueOf(1), map.putIfAbsent("key", 2));
        Assert.assertEquals(Integer.valueOf(1), map.get("key"));
    }

    @Test
    public void testReplace() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("key", 1);
        Assert.assertEquals(Integer.valueOf(1), map.replace("key", 2));
        Assert.assertEquals(Integer.valueOf(2), map.get("key"));
    }

    @Test
    public void testReplaceWithValueCheck() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("key", 1);
        Assert.assertTrue(map.replace("key", 1, 2));
        Assert.assertFalse(map.replace("key", 1, 3));
        Assert.assertEquals(Integer.valueOf(2), map.get("key"));
    }

    @Test
    public void testComputeIfAbsent() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        Integer result = map.computeIfAbsent("key", k -> 42);
        Assert.assertEquals(Integer.valueOf(42), result);
        Assert.assertEquals(Integer.valueOf(42), map.get("key"));
    }

    @Test
    public void testComputeIfAbsentSupplier() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        Integer result = map.computeIfAbsent("key", () -> 99);
        Assert.assertEquals(Integer.valueOf(99), result);
        Assert.assertEquals(Integer.valueOf(99), map.get("key"));
    }

    @Test
    public void testComputeIfPresent() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("key", 1);
        Integer result = map.computeIfPresent("key", (k, v) -> v + 10);
        Assert.assertEquals(Integer.valueOf(11), result);
        Assert.assertNull(map.computeIfPresent("missing", (k, v) -> 99));
    }

    @Test
    public void testKeySet() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        Assert.assertEquals(3, map.keySet().size());
        Assert.assertTrue(map.keySet().contains("a"));
        Assert.assertTrue(map.keySet().contains("b"));
        Assert.assertTrue(map.keySet().contains("c"));
    }

    @Test
    public void testEntrySet() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("a", 1);
        map.put("b", 2);
        Assert.assertEquals(2, map.entrySet().size());
    }

    @Test
    public void testForEach() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        int[] sum = { 0 };
        map.forEach((k, v) -> sum[0] += v);
        Assert.assertEquals(6, sum[0]);
    }

    @Test
    public void testPutAll() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        Map<String, Integer> source = new ConcurrentHashMap<>();
        source.put("x", 10);
        source.put("y", 20);
        map.putAll(source);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(Integer.valueOf(10), map.get("x"));
        Assert.assertEquals(Integer.valueOf(20), map.get("y"));
    }

    @Test
    public void testIterator() {
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>();
        map.put("a", 1);
        map.put("b", 2);
        int count = 0;
        for (Map.Entry<String, Integer> entry : map) {
            Assert.assertNotNull(entry.getKey());
            Assert.assertNotNull(entry.getValue());
            count++;
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void testWithRawConcurrentMap() {
        ConcurrentMap<java.lang.ref.Reference<String>, Integer> raw = new ConcurrentHashMap<>();
        WeakConcurrentMap<String, Integer> map = new WeakConcurrentMap<>(raw);
        map.put("key", 42);
        Assert.assertEquals(Integer.valueOf(42), map.get("key"));
        Assert.assertEquals(1, raw.size());
    }
}
