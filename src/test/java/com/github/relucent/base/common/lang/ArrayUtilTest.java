package com.github.relucent.base.common.lang;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilTest {

	@Test
	public void testIsEmpty() {
		Assert.assertTrue(ArrayUtil.isEmpty((Object[]) null));
		Assert.assertTrue(ArrayUtil.isEmpty(new String[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new String[] { "a" }));
		Assert.assertTrue(ArrayUtil.isEmpty((int[]) null));
		Assert.assertTrue(ArrayUtil.isEmpty(new int[0]));
		Assert.assertFalse(ArrayUtil.isEmpty(new int[] { 1 }));
	}

	@Test
	public void testIsNotEmpty() {
		Assert.assertFalse(ArrayUtil.isNotEmpty((Object[]) null));
		Assert.assertTrue(ArrayUtil.isNotEmpty(new String[] { "a" }));
	}

	@Test
	public void testGetLength() {
		Assert.assertEquals(0, ArrayUtil.getLength(null));
		Assert.assertEquals(0, ArrayUtil.getLength(new Object[0]));
		Assert.assertEquals(1, ArrayUtil.getLength(new Object[] { null }));
		Assert.assertEquals(3, ArrayUtil.getLength(new int[] { 1, 2, 3 }));
	}

	@Test
	public void testIndexOfAndContains() {
		String[] arr = { "a", "b", "c", "a" };
		Assert.assertEquals(0, ArrayUtil.indexOf(arr, "a"));
		Assert.assertEquals(3, ArrayUtil.lastIndexOf(arr, "a"));
		Assert.assertEquals(-1, ArrayUtil.indexOf(arr, "z"));
		Assert.assertTrue(ArrayUtil.contains(arr, "b"));
		Assert.assertFalse(ArrayUtil.contains(arr, "z"));

		int[] iarr = { 1, 2, 3 };
		Assert.assertEquals(1, ArrayUtil.indexOf(iarr, (byte) 2));
		Assert.assertTrue(ArrayUtil.contains(iarr, (byte) 3));
	}

	@Test
	public void testGetFirst() {
		Assert.assertNull(ArrayUtil.getFirst((String[]) null));
		Assert.assertNull(ArrayUtil.getFirst(new String[0]));
		Assert.assertEquals("a", ArrayUtil.getFirst(new String[] { "a", "b" }));
	}

	@Test
	public void testGet() {
		String[] arr = { "a", "b", "c" };
		Assert.assertEquals("a", ArrayUtil.get(arr, 0));
		Assert.assertEquals("c", ArrayUtil.get(arr, 2));
		Assert.assertEquals("c", ArrayUtil.get(arr, -1)); // 负索引从末尾回绕
		Assert.assertNull(ArrayUtil.get(arr, 5)); // 越界返回 null
		Assert.assertNull(ArrayUtil.get(arr, -5));
		Assert.assertNull(ArrayUtil.get((String[]) null, 0)); // null 数组返回 null（null 安全）
	}

	@Test
	public void testHasNull() {
		Assert.assertFalse(ArrayUtil.hasNull((String[]) null));
		Assert.assertFalse(ArrayUtil.hasNull(new String[] { "a", "b" }));
		Assert.assertTrue(ArrayUtil.hasNull(new String[] { "a", null }));
	}

	@Test
	public void testToObjectAndToPrimitive() {
		Integer[] obj = ArrayUtil.toObject(new int[] { 1, 2, 3 });
		Assert.assertArrayEquals(new Integer[] { 1, 2, 3 }, obj);
		int[] prim = ArrayUtil.toPrimitive(new Integer[] { 1, 2, 3 });
		Assert.assertArrayEquals(new int[] { 1, 2, 3 }, prim);
	}

	@Test
	public void testConcat() {
		String[] r = ArrayUtil.concat(new String[] { "a" }, new String[] { "b" });
		Assert.assertArrayEquals(new String[] { "a", "b" }, r);
		// 单个数组原样返回（避免无参/多参调用在众多原始类型重载下的歧义）
		int[] ri = ArrayUtil.concat(new int[] { 1, 2, 3 });
		Assert.assertArrayEquals(new int[] { 1, 2, 3 }, ri);
	}

	@Test
	public void testFilter() {
		Integer[] filtered = ArrayUtil.filter(new Integer[] { 1, 2, 3 }, new Predicate<Integer>() {
			@Override
			public boolean test(Integer i) {
				return i > 1;
			}
		});
		Assert.assertArrayEquals(new Integer[] { 2, 3 }, filtered);
	}

	@Test
	public void testMap() {
		String[] mapped = ArrayUtil.map(new String[] { "a", "b" }, String.class, new Function<String, String>() {
			@Override
			public String apply(String s) {
				return s.toUpperCase();
			}
		});
		Assert.assertArrayEquals(new String[] { "A", "B" }, mapped);
	}

	@Test
	public void testFindFirst() {
		Integer found = ArrayUtil.findFirst(new Integer[] { 1, 2, 3 }, new Predicate<Integer>() {
			@Override
			public boolean test(Integer i) {
				return i == 2;
			}
		});
		Assert.assertEquals(Integer.valueOf(2), found);
		Integer none = ArrayUtil.findFirst(new Integer[] { 1 }, new Predicate<Integer>() {
			@Override
			public boolean test(Integer i) {
				return false;
			}
		});
		Assert.assertNull(none);
	}

	@Test
	public void testNewArray() {
		String[] a = ArrayUtil.newArray(String.class, 3);
		Assert.assertTrue(a instanceof String[]);
		Assert.assertEquals(3, a.length);
	}

	@Test
	public void testToList() {
		List<String> list = Arrays.asList("a", "b");
		Assert.assertTrue(ArrayUtil.isNotEmpty(list.toArray(new String[0])));
	}
}
