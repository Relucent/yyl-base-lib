package com.github.relucent.base.common.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link CastUtil} 单元测试
 */
public class CastUtilTest {

    @Test
    public void testCastUpCollection() {
        Collection<Integer> source = new ArrayList<>();
        source.add(1);
        Collection<Number> result = CastUtil.castUp(source);
        Assert.assertSame(source, result);
        Assert.assertTrue(result.contains(1));
    }

    @Test
    public void testCastDownCollection() {
        Collection<Number> source = new ArrayList<>();
        source.add(1);
        Collection<Integer> result = CastUtil.castDown(source);
        Assert.assertSame(source, result);
    }

    @Test
    public void testCastUpSet() {
        Set<Integer> source = new HashSet<>();
        source.add(1);
        Set<Number> result = CastUtil.castUp(source);
        Assert.assertSame(source, result);
    }

    @Test
    public void testCastDownSet() {
        Set<Number> source = new HashSet<>();
        source.add(1);
        Set<Integer> result = CastUtil.castDown(source);
        Assert.assertSame(source, result);
    }

    @Test
    public void testCastUpList() {
        List<Integer> source = new LinkedList<>();
        source.add(1);
        List<Number> result = CastUtil.castUp(source);
        Assert.assertSame(source, result);
    }

    @Test
    public void testCastDownList() {
        List<Number> source = new LinkedList<>();
        source.add(1);
        List<Integer> result = CastUtil.castDown(source);
        Assert.assertSame(source, result);
    }

    @Test
    public void testCastUpMap() {
        Map<String, Integer> source = new HashMap<>();
        source.put("a", 1);
        Map<String, Number> result = CastUtil.castUp(source);
        Assert.assertSame(source, result);
    }

    @Test
    public void testCastDownMap() {
        Map<String, Number> source = new LinkedHashMap<>();
        source.put("a", 1);
        Map<String, Integer> result = CastUtil.castDown(source);
        Assert.assertSame(source, result);
    }

    @Test
    public void testCastUpLinkedHashSet() {
        Set<String> source = new LinkedHashSet<>();
        source.add("x");
        Set<Object> result = CastUtil.castUp(source);
        Assert.assertSame(source, result);
        Assert.assertEquals("x", result.iterator().next());
    }
}
