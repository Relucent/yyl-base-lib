package com.github.relucent.base.common.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class DurationUtilTest {

	@Test
	public void testGetNanosOfMilli() {
		// 1234毫秒 + 567000纳秒 → 毫秒以下部分为 567000
		Duration duration = Duration.ofMillis(1234).plusNanos(567000);
		Assert.assertEquals(1234, duration.toMillis());
		Assert.assertEquals(567000, DurationUtil.getNanosOfMilli(duration));
	}

	@Test
	public void testGetNanosOfMilliBoundary() {
		Assert.assertEquals(0, DurationUtil.getNanosOfMilli(Duration.ofSeconds(1)));
		Assert.assertEquals(0, DurationUtil.getNanosOfMilli(Duration.ofMillis(1)));
		Assert.assertEquals(999999, DurationUtil.getNanosOfMilli(Duration.ofNanos(999999)));
		Assert.assertEquals(1, DurationUtil.getNanosOfMilli(Duration.ofNanos(1000001)));
	}

	@Test
	public void testGetNanosOfMilliZero() {
		Assert.assertEquals(0, DurationUtil.getNanosOfMilli(Duration.ZERO));
	}

	@Test
	public void testIsPositive() {
		Assert.assertTrue(DurationUtil.isPositive(Duration.ofSeconds(1)));
		Assert.assertTrue(DurationUtil.isPositive(Duration.ofNanos(1)));
		Assert.assertFalse(DurationUtil.isPositive(Duration.ZERO));
		Assert.assertFalse(DurationUtil.isPositive(Duration.ofSeconds(-1)));
		Assert.assertFalse(DurationUtil.isPositive(Duration.ofNanos(-1)));
	}

	@Test
	public void testToDuration() {
		Assert.assertEquals(Duration.ofNanos(7), DurationUtil.toDuration(7, TimeUnit.NANOSECONDS));
		Assert.assertEquals(Duration.ofNanos(7000), DurationUtil.toDuration(7, TimeUnit.MICROSECONDS));
		Assert.assertEquals(Duration.ofMillis(500), DurationUtil.toDuration(500, TimeUnit.MILLISECONDS));
		Assert.assertEquals(Duration.ofSeconds(3), DurationUtil.toDuration(3, TimeUnit.SECONDS));
		Assert.assertEquals(Duration.ofMinutes(90), DurationUtil.toDuration(90, TimeUnit.MINUTES));
		Assert.assertEquals(Duration.ofHours(2), DurationUtil.toDuration(2, TimeUnit.HOURS));
		Assert.assertEquals(Duration.ofDays(1), DurationUtil.toDuration(1, TimeUnit.DAYS));
	}

	@Test
	public void testToDurationNegative() {
		Assert.assertEquals(Duration.ofSeconds(-3), DurationUtil.toDuration(-3, TimeUnit.SECONDS));
	}

	@Test
	public void testToChronoUnit() {
		Assert.assertEquals(ChronoUnit.NANOS, DurationUtil.toChronoUnit(TimeUnit.NANOSECONDS));
		Assert.assertEquals(ChronoUnit.MICROS, DurationUtil.toChronoUnit(TimeUnit.MICROSECONDS));
		Assert.assertEquals(ChronoUnit.MILLIS, DurationUtil.toChronoUnit(TimeUnit.MILLISECONDS));
		Assert.assertEquals(ChronoUnit.SECONDS, DurationUtil.toChronoUnit(TimeUnit.SECONDS));
		Assert.assertEquals(ChronoUnit.MINUTES, DurationUtil.toChronoUnit(TimeUnit.MINUTES));
		Assert.assertEquals(ChronoUnit.HOURS, DurationUtil.toChronoUnit(TimeUnit.HOURS));
		Assert.assertEquals(ChronoUnit.DAYS, DurationUtil.toChronoUnit(TimeUnit.DAYS));
	}

	@Test(expected = NullPointerException.class)
	public void testToChronoUnitNull() {
		DurationUtil.toChronoUnit(null);
	}

	@Test
	public void testZeroIfNull() {
		Assert.assertEquals(Duration.ZERO, DurationUtil.zeroIfNull(null));
		Duration duration = Duration.ofSeconds(5);
		Assert.assertSame(duration, DurationUtil.zeroIfNull(duration));
	}
}
