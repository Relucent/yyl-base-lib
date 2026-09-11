package com.github.relucent.base.common.convert.impl;

import java.time.ZoneId;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link TimeZoneConverter} 单元测试
 */
public class TimeZoneConverterTest {

	@Test
	public void testConvertFromTimeZone() {
		TimeZone source = TimeZone.getTimeZone("Asia/Shanghai");
		Assert.assertSame(source, TimeZoneConverter.INSTANCE.convert(source, TimeZone.class));
	}

	@Test
	public void testConvertFromZoneId() {
		Assert.assertEquals(ZoneId.of("Asia/Shanghai"),
				TimeZoneConverter.INSTANCE.convert(ZoneId.of("Asia/Shanghai"), TimeZone.class).toZoneId());
	}

	@Test
	public void testConvertFromText() {
		Assert.assertEquals("Asia/Shanghai",
				TimeZoneConverter.INSTANCE.convert("Asia/Shanghai", TimeZone.class).getID());
	}

	@Test
	public void testConvertFromIllegalTextFallsBackToGmt() {
		// 固化既有语义：TimeZone.getTimeZone 对无效 ID 静默退化为 GMT
		Assert.assertEquals("GMT", TimeZoneConverter.INSTANCE.convert("garbage", TimeZone.class).getID());
		Assert.assertEquals("GMT", TimeZoneConverter.INSTANCE.convert("", TimeZone.class).getID());
	}

	@Test
	public void testConvertNull() {
		Assert.assertNull(TimeZoneConverter.INSTANCE.convert(null, TimeZone.class));
	}
}
