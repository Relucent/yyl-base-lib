package com.github.relucent.base.common.time.format;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link FastDateFormat} 单元测试<br>
 * 重点验证实例缓存、{@code equals}/{@code hashCode} 语义以及线程安全性
 */
public class FastDateFormatTest {

    /** 东八区（无夏令时，偏移固定） */
    private static final TimeZone GMT_PLUS_8 = TimeZone.getTimeZone("GMT+08:00");
    /** 固定时刻：2021-05-03T02:13:20Z */
    private static final long MILLIS = 1620008000000L;

    @Test
    public void testGetInstanceCached() {
        FastDateFormat a = FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8, Locale.ENGLISH);
        FastDateFormat b = FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8, Locale.ENGLISH);
        // 相同参数应命中缓存，返回同一实例
        Assert.assertSame(a, b);
    }

    @Test
    public void testGetInstanceDifferentZoneNotSame() {
        FastDateFormat a = FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8, Locale.ENGLISH);
        FastDateFormat b = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        Assert.assertNotSame(a, b);
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void testGetInstanceDefault() {
        Assert.assertNotNull(FastDateFormat.getInstance());
        Assert.assertNotNull(FastDateFormat.getInstance("yyyy-MM-dd"));
        Assert.assertNotNull(FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8));
        Assert.assertNotNull(FastDateFormat.getInstance("yyyy-MM-dd", Locale.ENGLISH));
    }

    @Test
    public void testGetDateInstance() {
        FastDateFormat format = FastDateFormat.getDateInstance(FastDateFormat.SHORT, GMT_PLUS_8, Locale.ENGLISH);
        Assert.assertNotNull(format);
        Assert.assertNotNull(format.getPattern());
        Assert.assertFalse(format.format(new Date(MILLIS)).isEmpty());
    }

    @Test
    public void testGetTimeInstance() {
        FastDateFormat format = FastDateFormat.getTimeInstance(FastDateFormat.MEDIUM, GMT_PLUS_8, Locale.ENGLISH);
        Assert.assertNotNull(format);
        Assert.assertFalse(format.format(new Date(MILLIS)).isEmpty());
    }

    @Test
    public void testGetDateTimeInstance() {
        // 使用 MEDIUM 时间样式（包含秒），保证格式化结果可以无损回解析
        FastDateFormat format = FastDateFormat.getDateTimeInstance(FastDateFormat.MEDIUM, FastDateFormat.MEDIUM, GMT_PLUS_8, Locale.ENGLISH);
        Assert.assertNotNull(format);
        String text = format.format(new Date(MILLIS));
        Assert.assertFalse(text.isEmpty());
        try {
            Assert.assertEquals(MILLIS, format.parse(text).getTime());
        } catch (ParseException e) {
            Assert.fail("风格化格式化结果无法回解析: " + text + ", cause=" + e);
        }
    }

    @Test
    public void testGetDateTimeInstanceShortDropsSeconds() {
        // SHORT 时间样式不含秒，回解析只能还原到分钟，此处固化该预期行为
        FastDateFormat format = FastDateFormat.getDateTimeInstance(FastDateFormat.SHORT, FastDateFormat.SHORT, GMT_PLUS_8, Locale.ENGLISH);
        try {
            String text = format.format(new Date(MILLIS));
            long parsed = format.parse(text).getTime();
            Assert.assertTrue("SHORT 样式应丢弃秒级精度: " + text, parsed <= MILLIS);
            Assert.assertTrue("SHORT 样式误差应小于一分钟", MILLIS - parsed < 60_000L);
        } catch (ParseException e) {
            Assert.fail("风格化格式化结果无法回解析, cause=" + e);
        }
    }

    @Test
    public void testFormatRoundTrip() throws ParseException {
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss", GMT_PLUS_8, Locale.ENGLISH);
        String text = format.format(MILLIS);
        Assert.assertEquals("2021-05-03T10:13:20", text);
        Assert.assertEquals(MILLIS, format.parse(text).getTime());
    }

    @Test
    public void testFormatObject() {
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8, Locale.ENGLISH);
        StringBuffer buffer = new StringBuffer();
        StringBuffer result = format.format(Long.valueOf(MILLIS), buffer, null);
        Assert.assertSame(buffer, result);
        Assert.assertEquals("2021-05-03", result.toString());
    }

    @Test
    public void testAccessors() {
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8, Locale.ENGLISH);
        Assert.assertEquals("yyyy-MM-dd", format.getPattern());
        Assert.assertEquals(GMT_PLUS_8, format.getTimeZone());
        Assert.assertEquals(Locale.ENGLISH, format.getLocale());
        Assert.assertTrue(format.getMaxLengthEstimate() >= "2021-05-03".length());
    }

    @Test
    public void testEqualsAndHashCode() {
        FastDateFormat a = FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8, Locale.ENGLISH);
        FastDateFormat b = FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8, Locale.ENGLISH);
        FastDateFormat c = FastDateFormat.getInstance("yyyy-MM", GMT_PLUS_8, Locale.ENGLISH);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a, c);
        Assert.assertFalse(a.equals(null));
        Assert.assertFalse(a.equals("yyyy-MM-dd"));
    }

    @Test
    public void testToString() {
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd", GMT_PLUS_8, Locale.ENGLISH);
        String text = format.toString();
        Assert.assertTrue(text.contains("yyyy-MM-dd"));
        Assert.assertTrue(text.contains("GMT+08:00"));
    }

    @Test
    public void testInvalidPattern() {
        try {
            FastDateFormat.getInstance(null, GMT_PLUS_8, Locale.ENGLISH);
            Assert.fail("pattern 为 null 应当抛出异常");
        } catch (NullPointerException | IllegalArgumentException expected) {
            // 符合预期
        }
    }

    /**
     * 线程安全性：同一实例被多线程并发使用，结果应完全一致（{@link FastDateFormat} 的核心卖点）
     */
    @Test
    public void testThreadSafe() throws Exception {
        final FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ssZ", GMT_PLUS_8, Locale.ENGLISH);
        final String expected = format.format(MILLIS);
        final int threads = 8;
        final int loops = 2000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Callable<String>> tasks = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                tasks.add(new Callable<String>() {
                    @Override
                    public String call() {
                        String last = null;
                        for (int j = 0; j < loops; j++) {
                            last = format.format(MILLIS);
                            if (!expected.equals(last)) {
                                return last;
                            }
                        }
                        return last;
                    }
                });
            }
            for (Future<String> future : executor.invokeAll(tasks, 60, TimeUnit.SECONDS)) {
                Assert.assertEquals(expected, future.get());
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
