package com.github.relucent.base.common.convert.impl;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.convert.ConvertUtil;
import com.github.relucent.base.common.time.ZoneUtil;

/**
 * {@link TemporalAccessorConverter} 单元测试
 */
public class TemporalAccessorConverterTest {

	@Test
	public void testConvertNull() {
		Assert.assertNull(TemporalAccessorConverter.INSTANCE.convert(null, LocalDate.class));
	}

	@Test
	public void testConvertSameType() {
		LocalDate source = LocalDate.of(2023, 1, 1);
		Assert.assertSame(source, TemporalAccessorConverter.INSTANCE.convert(source, LocalDate.class));
	}

	@Test
	public void testConvertNumberToMonth() {
		Assert.assertEquals(Month.JANUARY, TemporalAccessorConverter.INSTANCE.convert(1, Month.class));
		Assert.assertEquals(Month.DECEMBER, TemporalAccessorConverter.INSTANCE.convert(12, Month.class));
	}

	@Test
	public void testConvertNumberToDayOfWeek() {
		Assert.assertEquals(DayOfWeek.MONDAY, TemporalAccessorConverter.INSTANCE.convert(1, DayOfWeek.class));
		Assert.assertEquals(DayOfWeek.SUNDAY, TemporalAccessorConverter.INSTANCE.convert(7, DayOfWeek.class));
	}

	@Test
	public void testConvertNumberToInstant() {
		Instant result = (Instant) TemporalAccessorConverter.INSTANCE.convert(0L, Instant.class);
		Assert.assertEquals(Instant.ofEpochMilli(0L), result);
	}

	@Test
	public void testConvertNumberToLocalDateTime() {
		Instant instant = Instant.ofEpochMilli(0L);
		LocalDateTime expected = LocalDateTime.ofInstant(instant, ZoneUtil.getDefaultZoneId());
		LocalDateTime result = (LocalDateTime) TemporalAccessorConverter.INSTANCE.convert(0L, LocalDateTime.class);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertInstantToLocalDateTime() {
		Instant instant = Instant.ofEpochMilli(1700000000000L);
		LocalDateTime expected = LocalDateTime.ofInstant(instant, ZoneUtil.getDefaultZoneId());
		LocalDateTime result = (LocalDateTime) TemporalAccessorConverter.INSTANCE.convert(instant, LocalDateTime.class);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertDateToInstant() {
		Date date = new Date(0L);
		Instant result = (Instant) TemporalAccessorConverter.INSTANCE.convert(date, Instant.class);
		Assert.assertEquals(Instant.ofEpochMilli(0L), result);
	}

	@Test
	public void testConvertLocalDateTimeToLocalDate() {
		LocalDateTime ldt = LocalDateTime.of(2023, 1, 1, 12, 30);
		LocalDate result = (LocalDate) TemporalAccessorConverter.INSTANCE.convert(ldt, LocalDate.class);
		Assert.assertEquals(LocalDate.of(2023, 1, 1), result);
	}

	@Test
	public void testConvertLocalDateTimeToLocalTime() {
		LocalDateTime ldt = LocalDateTime.of(2023, 1, 1, 12, 30);
		LocalTime result = (LocalTime) TemporalAccessorConverter.INSTANCE.convert(ldt, LocalTime.class);
		Assert.assertEquals(LocalTime.of(12, 30), result);
	}

	@Test
	public void testConvertLocalDateTimeToInstant() {
		LocalDateTime ldt = LocalDateTime.of(2023, 1, 1, 12, 30);
		Instant expected = ldt.atZone(ZoneUtil.getDefaultZoneId()).toInstant();
		Instant result = (Instant) TemporalAccessorConverter.INSTANCE.convert(ldt, Instant.class);
		Assert.assertEquals(expected, result);
	}

	@Test
	public void testConvertZonedDateTimeToLocalDateTime() {
		ZonedDateTime zdt = ZonedDateTime.of(2023, 1, 1, 12, 30, 0, 0, ZoneId.of("UTC"));
		LocalDateTime result = (LocalDateTime) TemporalAccessorConverter.INSTANCE.convert(zdt, LocalDateTime.class);
		Assert.assertEquals(LocalDateTime.of(2023, 1, 1, 12, 30), result);
	}

	@Test
	public void testConvertZonedDateTimeToOffsetDateTime() {
		ZonedDateTime zdt = ZonedDateTime.of(2023, 1, 1, 12, 30, 0, 0, ZoneId.of("UTC"));
		OffsetDateTime result = (OffsetDateTime) TemporalAccessorConverter.INSTANCE.convert(zdt, OffsetDateTime.class);
		Assert.assertEquals(zdt.toOffsetDateTime(), result);
	}

	@Test
	public void testConvertStringToDayOfWeek() {
		Assert.assertEquals(DayOfWeek.MONDAY, TemporalAccessorConverter.INSTANCE.convert("MONDAY", DayOfWeek.class));
	}

	@Test
	public void testConvertStringToMonth() {
		Assert.assertEquals(Month.JANUARY, TemporalAccessorConverter.INSTANCE.convert("JANUARY", Month.class));
	}

	@Test
	public void testConvertStringToLocalDate() {
		LocalDate result = (LocalDate) TemporalAccessorConverter.INSTANCE.convert("2023-01-01", LocalDate.class);
		Assert.assertEquals(LocalDate.of(2023, 1, 1), result);
	}

	@Test
	public void testConvertStringToZonedDateTime() {
		ZonedDateTime result = (ZonedDateTime) TemporalAccessorConverter.INSTANCE.convert("2023-01-01T00:00:00Z",
				ZonedDateTime.class);
		Assert.assertNotNull(result);
		// 只比较日期,避免默认时区转换带来的小时差异
		Assert.assertEquals(LocalDate.of(2023, 1, 1), result.toLocalDate());
	}

	@Test
	public void testConvertToTemporalAccessor() {
		// 目标类型是 TemporalAccessor 接口本身
		TemporalAccessor result = TemporalAccessorConverter.INSTANCE.convert(0L, TemporalAccessor.class);
		Assert.assertNotNull(result);
	}

	@Test
	public void testConvertUnmatchedString() {
		// 无法解析的字符串返回 null
		Assert.assertNull(TemporalAccessorConverter.INSTANCE.convert("not-a-date", LocalDate.class));
	}

	@Test
	public void testConvertSameEra() {
		Assert.assertSame(IsoEra.CE, TemporalAccessorConverter.INSTANCE.convert(IsoEra.CE, Era.class));
	}

	@Test
	public void testConvertNumberToEra() {
		Assert.assertEquals(IsoEra.BCE, TemporalAccessorConverter.INSTANCE.convert(0, Era.class));
		Assert.assertEquals(IsoEra.CE, TemporalAccessorConverter.INSTANCE.convert(1, Era.class));
		// 越界的纪元数值按转换失败处理
		Assert.assertNull(TemporalAccessorConverter.INSTANCE.convert(2, Era.class));
	}

	@Test
	public void testConvertStringToEra() {
		Assert.assertEquals(IsoEra.CE, TemporalAccessorConverter.INSTANCE.convert("CE", Era.class));
		Assert.assertEquals(IsoEra.BCE, TemporalAccessorConverter.INSTANCE.convert("BCE", Era.class));
		Assert.assertNull(TemporalAccessorConverter.INSTANCE.convert("not-an-era", Era.class));
	}

	@Test
	public void testConvertTemporalAccessorToEra() {
		Assert.assertEquals(IsoEra.CE, TemporalAccessorConverter.INSTANCE.convert(LocalDate.of(2026, 9, 14), Era.class));
		Assert.assertEquals(IsoEra.BCE, TemporalAccessorConverter.INSTANCE.convert(LocalDate.of(-1, 1, 1), Era.class));
		// LocalTime 不包含纪元字段，返回 null 而不是抛出异常
		Assert.assertNull(TemporalAccessorConverter.INSTANCE.convert(LocalTime.NOON, Era.class));
	}

	@Test
	public void testConvertDateToEra() {
		Assert.assertEquals(IsoEra.CE, TemporalAccessorConverter.INSTANCE.convert(new Date(0L), Era.class));
	}

	@Test
	public void testConvertToEraViaConvertUtil() {
		// Era 已注册为默认转换器，可通过 ConvertUtil 直接使用
		Assert.assertEquals(IsoEra.CE, ConvertUtil.convert("CE", Era.class));
		Assert.assertNull(ConvertUtil.convert("not-an-era", Era.class));
		// 无法转换时返回默认值
		Assert.assertEquals(IsoEra.CE, ConvertUtil.convert(99, Era.class, IsoEra.CE));
		Assert.assertEquals(IsoEra.CE, ConvertUtil.convert(LocalDate.of(2026, 9, 14), Era.class, IsoEra.BCE));
	}
}
