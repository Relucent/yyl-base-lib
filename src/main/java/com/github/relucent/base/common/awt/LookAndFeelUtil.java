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
        setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    /**
     * 初始化外观（使用默认的跨平台外观）
     */
    public static void useCrossPlatformLookAndFeelClassName() {
        setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    }

    /**
     * 初始化外观（指定外观实现类名）
     * @param className 外观实现类名，如 {@link UIManager#getSystemLookAndFeelClassName()}
     */
    public static void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
        } catch (Exception e) {
            LOGGER.error("initLookAndFeel Error! className=" + className, e);
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
