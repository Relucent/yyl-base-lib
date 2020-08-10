package com.github.relucent.base.common.logging;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.logging.LoggerMessageFormatter;
import com.github.relucent.base.common.logging.LoggerMessageTuple;

public class LoggerMessageFormatterTest {
	@Test
	public void testArrayFormat() {
		LoggerMessageTuple tuple = LoggerMessageFormatter.arrayFormat("A{},B{},C{}", new Object[] { "1", "2", "3" });
		Assert.assertEquals("A1,B2,C3", tuple.getMessage());
		Assert.assertNull(tuple.getThrowable());
	}

	@Test
	public void testArrayFormatThrowable() {
		Throwable throwable = new Throwable();
		LoggerMessageTuple tuple = LoggerMessageFormatter.arrayFormat("A{},B{},C{}", new Object[] { "1", "2", throwable });
		Assert.assertEquals("A1,B2,C{}", tuple.getMessage());
		Assert.assertEquals(throwable, tuple.getThrowable());
	}
}
