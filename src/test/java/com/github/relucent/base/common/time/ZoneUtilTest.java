package com.github.relucent.base.common.time;

import java.time.ZoneId;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ZoneUtil} 单元测试<br>
 * 注意：默认时区是全局静态状态，测试中修改后必须还原，避免污染其他测试
 */
public class ZoneUtilTest {

    @After
    public void after() {
        // 无论测试是否失败，都还原为系统默认时区
        ZoneUtil.resetDefaultZoneId();
    }

    @Test
    public void testGetDefaultZoneIdNotNull() {
        Assert.assertNotNull(ZoneUtil.getDefaultZoneId());
    }

    @Test
    public void testSetAndGetDefaultZoneId() {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        ZoneUtil.setDefaultZoneId(zoneId);
        Assert.assertEquals(zoneId, ZoneUtil.getDefaultZoneId());
    }

    @Test
    public void testSetDefaultZoneIdNullResetsToSystemDefault() {
        ZoneUtil.setDefaultZoneId(ZoneId.of("America/New_York"));
        ZoneUtil.setDefaultZoneId(null);
        Assert.assertEquals(ZoneId.systemDefault(), ZoneUtil.getDefaultZoneId());
    }

    @Test
    public void testResetDefaultZoneIdReturnsPrevious() {
        ZoneId zoneId = ZoneId.of("Asia/Tokyo");
        ZoneUtil.setDefaultZoneId(zoneId);
        ZoneId previous = ZoneUtil.resetDefaultZoneId();
        Assert.assertEquals(zoneId, previous);
        Assert.assertEquals(ZoneId.systemDefault(), ZoneUtil.getDefaultZoneId());
    }

    @Test
    public void testResetDefaultZoneIdIsIdempotent() {
        ZoneUtil.resetDefaultZoneId();
        Assert.assertEquals(ZoneId.systemDefault(), ZoneUtil.getDefaultZoneId());
        // 再次重置仍然返回系统默认时区
        Assert.assertEquals(ZoneId.systemDefault(), ZoneUtil.resetDefaultZoneId());
        Assert.assertEquals(ZoneId.systemDefault(), ZoneUtil.getDefaultZoneId());
    }
}
