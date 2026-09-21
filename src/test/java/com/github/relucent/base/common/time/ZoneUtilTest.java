package com.github.relucent.base.common.time;

import java.time.ZoneId;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ZoneUtil} 单元测试<br>
 */
public class ZoneUtilTest {

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
}
