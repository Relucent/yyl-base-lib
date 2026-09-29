package com.github.relucent.base.common.awt;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * ScreenUtil 测试（仅校验空安全分支，不触碰真实显示资源）
 */
public class ScreenUtilTest {

    @Test
    public void testGetScreenDevicesNotNull() {
        assertNotNull(ScreenUtil.getScreenDevices());
    }
}
