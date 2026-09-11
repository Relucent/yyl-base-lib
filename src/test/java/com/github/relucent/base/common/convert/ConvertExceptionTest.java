package com.github.relucent.base.common.convert;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ConvertException} 单元测试
 */
public class ConvertExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        ConvertException exception = new ConvertException("message");
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void testConstructorWithCause() {
        Exception cause = new IllegalStateException("cause");
        ConvertException exception = new ConvertException(cause);
        Assert.assertSame(cause, exception.getCause());
    }

    @Test
    public void testConstructorWithMessageAndCause() {
        Exception cause = new IllegalStateException("cause");
        ConvertException exception = new ConvertException("message", cause);
        Assert.assertEquals("message", exception.getMessage());
        Assert.assertSame(cause, exception.getCause());
    }

    @Test
    public void testWrapConvertException() {
        // 本身已是转换异常时原样返回
        ConvertException origin = new ConvertException("origin");
        Assert.assertSame(origin, ConvertException.wrap(origin));
    }

    @Test
    public void testWrapOtherException() {
        Exception cause = new IllegalStateException("cause");
        ConvertException exception = ConvertException.wrap(cause);
        Assert.assertEquals("cause", exception.getMessage());
        Assert.assertSame(cause, exception.getCause());
    }

    @Test
    public void testIsRuntimeException() {
        Assert.assertTrue(RuntimeException.class.isAssignableFrom(ConvertException.class));
    }
}
