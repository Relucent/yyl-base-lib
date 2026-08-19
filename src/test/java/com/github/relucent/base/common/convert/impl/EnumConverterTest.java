package com.github.relucent.base.common.convert.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link EnumConverter} 单元测试
 */
public class EnumConverterTest {

    private enum TestEnum {
        A, B, C
    }

    @Test
    public void testConvertNull() {
        Assert.assertNull(EnumConverter.INSTANCE.convert(null, TestEnum.class));
    }

    @Test
    public void testConvertSameType() {
        Assert.assertEquals(TestEnum.A, EnumConverter.INSTANCE.convert(TestEnum.A, TestEnum.class));
        Assert.assertEquals(TestEnum.C, EnumConverter.INSTANCE.convert(TestEnum.C, TestEnum.class));
    }

    @Test
    public void testConvertNumber() {
        // 序号 0 → A, 1 → B
        Assert.assertEquals(TestEnum.A, EnumConverter.INSTANCE.convert(0, TestEnum.class));
        Assert.assertEquals(TestEnum.B, EnumConverter.INSTANCE.convert(1, TestEnum.class));
        Assert.assertEquals(TestEnum.C, EnumConverter.INSTANCE.convert(2, TestEnum.class));
        // 越界序号返回 null
        Assert.assertNull(EnumConverter.INSTANCE.convert(9, TestEnum.class));
    }

    @Test
    public void testConvertStringName() {
        Assert.assertEquals(TestEnum.A, EnumConverter.INSTANCE.convert("A", TestEnum.class));
        Assert.assertEquals(TestEnum.B, EnumConverter.INSTANCE.convert("B", TestEnum.class));
    }

    @Test
    public void testConvertStringDigits() {
        // 字符串数字按序号解析
        Assert.assertEquals(TestEnum.A, EnumConverter.INSTANCE.convert("0", TestEnum.class));
        Assert.assertEquals(TestEnum.B, EnumConverter.INSTANCE.convert("1", TestEnum.class));
    }

    @Test
    public void testConvertInvalidString() {
        // 无法匹配的字符串返回 null
        Assert.assertNull(EnumConverter.INSTANCE.convert("X", TestEnum.class));
    }

    @Test
    public void testConvertNonEnumTarget() {
        // 目标类型不是枚举,且源不是目标类型 → 返回 null
        Assert.assertNull(EnumConverter.INSTANCE.convert(123, TestEnum.class));
    }
}
