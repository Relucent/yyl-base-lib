package com.github.relucent.base.common.collection;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ArrayIterator} 单元测试
 */
public class ArrayIteratorTest {

	@Test
	public void testIteration() {
		String[] array = { "a", "b", "c" };
		ArrayIterator<String> iter = new ArrayIterator<>(array);
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals("a", iter.next());
		Assert.assertEquals("b", iter.next());
		Assert.assertEquals("c", iter.next());
		Assert.assertFalse(iter.hasNext());
	}

	@Test
	public void testEmptyArray() {
		Object[] array = new Object[0];
		ArrayIterator<Object> iter = new ArrayIterator<>(array);
		Assert.assertFalse(iter.hasNext());
	}

	@Test
	public void testPrimitiveArray() {
		int[] array = { 10, 20, 30 };
		ArrayIterator<Integer> iter = new ArrayIterator<>(array);
		Assert.assertEquals(Integer.valueOf(10), iter.next());
		Assert.assertEquals(Integer.valueOf(20), iter.next());
		Assert.assertEquals(Integer.valueOf(30), iter.next());
		Assert.assertFalse(iter.hasNext());
	}

	@Test(expected = NoSuchElementException.class)
	public void testNextBeyondEnd() {
		String[] array = { "only" };
		ArrayIterator<String> iter = new ArrayIterator<>(array);
		iter.next();
		iter.next(); // should throw
	}

	@Test(expected = NullPointerException.class)
	public void testNullArrayThrows() {
		new ArrayIterator<>(null);
	}

	@Test
	public void testSingleElement() {
		int[] array = { 42 };
		ArrayIterator<Integer> iter = new ArrayIterator<>(array);
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(Integer.valueOf(42), iter.next());
		Assert.assertFalse(iter.hasNext());
	}

	@Test
	public void testCollectToList() {
		String[] array = { "x", "y", "z" };
		ArrayIterator<String> iter = new ArrayIterator<>(array);
		int count = 0;
		while (iter.hasNext()) {
			iter.next();
			count++;
		}
		Assert.assertEquals(3, count);
	}
}
