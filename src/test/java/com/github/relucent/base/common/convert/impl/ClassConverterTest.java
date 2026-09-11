package com.github.relucent.base.common.convert.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ClassConverter} 单元测试
 */
public class ClassConverterTest {

    @Test
    public void testConvertClassName() {
        Object result = ClassConverter.INSTANCE.convert("java.lang.String", Class.class);
        Assert.assertEquals(String.class, result);
    }

    @Test
    public void testConvertIntegerClassName() {
        Object result = ClassConverter.INSTANCE.convert("java.lang.Integer", Class.class);
        Assert.assertEquals(Integer.class, result);
    }

    @Test
    public void testConvertPrimitiveArrayClassName() {
        // 原始类型数组
        Object result = ClassConverter.INSTANCE.convert("[I", Class.class);
        Assert.assertNotNull(result);
        Assert.assertTrue(((Class<?>) result).isArray());
    }

    @Test
    public void testConvertObjectArrayClassName() {
        Object result = ClassConverter.INSTANCE.convert("[Ljava.lang.String;", Class.class);
        Assert.assertEquals(String[].class, result);
    }

    @Test
    public void testConvertNull() {
        // BasicConverter 对 null 直接返回 null
        Assert.assertNull(ClassConverter.INSTANCE.convert(null, Class.class));
    }

    @Test
    public void testConvertUnknownClassName() {
        // 不存在的类，按转换失败处理返回 null（修复前会抛出异常）
        Assert.assertNull(ClassConverter.INSTANCE.convert("no.such.Clazz", Class.class));
    }

    @Test
    public void testConvertWithDefaultValue() {
        // 转换失败时返回默认值
        Assert.assertEquals(String.class, ClassConverter.INSTANCE.convert("no.such.Clazz", Class.class, String.class));
    }
}
