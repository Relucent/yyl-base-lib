package com.github.relucent.base.common.convert.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.reflect.TypeReference;

/**
 * {@link CollectionConverter} 单元测试
 */
public class CollectionConverterTest {

    private enum TestEnum {
        A, B, C
    }

    @Test
    public void testConvertNull() {
        // source 为 null 时,遵循转换器契约返回 null(修复前会进入 addElements 触发 NPE)
        Assert.assertNull(CollectionConverter.INSTANCE.convert(null, new TypeReference<List<Integer>>() {}.getType()));
    }

    @Test
    public void testConvertToArrayList() {
        Collection<?> result = CollectionConverter.INSTANCE.convert(Arrays.asList(1, 2, 3),
            new TypeReference<ArrayList<Integer>>() {}.getType());
        Assert.assertTrue(result instanceof ArrayList);
        Assert.assertEquals(3, result.size());
        Assert.assertTrue(result.contains(1));
        Assert.assertTrue(result.contains(2));
        Assert.assertTrue(result.contains(3));
    }

    @Test
    public void testConvertToLinkedList() {
        Collection<?> result = CollectionConverter.INSTANCE.convert(Arrays.asList(1, 2, 3),
            new TypeReference<LinkedList<Integer>>() {}.getType());
        Assert.assertTrue(result instanceof LinkedList);
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void testConvertToHashSet() {
        Collection<?> result = CollectionConverter.INSTANCE.convert(Arrays.asList(1, 2, 3),
            new TypeReference<HashSet<Integer>>() {}.getType());
        Assert.assertTrue(result instanceof HashSet);
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void testConvertToLinkedHashSet() {
        Collection<?> result = CollectionConverter.INSTANCE.convert(Arrays.asList(1, 2, 3),
            new TypeReference<LinkedHashSet<Integer>>() {}.getType());
        Assert.assertTrue(result instanceof LinkedHashSet);
        Assert.assertEquals(3, result.size());
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testConvertToTreeSet() {
        Collection<?> result = CollectionConverter.INSTANCE.convert(Arrays.asList(3, 1, 2),
            new TypeReference<TreeSet<Integer>>() {}.getType());
        Assert.assertTrue(result instanceof TreeSet);
        Assert.assertEquals(3, result.size());
        // TreeSet 有序
        Assert.assertEquals(Integer.valueOf(1), ((TreeSet<Integer>) result).first());
        Assert.assertEquals(Integer.valueOf(3), ((TreeSet<Integer>) result).last());
    }

    @Test
    public void testConvertToEnumSet() {
        Collection<?> result = CollectionConverter.INSTANCE.convert(Arrays.asList("A", "B"),
            new TypeReference<java.util.EnumSet<TestEnum>>() {}.getType());
        Assert.assertTrue(result instanceof java.util.EnumSet);
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(TestEnum.A));
        Assert.assertTrue(result.contains(TestEnum.B));
    }

    @Test
    public void testConvertWithElementConversion() {
        // 字符串元素 → 整数元素
        Collection<?> result = CollectionConverter.INSTANCE.convert(Arrays.asList("1", "2", "3"),
            new TypeReference<List<Integer>>() {}.getType());
        Assert.assertTrue(result instanceof List);
        List<?> list = (List<?>) result;
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Integer.valueOf(1), list.get(0));
        Assert.assertEquals(Integer.valueOf(2), list.get(1));
        Assert.assertEquals(Integer.valueOf(3), list.get(2));
    }

    @Test
    public void testConvertToRawList() {
        // 无泛型参数的 List
        Collection<?> result = CollectionConverter.INSTANCE.convert(Arrays.asList(1, 2, 3), List.class);
        Assert.assertTrue(result instanceof List);
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void testConvertToRawEnumSet() {
        // 原始 EnumSet.class 无法确定元素类型，按转换失败处理返回 null（修复前会抛出 ClassCastException）
        Assert.assertNull(CollectionConverter.INSTANCE.convert(Arrays.asList("A", "B"), java.util.EnumSet.class));
    }
}
