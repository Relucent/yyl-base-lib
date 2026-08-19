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
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

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
}
