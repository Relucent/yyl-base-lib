package com.github.relucent.base.common.constant;

import java.io.File;

/**
 * 常规文件名和文件路径相关常量
 */
public class FilenameConstants {

    /** 文件扩展名分隔符 */
    public static final char EXTENSION_SEPARATOR = '.';
    /** 文件扩展名分隔符(字符串) */
    public static final String EXTENSION_SEPARATOR_STRING = Character.toString(EXTENSION_SEPARATOR);
    /** UNIX 文件名路径分隔符 */
    public static final char UNIX_SEPARATOR = '/';
    /** WINDOWS 文件名路径分隔符 */
    public static final char WINDOWS_SEPARATOR = '\\';
    /** 当前系统的文件名路径分隔符 */
    public static final char SYSTEM_SEPARATOR = File.separatorChar;
}
