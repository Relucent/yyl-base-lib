package com.github.relucent.base.common.awt;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
     * <p>
     * 注意：基于主屏尺寸居中，多显示器环境请改用 {@link #getScreenDevices()} 取对应屏幕计算。
     * </p>
     * @param frame 窗口对象
     */
    public static void setCenterLocation(Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("The frame is not allowed to be null");
        }
        Dimension screenSize = getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
    }

    /**
     * 获取所有屏幕设备（多显示器环境）
     * <p>
     * {@link #getScreenSize()} / {@link #getRectangle()} 仅返回主屏尺寸； 多显示器环境下窗口居中请基于本方法返回的对应 {@link GraphicsDevice} 计算。
     * </p>
     * @return 屏幕设备数组（headless 环境下可能为空数组）
     */
    public static GraphicsDevice[] getScreenDevices() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return environment.getScreenDevices();
    }
}
