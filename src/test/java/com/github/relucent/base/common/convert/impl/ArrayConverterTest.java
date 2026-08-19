package com.github.relucent.base.common.convert.impl;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ArrayConverter} 单元测试
 */
public class ArrayConverterTest {

	@Test
	public void testConvertNull() {
		Assert.assertNull(ArrayConverter.INSTANCE.convert(null, int[].class));
	}

	@Test
	public void testConvertSameArrayType() {
		int[] source = new int[] { 1, 2, 3 };
		Object result = ArrayConverter.INSTANCE.convert(source, int[].class);
		Assert.assertSame(source, result);
	}

	@Test
	public void testConvertIntegerArrayToIntArray() {
		Integer[] source = new Integer[] { 1, 2, 3 };
		Object result = ArrayConverter.INSTANCE.convert(source, int[].class);
		Assert.assertTrue(result instanceof int[]);
		Assert.assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) result);
	}

	@Test
	public void testConvertIntArrayToIntegerArray() {
		int[] source = new int[] { 1, 2, 3 };
		Object result = ArrayConverter.INSTANCE.convert(source, Integer[].class);
		Assert.assertTrue(result instanceof Integer[]);
		Assert.assertArrayEquals(new Integer[] { 1, 2, 3 }, (Object[]) result);
	}

	@Test
	public void testConvertStringToCharArray() {
		Object result = ArrayConverter.INSTANCE.convert("abc", char[].class);
		Assert.assertTrue(result instanceof char[]);
		Assert.assertArrayEquals(new char[] { 'a', 'b', 'c' }, (char[]) result);
	}

	@Test
	public void testConvertStringToByteArray() {
		Object result = ArrayConverter.INSTANCE.convert("abc", byte[].class);
		Assert.assertTrue(result instanceof byte[]);
		Assert.assertArrayEquals("abc".getBytes(), (byte[]) result);
	}

	@Test
	public void testConvertCollectionToArray() {
		List<Integer> source = Arrays.asList(1, 2, 3);
		Object result = ArrayConverter.INSTANCE.convert(source, Integer[].class);
		Assert.assertTrue(result instanceof Integer[]);
		Assert.assertArrayEquals(new Integer[] { 1, 2, 3 }, (Object[]) result);
	}

	@Test
	public void testConvertCollectionToPrimitiveArray() {
		List<Integer> source = Arrays.asList(1, 2, 3);
		Object result = ArrayConverter.INSTANCE.convert(source, int[].class);
		Assert.assertTrue(result instanceof int[]);
		Assert.assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) result);
	}

	@Test
	public void testConvertStringArrayToIntArray() {
		String[] source = new String[] { "1", "2", "3" };
		Object result = ArrayConverter.INSTANCE.convert(source, int[].class);
		Assert.assertTrue(result instanceof int[]);
		Assert.assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) result);
	}

	@Test
	public void testConvertSingleElementToPrimitiveArray() {
		// 单元素 → int[] (修复前 convertToSingleElementArray 经 ArrayUtil.newArray 对原始类型触发 ClassCastException 被吞返回 null)
		Object result = ArrayConverter.INSTANCE.convert(42, int[].class);
		Assert.assertTrue(result instanceof int[]);
		Assert.assertArrayEquals(new int[] { 42 }, (int[]) result);
	}

	@Test
	public void testConvertSingleElementToObjectArray() {
		// 单元素 → String[] (引用类型组件走同一路径,验证未回归)
		Object result = ArrayConverter.INSTANCE.convert(123, String[].class);
		Assert.assertTrue(result instanceof String[]);
		Assert.assertArrayEquals(new String[] { "123" }, (String[]) result);
	}
}
