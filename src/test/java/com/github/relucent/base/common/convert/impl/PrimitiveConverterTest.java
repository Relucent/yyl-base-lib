package com.github.relucent.base.common.convert.impl;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.convert.ConvertException;

/**
 * {@link PrimitiveConverter} 单元测试
 */
public class PrimitiveConverterTest {

	@Test
	public void testConvertToInt() {
		Assert.assertEquals(Integer.valueOf(1), PrimitiveConverter.INSTANCE.convert(1, int.class));
		Assert.assertEquals(Integer.valueOf(123), PrimitiveConverter.INSTANCE.convert("123", int.class));
	}

	@Test
	public void testConvertNullReturnsNull() {
		// 注意:BasicConverter 对 source==null 直接短路返回 null,不会进入 convertInternal 的默认值逻辑
		Assert.assertNull(PrimitiveConverter.INSTANCE.convert(null, int.class));
		Assert.assertNull(PrimitiveConverter.INSTANCE.convert(null, boolean.class));
		Assert.assertNull(PrimitiveConverter.INSTANCE.convert(null, long.class));
		Assert.assertNull(PrimitiveConverter.INSTANCE.convert(null, double.class));
	}

	@Test
	public void testConvertInvalidToIntReturnsDefault() {
		// 无法转换的字符串 → convertInternal 内 defaultIfNull 兜底为默认值 0
		Assert.assertEquals(Integer.valueOf(0), PrimitiveConverter.INSTANCE.convert("abc", int.class));
	}

	@Test
	public void testConvertBoolean() {
		Assert.assertEquals(Boolean.TRUE, PrimitiveConverter.INSTANCE.convert(true, boolean.class));
		Assert.assertEquals(Boolean.TRUE, PrimitiveConverter.INSTANCE.convert("Y", boolean.class));
		Assert.assertEquals(Boolean.FALSE, PrimitiveConverter.INSTANCE.convert(false, boolean.class));
	}

	@Test
	public void testConvertInvalidBooleanReturnsDefault() {
		Assert.assertEquals(Boolean.FALSE, PrimitiveConverter.INSTANCE.convert("unknown", boolean.class));
	}

	@Test
	public void testConvertToChar() {
		Assert.assertEquals(Character.valueOf('A'), PrimitiveConverter.INSTANCE.convert("ABC", char.class));
	}

	@Test
	public void testConvertToByte() {
		Assert.assertEquals(Byte.valueOf((byte) 1), PrimitiveConverter.INSTANCE.convert(1, byte.class));
	}

	@Test
	public void testConvertToShort() {
		Assert.assertEquals(Short.valueOf((short) 1), PrimitiveConverter.INSTANCE.convert(1, short.class));
	}

	@Test
	public void testConvertToLong() {
		Assert.assertEquals(Long.valueOf(1L), PrimitiveConverter.INSTANCE.convert(1, long.class));
	}

	@Test
	public void testConvertToFloat() {
		Assert.assertEquals(Float.valueOf(1.5f), PrimitiveConverter.INSTANCE.convert("1.5", float.class));
	}

	@Test
	public void testConvertToDouble() {
		Assert.assertEquals(Double.valueOf(1.5d), PrimitiveConverter.INSTANCE.convert("1.5", double.class));
	}

	@Test(expected = ConvertException.class)
	public void testConvertNonPrimitiveThrows() {
		// 非原始类型,且源不是目标类型 → convertInternal 抛 ConvertException
		PrimitiveConverter.INSTANCE.convert(123, String.class);
	}
}
