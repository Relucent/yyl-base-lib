package com.github.relucent.base.common.awt;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.github.relucent.base.common.io.IoRuntimeException;

/**
 * 桌面工具类（平台相关）<br>
 * Desktop 类允许 Java 应用程序启动已在本机桌面上注册的关联应用程序。 <br>
 * @see Desktop
 */
public class DesktopUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected DesktopUtil() {
    }

    /**
     * 获得{@link Desktop}
     * @see Desktop#getDesktop()
     * @return {@link Desktop}
     */
    public static Desktop getDesktop() {
        return Desktop.getDesktop();
    }

    /**
     * 使用系统关联的应用程序打开文件
     * @param file 需要打开的文件
     */
    public static void open(File file) {
        Desktop desktop = getDesktop();
        try {
            desktop.open(file);
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    /**
     * 使用系统关联的应用程序编辑文件
     * @param file 文件
     */
    public static void edit(File file) {
        Desktop desktop = getDesktop();
        try {
            desktop.edit(file);
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    /**
     * 使用平台默认浏览器打开指定URL地址
     * @param url URL地址
     */
    public static void browse(String url) {
        browse(toURI(url));
    }

    /**
     * 使用平台默认浏览器打开指定URI地址
     * @param uri URI地址
     */
    public static void browse(URI uri) {
        Desktop desktop = getDesktop();
        try {
            desktop.browse(uri);
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    /**
     * 使用系统关联的打印命令打印文件
     * @param file 文件
     */
    public static void print(File file) {
        final Desktop desktop = getDesktop();
        try {
            desktop.print(file);
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    /**
     * 启动用户默认邮件的邮件撰写窗口打开邮件
     * @param mailAddress 邮件地址
     */
    public static void mail(String mailAddress) {
        final Desktop desktop = getDesktop();
        try {
            desktop.mail(toURI(mailAddress));
        } catch (IOException e) {
            throw new IoRuntimeException(e);
        }
    }

    /**
     * 转字符串为URI
     * @param location 字符串路径
     * @return URI
     */
    private static URI toURI(String location) {
        try {
            return new URI(location);
        } catch (URISyntaxException e) {
            throw new IoRuntimeException(e);
        }
    }
}
