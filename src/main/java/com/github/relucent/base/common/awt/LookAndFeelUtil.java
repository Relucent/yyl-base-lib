package com.github.relucent.base.common.awt;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.github.relucent.base.common.logging.Logger;

/**
 * 图形界面的样式和风格
 */
public class LookAndFeelUtil {

    private static final Logger LOGGER = Logger.getLogger(LookAndFeelUtil.class);

    /**
     * 初始化外观（使用系统外观）
     */
    public static void useSystemLookAndFeelClassName() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.error("initLookAndFeel Error!", e);
        }
    }

    /**
     * 初始化外观（使用默认的跨平台外观）
     */
    public static void useCrossPlatformLookAndFeelClassName() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.error("initLookAndFeel Error!", e);
        }
    }

    /**
     * 获得可用外观
     * @return 可用外观信息
     */
    public static LookAndFeelInfo[] getInstalledLookAndFeels() {
        try {
            return UIManager.getInstalledLookAndFeels();
        } catch (Exception e) {
            return new LookAndFeelInfo[0];
        }
    }
}
