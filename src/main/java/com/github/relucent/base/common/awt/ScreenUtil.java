package com.github.relucent.base.common.awt;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * 屏幕相关（当前显示设置）工具类
 */
public class ScreenUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected ScreenUtil() {
    }

    /**
     * 获取屏幕的尺寸
     * @return 屏幕的尺寸
     */
    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * 获取屏幕的矩形
     * @return 屏幕的矩形
     */
    public static Rectangle getRectangle() {
        Dimension dimension = getScreenSize();
        return new Rectangle(dimension.width, dimension.height);
    }

    /**
     * 设置窗口对象(Frame)屏幕居中
     * @param frame 窗口对象
     */
    public static void setCenterLocation(Frame frame) {
        Dimension screenSize = getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
    }
}
