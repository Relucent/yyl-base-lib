package com.github.relucent.base.common.lang;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EnumUtilTest {

	enum Sample {
		A, B, C
	}

	@Test
	public void testIsEnumClass() {
		assertTrue(EnumUtil.isEnum(Sample.class));
		assertFalse(EnumUtil.isEnum(String.class));
		assertFalse(EnumUtil.isEnum(null));
	}

	@Test
	public void testIsEnumObject() {
		assertTrue(EnumUtil.isEnum((Object) Sample.A));
		assertFalse(EnumUtil.isEnum("x"));
		assertFalse(EnumUtil.isEnum(null));
	}

	@Test
	public void testGetEnumAt() {
		assertSame(Sample.A, EnumUtil.getEnumAt(Sample.class, 0));
		assertSame(Sample.C, EnumUtil.getEnumAt(Sample.class, 2));
		assertNull(EnumUtil.getEnumAt(Sample.class, 3));
		assertNull(EnumUtil.getEnumAt(Sample.class, -1));
	}

	@Test
	public void testGetNames() {
		assertArrayEquals(new String[] { "A", "B", "C" }, EnumUtil.getNames(Sample.class));
	}

	@Test
	public void testFindFirst() {
		assertSame(Sample.B, EnumUtil.findFirst(Sample.class, e -> e.name().startsWith("B")));
		assertNull(EnumUtil.findFirst(Sample.class, e -> e.name().startsWith("Z")));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNonEnumClass() {
		// 非枚举类 / null 应安全返回 null，而非抛 NPE
		@SuppressWarnings("rawtypes")
		final Class rawNonEnum = String.class;
		assertNull(EnumUtil.getEnumAt(rawNonEnum, 0));
		assertNull(EnumUtil.getNames(rawNonEnum));
		assertNull(EnumUtil.findFirst(rawNonEnum, e -> true));
		assertNull(EnumUtil.getEnumAt((Class<Sample>) null, 0));
	}
}
