package com.github.relucent.base.common.convert.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link BooleanConverter} 单元测试
 */
public class BooleanConverterTest {

	@Test
	public void testConvertNull() {
		Assert.assertNull(BooleanConverter.INSTANCE.convert(null, Boolean.class));
	}

	@Test
	public void testConvertBoolean() {
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert(Boolean.TRUE, Boolean.class));
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert(Boolean.FALSE, Boolean.class));
	}

	@Test
	public void testConvertNumber() {
		// 非0为true
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert(1, Boolean.class));
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert(-1, Boolean.class));
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert(100L, Boolean.class));
		// 0为false
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert(0, Boolean.class));
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert(0L, Boolean.class));
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert(0.0, Boolean.class));
	}

	@Test
	public void testConvertString() {
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert("1", Boolean.class));
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert("Y", Boolean.class));
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert("T", Boolean.class));
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert("true", Boolean.class));
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert("on", Boolean.class));
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert("YES", Boolean.class));

		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert("0", Boolean.class));
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert("N", Boolean.class));
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert("F", Boolean.class));
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert("false", Boolean.class));
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert("off", Boolean.class));
		Assert.assertEquals(Boolean.FALSE, BooleanConverter.INSTANCE.convert("NO", Boolean.class));
	}

	@Test
	public void testConvertUnrecognizedString() {
		// 无法识别的字符串返回 null
		Assert.assertNull(BooleanConverter.INSTANCE.convert("hello", Boolean.class));
	}

	@Test
	public void testConvertWithDefault() {
		// 转换失败返回默认值
		Assert.assertEquals(Boolean.TRUE, BooleanConverter.INSTANCE.convert("hello", Boolean.class, Boolean.TRUE));
		Assert.assertNull(BooleanConverter.INSTANCE.convert("hello", Boolean.class, null));
	}
}
