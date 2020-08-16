package com.github.relucent.base.common.convert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.time.DateUtil;

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
        Assert.assertEquals(Integer.valueOf(0), ConvertUtil.toInteger("0"));
        Assert.assertEquals(Integer.valueOf(-1), ConvertUtil.toInteger("-1"));
        Assert.assertEquals(Integer.valueOf(1024), ConvertUtil.toInteger("1024"));
        Assert.assertEquals(Integer.valueOf(32), ConvertUtil.toInteger(null, 32));
        Assert.assertEquals(Integer.valueOf(256), ConvertUtil.toInteger("XX", 256));
        Assert.assertNull(ConvertUtil.toInteger(null, null));
        Assert.assertNull(ConvertUtil.toInteger("", null));
    }

    @Test
    public void testToLong() {
        Assert.assertEquals(Long.valueOf(0L), ConvertUtil.toLong("0"));
        Assert.assertEquals(Long.valueOf(-1L), ConvertUtil.toLong("-1"));
        Assert.assertEquals(Long.valueOf(1024L), ConvertUtil.toLong("1024"));
        Assert.assertEquals(Long.valueOf(32L), ConvertUtil.toLong(null, 32L));
        Assert.assertEquals(Long.valueOf(256L), ConvertUtil.toLong("XX", 256L));
        Assert.assertNull(ConvertUtil.toLong(null, null));
        Assert.assertNull(ConvertUtil.toLong("", null));
    }

    @Test
    public void testToFloat() {
        Assert.assertEquals(Float.valueOf(0F), ConvertUtil.toFloat("0"));
        Assert.assertEquals(Float.valueOf(0.0F), ConvertUtil.toFloat("0.0"));
        Assert.assertEquals(Float.valueOf(-1F), ConvertUtil.toFloat("-1"));
        Assert.assertEquals(Float.valueOf(10.24F), ConvertUtil.toFloat("10.24"));
        Assert.assertEquals(Float.valueOf(32F), ConvertUtil.toFloat(null, 32F));
        Assert.assertEquals(Float.valueOf(32.1415926F), ConvertUtil.toFloat("XX", 32.1415926F));
        Assert.assertNull(ConvertUtil.toFloat(null, null));
        Assert.assertNull(ConvertUtil.toFloat("", null));
    }

    @Test
    public void testToDouble() {
        Assert.assertEquals(Double.valueOf(0D), ConvertUtil.toDouble("0"));
        Assert.assertEquals(Double.valueOf(0.0D), ConvertUtil.toDouble("0.0"));
        Assert.assertEquals(Double.valueOf(-1D), ConvertUtil.toDouble("-1"));
        Assert.assertEquals(Double.valueOf(10.24D), ConvertUtil.toDouble("10.24"));
        Assert.assertEquals(Double.valueOf(32D), ConvertUtil.toDouble(null, 32D));
        Assert.assertEquals(Double.valueOf(32.1415926D), ConvertUtil.toDouble("XX", 32.1415926D));
        Assert.assertNull(ConvertUtil.toDouble(null, null));
        Assert.assertNull(ConvertUtil.toDouble("", null));
    }

    @Test
    public void testToString() {
        SimpleDateFormat format = new SimpleDateFormat(DateUtil.ISO_DATETIME_FORMAT);
        Date now = new Date();
        Assert.assertEquals(format.format(now), ConvertUtil.toString(now));
        Assert.assertEquals("Default", ConvertUtil.toString(null, "Default"));
        Assert.assertNull(ConvertUtil.toString(null, null));
    }

    @Test
    public void testToDate() throws ParseException {
        String[] patterns = { "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyyMMdd", "yyyyMM", "yyyy" };
        for (String pattern : patterns) {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            String string = format.format(new Date());
            Date date = format.parse(string);
            Assert.assertEquals(date, ConvertUtil.toDate(string));
            Assert.assertEquals(date, ConvertUtil.toDate(null, date));
        }
    }

    @Test
    public void testToEnum() throws ParseException {
        Assert.assertEquals(TestEnum.A, ConvertUtil.toEnum("A", TestEnum.class));
        Assert.assertEquals(TestEnum.B, ConvertUtil.toEnum(1, TestEnum.class));
        Assert.assertEquals(TestEnum.B, ConvertUtil.toEnum(-1, TestEnum.class, TestEnum.B));
        Assert.assertEquals(TestEnum.A, ConvertUtil.toEnum("A", TestEnum.class, TestEnum.B));
        Assert.assertEquals(TestEnum.A, ConvertUtil.toEnum(null, TestEnum.class, TestEnum.A));
        Assert.assertNull(ConvertUtil.toEnum("N", TestEnum.class));
        Assert.assertNull(ConvertUtil.toEnum(null, TestEnum.class));
        Assert.assertNull(ConvertUtil.toEnum(-1, TestEnum.class));
        Assert.assertNull(ConvertUtil.toEnum(9, TestEnum.class));
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
        Assert.assertEquals(Integer.valueOf(Integer.MAX_VALUE), sample.getInteger(0));
        Assert.assertEquals(Long.valueOf(Long.MAX_VALUE), sample.getLong(1));
        Assert.assertEquals(Boolean.TRUE, sample.getBoolean(2));
        Assert.assertEquals("string", sample.getString(3));
        Assert.assertEquals(now, sample.getDate(4));
        Assert.assertEquals(null, sample.getString(5));
        Assert.assertEquals("DEFAULT", sample.getString(5, "DEFAULT"));
        Assert.assertArrayEquals(sample.toArray(), origin.toArray());
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
        Assert.assertEquals(Integer.valueOf(Integer.MAX_VALUE), sample.getInteger("int"));
        Assert.assertEquals(Long.valueOf(Long.MAX_VALUE), sample.getLong("long"));
        Assert.assertEquals(Boolean.TRUE, sample.getBoolean("boolean-true"));
        Assert.assertEquals(Boolean.FALSE, sample.getBoolean("boolean-false"));
        Assert.assertEquals("hello", sample.getString("string"));
        Assert.assertEquals("Default", sample.getString("nonexistent", "Default"));
        Assert.assertEquals(now, sample.getDate("date-now"));
        Assert.assertEquals("NULL_STRING", sample.getString(null));
        Assert.assertEquals("NULL_STRING", sample.getString("null"));
        Assert.assertTrue(origin.size() == sample.size());

        origin.remove(null);
        sample.remove(null);
        Assert.assertArrayEquals(origin.keySet().toArray(), sample.keySet().toArray());
    }
}
