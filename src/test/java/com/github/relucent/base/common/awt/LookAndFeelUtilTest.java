package com.github.relucent.base.common.awt;

import javax.swing.UIManager.LookAndFeelInfo;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * LookAndFeelUtil 测试（不依赖真实显示设备）
 */
public class LookAndFeelUtilTest {

    @Test
    public void testGetInstalledLookAndFeelsNotNull() {
        LookAndFeelInfo[] infos = LookAndFeelUtil.getInstalledLookAndFeels();
        assertNotNull(infos);
    }
}
