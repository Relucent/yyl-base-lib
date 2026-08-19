package com.github.relucent.base.common.collection;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link IteratorUtil} 单元测试
 */
public class IteratorUtilTest {

	@Test
	public void testHasNextNull() {
		Assert.assertFalse(IteratorUtil.hasNext(null));
	}

	@Test
	public void testHasNextEmpty() {
		Iterator<?> iter = Collections.emptyList().iterator();
		Assert.assertFalse(IteratorUtil.hasNext(iter));
	}

	@Test
	public void testHasNextNonEmpty() {
		Iterator<?> iter = Arrays.asList(1, 2).iterator();
		Assert.assertTrue(IteratorUtil.hasNext(iter));
		iter.next();
		Assert.assertTrue(IteratorUtil.hasNext(iter));
		iter.next();
		Assert.assertFalse(IteratorUtil.hasNext(iter));
	}

	@Test
	public void testToIteratorNull() {
		Assert.assertNull(IteratorUtil.toIterator(null));
	}

	@Test
	public void testToIteratorFromIterator() {
		List<Integer> list = Arrays.asList(1, 2, 3);
		Iterator<Integer> original = list.iterator();
		Iterator<?> result = IteratorUtil.toIterator(original);
		Assert.assertSame(original, result);
	}

	@Test
	public void testToIteratorFromIterable() {
		List<Integer> list = Arrays.asList(1, 2, 3);
		Iterator<?> result = IteratorUtil.toIterator(list);
		Assert.assertEquals(1, result.next());
		Assert.assertEquals(2, result.next());
		Assert.assertEquals(3, result.next());
		Assert.assertFalse(result.hasNext());
	}

	@Test
	public void testToIteratorFromArray() {
		String[] array = { "a", "b", "c" };
		Iterator<?> result = IteratorUtil.toIterator(array);
		Assert.assertEquals("a", result.next());
		Assert.assertEquals("b", result.next());
		Assert.assertEquals("c", result.next());
		Assert.assertFalse(result.hasNext());
	}

	@Test
	public void testToIteratorFromMap() {
		Map<String, Integer> map = new HashMap<>();
		map.put("x", 1);
		map.put("y", 2);
		Iterator<?> result = IteratorUtil.toIterator(map);
		int count = 0;
		while (result.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) result.next();
			Assert.assertNotNull(entry.getKey());
			Assert.assertNotNull(entry.getValue());
			count++;
		}
		Assert.assertEquals(2, count);
	}

	@Test
	public void testToIteratorFromEnumeration() {
		Vector<String> vector = new Vector<>();
		vector.addAll(Arrays.asList("p", "q"));
		Enumeration<String> enum_ = vector.elements();
		Iterator<?> result = IteratorUtil.toIterator(enum_);
		Assert.assertEquals("p", result.next());
		Assert.assertEquals("q", result.next());
		Assert.assertFalse(result.hasNext());
	}

	@Test
	public void testToIteratorFromDictionary() {
		Hashtable<String, Integer> dict = new Hashtable<>();
		dict.put("k1", 10);
		dict.put("k2", 20);
		Iterator<?> result = IteratorUtil.toIterator(dict);
		int count = 0;
		while (result.hasNext()) {
			result.next();
			count++;
		}
		Assert.assertEquals(2, count);
	}

	@Test
	public void testToIteratorFromSingleObject() {
		Object obj = "single";
		Iterator<?> result = IteratorUtil.toIterator(obj);
		Assert.assertTrue(result.hasNext());
		Assert.assertEquals("single", result.next());
		Assert.assertFalse(result.hasNext());
	}

	@Test
	public void testToIteratorFromPrimitiveArray() {
		int[] array = { 1, 2, 3 };
		Iterator<?> result = IteratorUtil.toIterator(array);
		Assert.assertEquals(1, result.next());
		Assert.assertEquals(2, result.next());
		Assert.assertEquals(3, result.next());
		Assert.assertFalse(result.hasNext());
	}
}
