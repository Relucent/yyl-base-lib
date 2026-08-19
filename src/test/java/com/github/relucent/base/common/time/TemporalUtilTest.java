package com.github.relucent.base.common.time;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link TemporalUtil} 单元测试
 */
public class TemporalUtilTest {

	private static final LocalDateTime START = LocalDateTime.of(2021, 5, 3, 10, 13, 20);
	private static final LocalDateTime END = LocalDateTime.of(2021, 5, 4, 12, 14, 21);

	@Test
	public void testBetweenDuration() {
		Duration duration = TemporalUtil.between(START, END);
		Assert.assertEquals(Duration.ofDays(1).plusHours(2).plusMinutes(1).plusSeconds(1), duration);
	}

	@Test
	public void testBetweenNegative() {
		Duration duration = TemporalUtil.between(END, START);
		Assert.assertTrue(duration.isNegative());
	}

	@Test
	public void testBetweenWithChronoUnit() {
		Assert.assertEquals(1, TemporalUtil.between(START, END, ChronoUnit.DAYS));
		Assert.assertEquals(26, TemporalUtil.between(START, END, ChronoUnit.HOURS));
		Assert.assertEquals(1561, TemporalUtil.between(START, END, ChronoUnit.MINUTES));
	}

	@Test
	public void testBetweenWithDateBasedUnit() {
		// LocalDateTime 支持日期单位（MONTHS/YEARS）的差值计算
		Assert.assertEquals(0, TemporalUtil.between(START, END, ChronoUnit.MONTHS));
		Assert.assertEquals(0, TemporalUtil.between(START, END, ChronoUnit.YEARS));
	}

	@Test
	public void testOffset() {
		LocalDateTime result = TemporalUtil.offset(START, 2, ChronoUnit.DAYS);
		Assert.assertEquals(2021, result.getYear());
		Assert.assertEquals(5, result.getMonthValue());
		Assert.assertEquals(5, result.getDayOfMonth());
		Assert.assertEquals(10, result.getHour());
	}

	@Test
	public void testOffsetNegative() {
		LocalDateTime result = TemporalUtil.offset(START, -3, ChronoUnit.MONTHS);
		Assert.assertEquals(2, result.getMonthValue());
		Assert.assertEquals(3, result.getDayOfMonth());
	}

	@Test
	public void testOffsetNull() {
		Assert.assertNull(TemporalUtil.offset(null, 1, ChronoUnit.DAYS));
	}

	@Test
	public void testOffsetLocalDate() {
		LocalDate result = TemporalUtil.offset(LocalDate.of(2021, 5, 3), 1, ChronoUnit.WEEKS);
		Assert.assertEquals(LocalDate.of(2021, 5, 10), result);
	}

	@Test
	public void testToChronoUnit() {
		Assert.assertEquals(ChronoUnit.DAYS, TemporalUtil.toChronoUnit(TimeUnit.DAYS));
		Assert.assertEquals(ChronoUnit.HOURS, TemporalUtil.toChronoUnit(TimeUnit.HOURS));
		Assert.assertEquals(ChronoUnit.MINUTES, TemporalUtil.toChronoUnit(TimeUnit.MINUTES));
		Assert.assertEquals(ChronoUnit.SECONDS, TemporalUtil.toChronoUnit(TimeUnit.SECONDS));
		Assert.assertEquals(ChronoUnit.MILLIS, TemporalUtil.toChronoUnit(TimeUnit.MILLISECONDS));
		Assert.assertEquals(ChronoUnit.MICROS, TemporalUtil.toChronoUnit(TimeUnit.MICROSECONDS));
		Assert.assertEquals(ChronoUnit.NANOS, TemporalUtil.toChronoUnit(TimeUnit.NANOSECONDS));
	}

	@Test
	public void testToChronoUnitNull() {
		Assert.assertNull(TemporalUtil.toChronoUnit(null));
	}

	@Test
	public void testToTimeUnit() {
		Assert.assertEquals(TimeUnit.DAYS, TemporalUtil.toTimeUnit(ChronoUnit.DAYS));
		Assert.assertEquals(TimeUnit.HOURS, TemporalUtil.toTimeUnit(ChronoUnit.HOURS));
		Assert.assertEquals(TimeUnit.MINUTES, TemporalUtil.toTimeUnit(ChronoUnit.MINUTES));
		Assert.assertEquals(TimeUnit.SECONDS, TemporalUtil.toTimeUnit(ChronoUnit.SECONDS));
		Assert.assertEquals(TimeUnit.MILLISECONDS, TemporalUtil.toTimeUnit(ChronoUnit.MILLIS));
		Assert.assertEquals(TimeUnit.MICROSECONDS, TemporalUtil.toTimeUnit(ChronoUnit.MICROS));
		Assert.assertEquals(TimeUnit.NANOSECONDS, TemporalUtil.toTimeUnit(ChronoUnit.NANOS));
	}

	@Test
	public void testToTimeUnitNull() {
		Assert.assertNull(TemporalUtil.toTimeUnit(null));
	}

	@Test
	public void testToTimeUnitUnsupported() {
		try {
			TemporalUtil.toTimeUnit(ChronoUnit.MONTHS);
			Assert.fail("ChronoUnit.MONTHS 没有对应的 TimeUnit，应当抛出 IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			// 符合预期
		}
	}

	@Test
	public void testToChronoUnitRoundTrip() {
		for (TimeUnit unit : new TimeUnit[] { TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, //
				TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS }) {
			Assert.assertEquals(unit, TemporalUtil.toTimeUnit(TemporalUtil.toChronoUnit(unit)));
		}
	}

	@Test
	public void testOffsetToDayOfWeek() {
		// 静态方法 offsetTo 是推荐入口
		LocalDate monday = LocalDate.of(2021, 5, 3);// 周一
		// previous/next 均不包含当天
		Assert.assertEquals(LocalDate.of(2021, 4, 26), TemporalUtil.offset(monday, DayOfWeek.MONDAY, true));
		Assert.assertEquals(LocalDate.of(2021, 5, 10), TemporalUtil.offset(monday, DayOfWeek.MONDAY, false));
		Assert.assertEquals(LocalDate.of(2021, 5, 7), TemporalUtil.offset(monday, DayOfWeek.FRIDAY, false));
		Assert.assertEquals(LocalDate.of(2021, 4, 30), TemporalUtil.offset(monday, DayOfWeek.FRIDAY, true));
		// 也支持 LocalDateTime
		LocalDateTime start = LocalDateTime.of(2021, 5, 3, 10, 13, 20);
		Assert.assertEquals(LocalDateTime.of(2021, 5, 7, 10, 13, 20),
				TemporalUtil.offset(start, DayOfWeek.FRIDAY, false));
	}
}
