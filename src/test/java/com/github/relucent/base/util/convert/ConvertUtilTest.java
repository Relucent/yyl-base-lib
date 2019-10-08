package com.github.relucent.base.util.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.util.collection.Listx;
import com.github.relucent.base.util.collection.Mapx;
import com.github.relucent.base.util.time.DateUtil;

public class ConvertUtilTest {

    @Test
    public void testToBoolean() {
        Assert.assertTrue(ConvertUtil.toBoolean("1"));
        Assert.assertTrue(ConvertUtil.toBoolean("Y"));
        Assert.assertTrue(ConvertUtil.toBoolean("T"));
        Assert.assertTrue(ConvertUtil.toBoolean("true"));
        Assert.assertTrue(ConvertUtil.toBoolean("on"));
        Assert.assertTrue(ConvertUtil.toBoolean("T", null));
        Assert.assertTrue(ConvertUtil.toBoolean("T", Boolean.TRUE));
        Assert.assertTrue(ConvertUtil.toBoolean("T", Boolean.FALSE));
        Assert.assertTrue(ConvertUtil.toBoolean(null, Boolean.TRUE));

        Assert.assertFalse(ConvertUtil.toBoolean("0"));
        Assert.assertFalse(ConvertUtil.toBoolean("N"));
        Assert.assertFalse(ConvertUtil.toBoolean("F"));
        Assert.assertFalse(ConvertUtil.toBoolean("false"));
        Assert.assertFalse(ConvertUtil.toBoolean("off"));
        Assert.assertFalse(ConvertUtil.toBoolean("F", null));
        Assert.assertFalse(ConvertUtil.toBoolean("F", Boolean.TRUE));
        Assert.assertFalse(ConvertUtil.toBoolean("F", Boolean.FALSE));
        Assert.assertFalse(ConvertUtil.toBoolean(null, Boolean.FALSE));

        Assert.assertNull(ConvertUtil.toBoolean("hello", null));
        Assert.assertNull(ConvertUtil.toBoolean("null", null));
        Assert.assertNull(ConvertUtil.toBoolean(null, null));
    }

    @Test
    public void testToInteger() {
        Assert.assertEquals(ConvertUtil.toInteger("0"), Integer.valueOf(0));
        Assert.assertEquals(ConvertUtil.toInteger("-1"), Integer.valueOf(-1));
        Assert.assertEquals(ConvertUtil.toInteger("1024"), Integer.valueOf(1024));
        Assert.assertEquals(ConvertUtil.toInteger(null, 32), Integer.valueOf(32));
        Assert.assertEquals(ConvertUtil.toInteger("XX", 256), Integer.valueOf(256));
        Assert.assertNull(ConvertUtil.toInteger(null, null));
        Assert.assertNull(ConvertUtil.toInteger("", null));
    }

    @Test
    public void testToLong() {
        Assert.assertEquals(ConvertUtil.toLong("0"), Long.valueOf(0L));
        Assert.assertEquals(ConvertUtil.toLong("-1"), Long.valueOf(-1L));
        Assert.assertEquals(ConvertUtil.toLong("1024"), Long.valueOf(1024L));
        Assert.assertEquals(ConvertUtil.toLong(null, 32L), Long.valueOf(32L));
        Assert.assertEquals(ConvertUtil.toLong("XX", 256L), Long.valueOf(256L));
        Assert.assertNull(ConvertUtil.toLong(null, null));
        Assert.assertNull(ConvertUtil.toLong("", null));
    }

    @Test
    public void testToFloat() {
        Assert.assertEquals(ConvertUtil.toFloat("0"), Float.valueOf(0F));
        Assert.assertEquals(ConvertUtil.toFloat("0.0"), Float.valueOf(0.0F));
        Assert.assertEquals(ConvertUtil.toFloat("-1"), Float.valueOf(-1F));
        Assert.assertEquals(ConvertUtil.toFloat("10.24"), Float.valueOf(10.24F));
        Assert.assertEquals(ConvertUtil.toFloat(null, 32F), Float.valueOf(32F));
        Assert.assertEquals(ConvertUtil.toFloat("XX", 32.1415926F), Float.valueOf(32.1415926F));
        Assert.assertNull(ConvertUtil.toFloat(null, null));
        Assert.assertNull(ConvertUtil.toFloat("", null));
    }

    @Test
    public void testToDouble() {
        Assert.assertEquals(ConvertUtil.toDouble("0"), Double.valueOf(0D));
        Assert.assertEquals(ConvertUtil.toDouble("0.0"), Double.valueOf(0.0D));
        Assert.assertEquals(ConvertUtil.toDouble("-1"), Double.valueOf(-1D));
        Assert.assertEquals(ConvertUtil.toDouble("10.24"), Double.valueOf(10.24D));
        Assert.assertEquals(ConvertUtil.toDouble(null, 32D), Double.valueOf(32D));
        Assert.assertEquals(ConvertUtil.toDouble("XX", 32.1415926D), Double.valueOf(32.1415926D));
        Assert.assertNull(ConvertUtil.toDouble(null, null));
        Assert.assertNull(ConvertUtil.toDouble("", null));
    }

    @Test
    public void testToString() {
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.ISO_DATETIME_FORMAT);
        Date now = new Date();
        Assert.assertEquals(ConvertUtil.toString(now), format.format(now));
        Assert.assertEquals(ConvertUtil.toString(null, "Default"), "Default");
        Assert.assertNull(ConvertUtil.toString(null, null));
    }

    @Test
    public void testToDate() throws ParseException {
        String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyyMMdd", "yyyyMM", "yyyy"};
        for (String pattern : patterns) {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            String string = format.format(new Date());
            Date date = format.parse(string);
            Assert.assertEquals(ConvertUtil.toDate(string), date);
            Assert.assertEquals(ConvertUtil.toDate(null, date), date);
        }
    }

    @Test
    public void testToEnum() throws ParseException {
        Assert.assertEquals(ConvertUtil.toEnum("A", TestEnum.class), TestEnum.A);
        Assert.assertEquals(ConvertUtil.toEnum("A", TestEnum.class, TestEnum.B), TestEnum.A);
        Assert.assertEquals(ConvertUtil.toEnum(null, TestEnum.class, TestEnum.A), TestEnum.A);
        Assert.assertNull(ConvertUtil.toEnum("N", TestEnum.class));
        Assert.assertNull(ConvertUtil.toEnum(null, TestEnum.class));
    }

    private static enum TestEnum {
        A, B, C
    }

    @Test
    public void testToList() {
        Date now = new Date();// sample
        List<Object> origin = new ArrayList<>();
        origin.add(Integer.MAX_VALUE);// 0
        origin.add(Long.MAX_VALUE);// 1
        origin.add(Boolean.TRUE);// 2
        origin.add("string");// 3
        origin.add(now);// 4
        origin.add(null);// 5

        Listx sample = ConvertUtil.toList(origin);
        Assert.assertEquals(sample.getInteger(0), Integer.valueOf(Integer.MAX_VALUE));
        Assert.assertEquals(sample.getLong(1), Long.valueOf(Long.MAX_VALUE));
        Assert.assertEquals(sample.getBoolean(2), Boolean.TRUE);
        Assert.assertEquals(sample.getString(3), "string");
        Assert.assertEquals(sample.getDate(4), now);
        Assert.assertEquals(sample.getString(5), null);
        Assert.assertEquals(sample.getString(5, "DEFAULT"), "DEFAULT");
        Assert.assertArrayEquals(origin.toArray(), sample.toArray());
    }

    @Test
    public void testToMap() {

        Date now = new Date();
        Map<String, Object> origin = new HashMap<>();
        origin.put("int", Integer.MAX_VALUE);
        origin.put("long", Long.MAX_VALUE);
        origin.put("boolean-true", Boolean.TRUE);
        origin.put("boolean-false", Boolean.FALSE);
        origin.put("string", "hello");
        origin.put("date-now", now);
        origin.put("date-string", now.toString());
        origin.put(null, "NULL_STRING");

        Mapx sample = ConvertUtil.toMap(origin);
        Assert.assertEquals(sample.getInteger("int"), Integer.valueOf(Integer.MAX_VALUE));
        Assert.assertEquals(sample.getLong("long"), Long.valueOf(Long.MAX_VALUE));
        Assert.assertEquals(sample.getBoolean("boolean-true"), Boolean.TRUE);
        Assert.assertEquals(sample.getBoolean("boolean-false"), Boolean.FALSE);
        Assert.assertEquals(sample.getString("string"), "hello");
        Assert.assertEquals(sample.getString("nonexistent", "Default"), "Default");
        Assert.assertEquals(sample.getDate("date-now"), now);
        Assert.assertEquals(sample.getString(null), "NULL_STRING");
        Assert.assertEquals(sample.getString("null"), "NULL_STRING");
        Assert.assertTrue(origin.size() == sample.size());

        origin.remove(null);
        sample.remove(null);
        Assert.assertArrayEquals(origin.keySet().toArray(), sample.keySet().toArray());
    }
}
