package com.github.relucent.base.common.convert.impl;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.time.DateUtil;

/**
 * {@link StringConverter} 单元测试
 */
public class StringConverterTest {

	@Test
	public void testConvertNull() {
		Assert.assertNull(StringConverter.INSTANCE.convert(null, String.class));
	}

	@Test
	public void testConvertCharSequence() {
		Assert.assertEquals("hello", StringConverter.INSTANCE.convert(new StringBuilder("hello"), String.class));
		Assert.assertEquals("world", StringConverter.INSTANCE.convert("world", String.class));
	}

	@Test
	public void testConvertDate() {
		Date now = new Date();
		String expected = DateUtil.format(now);
		Assert.assertEquals(expected, StringConverter.INSTANCE.convert(now, String.class));
	}

	@Test
	public void testConvertTimeZone() {
		TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
		Assert.assertEquals("Asia/Shanghai", StringConverter.INSTANCE.convert(tz, String.class));
	}

	@Test
	public void testConvertType() {
		Type type = String.class;
		Assert.assertEquals("java.lang.String", StringConverter.INSTANCE.convert(type, String.class));
	}

	@Test
	public void testConvertObject() {
		Assert.assertEquals("123", StringConverter.INSTANCE.convert(123, String.class));
		Object obj = new Object();
		Assert.assertEquals(obj.toString(), StringConverter.INSTANCE.convert(obj, String.class));
	}

	@Test
	public void testConvertWithDefault() {
		Assert.assertEquals("default", StringConverter.INSTANCE.convert(null, String.class, "default"));
	}
}
