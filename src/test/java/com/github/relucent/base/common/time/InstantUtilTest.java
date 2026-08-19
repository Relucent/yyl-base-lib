package com.github.relucent.base.common.time;

import java.time.Instant;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link InstantUtil} 单元测试
 */
public class InstantUtilTest {

    @Test
    public void testFormatUtc() {
        Instant instant = Instant.ofEpochMilli(1620008000000L);
        Assert.assertEquals("2021-05-03T02:13:20Z", InstantUtil.formatUtc(instant));
    }

    @Test
    public void testFormatUtcWithMillis() {
        Instant instant = Instant.ofEpochMilli(1620008000123L);
        String text = InstantUtil.formatUtc(instant);
        Assert.assertTrue(text.startsWith("2021-05-03T02:13:20.123"));
        Assert.assertTrue(text.endsWith("Z"));
    }

    @Test
    public void testFormatUtcNull() {
        Assert.assertNull(InstantUtil.formatUtc(null));
    }

    @Test
    public void testToInstantFromDate() {
        Date date = new Date(1620008000000L);
        Assert.assertEquals(Instant.ofEpochMilli(1620008000000L), InstantUtil.toInstant(date));
    }

    @Test
    public void testToInstantFromDateNull() {
        Assert.assertNull(InstantUtil.toInstant((Date) null));
    }

    @Test
    public void testToInstantFromEpochMilli() {
        Assert.assertEquals(Instant.ofEpochMilli(1620008000000L), InstantUtil.toInstant(Long.valueOf(1620008000000L)));
    }

    @Test
    public void testToInstantFromEpochMilliNull() {
        Assert.assertNull(InstantUtil.toInstant((Long) null));
    }

    @Test
    public void testRoundTripDateAndInstant() {
        Date source = new Date(1234567890123L);
        Instant instant = InstantUtil.toInstant(source);
        Assert.assertEquals(source, new Date(instant.toEpochMilli()));
    }
}
