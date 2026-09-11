package com.github.relucent.base.common.convert.impl;

import java.time.ZoneId;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ZoneIdConverter} 单元测试
 */
public class ZoneIdConverterTest {

    @Test
    public void testConvertFromZoneId() {
        ZoneId source = ZoneId.of("Asia/Shanghai");
        Assert.assertSame(source, ZoneIdConverter.INSTANCE.convert(source, ZoneId.class));
    }

    @Test
    public void testConvertFromTimeZone() {
        Assert.assertEquals(ZoneId.of("Asia/Shanghai"), ZoneIdConverter.INSTANCE.convert(TimeZone.getTimeZone("Asia/Shanghai"), ZoneId.class));
    }

    @Test
    public void testConvertFromText() {
        Assert.assertEquals(ZoneId.of("Asia/Shanghai"), ZoneIdConverter.INSTANCE.convert("Asia/Shanghai", ZoneId.class));
        Assert.assertEquals(ZoneId.of("UTC"), ZoneIdConverter.INSTANCE.convert("UTC", ZoneId.class));
    }

    @Test
    public void testConvertFromIllegalText() {
        // 无效时区 ID 按转换失败处理返回 null（修复前抛出 ZoneRulesException，绕过默认值）
        Assert.assertNull(ZoneIdConverter.INSTANCE.convert("garbage", ZoneId.class));
        Assert.assertNull(ZoneIdConverter.INSTANCE.convert("", ZoneId.class));
    }

    @Test
    public void testConvertNull() {
        Assert.assertNull(ZoneIdConverter.INSTANCE.convert(null, ZoneId.class));
    }

    @Test
    public void testConvertWithDefaultValue() {
        // 转换失败时返回默认值
        Assert.assertEquals(ZoneId.of("UTC"), ZoneIdConverter.INSTANCE.convert("garbage", ZoneId.class, ZoneId.of("UTC")));
        // 转换成功时忽略默认值
        Assert.assertEquals(ZoneId.of("Asia/Shanghai"), ZoneIdConverter.INSTANCE.convert("Asia/Shanghai", ZoneId.class, ZoneId.of("UTC")));
    }
}
