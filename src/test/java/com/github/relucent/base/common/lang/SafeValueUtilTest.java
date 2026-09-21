package com.github.relucent.base.common.lang;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SafeValueUtilTest {

	@Test
	public void testSetIfNotNull() {
		List<String> captured = new ArrayList<>();
		SafeValueUtil.<String>setIfNotNull(captured::add, "x");
		assertEquals(Arrays.asList("x"), captured);

		captured.clear();
		SafeValueUtil.<String>setIfNotNull(captured::add, null);
		assertTrue(captured.isEmpty());
	}

	@Test
	public void testSetIfNotEmptyString() {
		List<String> cap = new ArrayList<>();
		SafeValueUtil.setIfNotEmpty(cap::add, "x");
		assertEquals(Arrays.asList("x"), cap);

		cap.clear();
		SafeValueUtil.setIfNotEmpty(cap::add, "");
		SafeValueUtil.setIfNotEmpty(cap::add, (String) null);
		assertTrue(cap.isEmpty());
	}

	@Test
	public void testSetIfNotBlank() {
		List<String> cap = new ArrayList<>();
		SafeValueUtil.setIfNotBlank(cap::add, " x ");
		assertEquals(Arrays.asList(" x "), cap);

		cap.clear();
		SafeValueUtil.setIfNotBlank(cap::add, "   ");
		SafeValueUtil.setIfNotBlank(cap::add, null);
		assertTrue(cap.isEmpty());
	}

	@Test
	public void testSetIfNotEmptyCollection() {
		// 元素类型为 Collection，才能匹配 setIfNotEmpty(Consumer<Collection<T>>, Collection<T>) 重载
		List<Collection<String>> cap = new ArrayList<>();
		SafeValueUtil.<String>setIfNotEmpty(cap::add, Arrays.asList("a"));
		assertEquals(1, cap.size());

		cap.clear();
		SafeValueUtil.<String>setIfNotEmpty(cap::add, new ArrayList<String>());
		assertTrue(cap.isEmpty());
	}

	@Test
	public void testSetIfNotEmptyMap() {
		List<Map<String, String>> cap = new ArrayList<>();
		Map<String, String> m = new HashMap<>();
		m.put("k", "v");
		SafeValueUtil.<String, String>setIfNotEmpty(cap::add, m);
		assertEquals(1, cap.size());

		cap.clear();
		SafeValueUtil.<String, String>setIfNotEmpty(cap::add, new HashMap<String, String>());
		assertTrue(cap.isEmpty());
	}

	@Test
	public void testGetOrNull() {
		String s = "x";
		assertSame(s, SafeValueUtil.getOrNull(s));
		assertNull(SafeValueUtil.getOrNull(null));
	}

	@Test
	public void testGetOrDefault() {
		assertEquals("x", SafeValueUtil.getOrDefault("x", "d"));
		assertEquals("d", SafeValueUtil.getOrDefault(null, "d"));
	}

	@Test
	public void testGetString() {
		assertEquals("x", SafeValueUtil.getString("x", "d"));
		assertEquals("d", SafeValueUtil.getString("", "d"));
		assertEquals("d", SafeValueUtil.getString(null, "d"));
		assertEquals(" ", SafeValueUtil.getString(" ", "d")); // 纯空白不拦截
	}

	@Test
	public void testGetStringIfNotBlank() {
		assertEquals("x", SafeValueUtil.getStringIfNotBlank("x", "d"));
		assertEquals("d", SafeValueUtil.getStringIfNotBlank("  ", "d"));
		assertEquals("d", SafeValueUtil.getStringIfNotBlank(null, "d"));
	}

	@Test
	public void testGetInt() {
		assertEquals(5, SafeValueUtil.getInt(5, 0));
		assertEquals(0, SafeValueUtil.getInt(null, 0));
	}

	@Test
	public void testGetLong() {
		assertEquals(5L, SafeValueUtil.getLong(5L, 1L));
		assertEquals(1L, SafeValueUtil.getLong(null, 1L));
	}

	@Test
	public void testGetDouble() {
		assertEquals(1.5d, SafeValueUtil.getDouble(1.5d, 0d), 0.0001);
		assertEquals(0d, SafeValueUtil.getDouble(null, 0d), 0.0001);
	}

	@Test
	public void testGetBoolean() {
		assertTrue(SafeValueUtil.getBoolean(Boolean.TRUE, false));
		assertTrue(SafeValueUtil.getBoolean(null, true));
		assertFalse(SafeValueUtil.getBoolean(null, false));
	}

	@Test
	public void testGetBigDecimal() {
		BigDecimal v = new BigDecimal("1.2");
		assertSame(v, SafeValueUtil.getBigDecimal(v, BigDecimal.ZERO));
		assertSame(BigDecimal.ZERO, SafeValueUtil.getBigDecimal(null, BigDecimal.ZERO));
	}

	@Test
	public void testGetFirstNonNull() {
		assertEquals("a", SafeValueUtil.getFirstNonNull(null, "a", "b"));
		assertEquals("b", SafeValueUtil.getFirstNonNull(null, null, "b"));
		assertNull(SafeValueUtil.getFirstNonNull(null, null));
		assertNull(SafeValueUtil.getFirstNonNull());
	}
}
