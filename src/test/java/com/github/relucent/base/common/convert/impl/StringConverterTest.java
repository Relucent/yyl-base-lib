package com.github.relucent.base.common.convert.impl;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;
import java.util.TimeZone;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

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

	@Test
	public void testConvertClob() throws Exception {
		Clob clob = new SerialClob("hello".toCharArray());
		Assert.assertEquals("hello", StringConverter.INSTANCE.convert(clob, String.class));
	}

	@Test
	public void testConvertBlob() throws Exception {
		Blob blob = new SerialBlob("hello".getBytes(StandardCharsets.UTF_8));
		Assert.assertEquals("hello", StringConverter.INSTANCE.convert(blob, String.class));
	}

	@Test
	public void testConvertClobReadFailed() {
		// Clob 读取失败按转换失败处理，返回 null 而不是抛出异常
		Clob clob = (Clob) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Clob.class },
				(proxy, method, args) -> {
					if ("getCharacterStream".equals(method.getName())) {
						throw new SQLException("mock read failure");
					}
					return null;
				});
		Assert.assertNull(StringConverter.INSTANCE.convert(clob, String.class));
	}

	@Test
	public void testConvertBlobReadFailed() {
		// Blob 读取失败按转换失败处理，返回 null 而不是抛出异常
		Blob blob = (Blob) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { Blob.class },
				(proxy, method, args) -> {
					if ("getBinaryStream".equals(method.getName())) {
						throw new SQLException("mock read failure");
					}
					return null;
				});
		Assert.assertNull(StringConverter.INSTANCE.convert(blob, String.class));
	}
}
