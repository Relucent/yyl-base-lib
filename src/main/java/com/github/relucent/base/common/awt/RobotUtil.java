package com.github.relucent.base.common.awt;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * {@link Robot} 工具类<br>
 * {@link Robot} 用于生成本地系统输入事件，以用于测试自动化、自运行演示以及需要控制鼠标和键盘的其他应用程序。 <br>
 */
public class RobotUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected RobotUtil() {
    }

    /**
     * 获取 Robot 单例实例
     * @return {@link Robot}单例对象
     */
    public static synchronized Robot getRobot() {
        return RobotHolder.INSTANCE;
    }

    /**
     * 模拟鼠标移动<br>
     * @param x 移动到的x坐标
     * @param y 移动到的y坐标
     */
    public static void mouseMove(int x, int y) {
        Robot robot = getRobot();
        robot.mouseMove(x, y);
    }

    /**
     * 模拟单击<br>
     * 鼠标单击包括鼠标左键的按下和释放
     */
    public static void click() {
        Robot robot = getRobot();
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    /**
     * 模拟右键单击<br>
     * 鼠标单击包括鼠标右键的按下和释放
     */
    public static void rightClick() {
        Robot robot = getRobot();
        robot.mousePress(InputEvent.BUTTON3_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
    }

    /**
     * 模拟鼠标滚轮滚动
     * @param wheelAmt 滚动数，负数表示向前滚动，正数向后滚动
     */
    public static void mouseWheel(int wheelAmt) {
        Robot robot = getRobot();
        robot.mouseWheel(wheelAmt);
    }

    /**
     * 模拟键盘点击， 包括键盘的按下和释放
     * @param keyCode 按键码列表，见{@link java.awt.event.KeyEvent}
     */
    public static void keyClick(int keyCode) {
        Robot robot = getRobot();
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }

    /**
     * shift+ 按键
     * @param key 按键
     */
    public static void keyPressWithShift(int key) {
        Robot robot = getRobot();
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(key);
        robot.keyRelease(key);
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    /**
     * ctrl+ 按键
     * @param key 按键
     */
    public static void keyPressWithCtrl(int key) {
        Robot robot = getRobot();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(key);
        robot.keyRelease(key);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /**
     * alt+ 按键
     * @param key 按键
     */
    public static void keyPressWithAlt(int key) {
        Robot robot = getRobot();
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(key);
        robot.keyRelease(key);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    /**
     * 截取全屏
     * @return 截屏的图片
     */
    public static BufferedImage captureScreen() {
        Dimension dimension = ScreenUtil.getScreenSize();
        return captureScreen(new Rectangle(dimension.width, dimension.height));
    }

    /**
     * 截屏
     * @param screenRect 截屏的矩形区域
     * @return 截屏的图片
     */
    public static BufferedImage captureScreen(Rectangle screenRect) {
        Robot robot = getRobot();
        return robot.createScreenCapture(screenRect);
    }

    /** 单例模式 */
    private static class RobotHolder {
        static final Robot INSTANCE;
        static {
            Robot robot = null;
            try {
                robot = new Robot();
            } catch (AWTException e) {
            }
            INSTANCE = robot;
        }
    }
}
