package com.github.relucent.base.common.io;

import com.github.relucent.base.common.constant.FilenameConstant;

/**
 * 常规文件名和文件路径操作工作类。
 */
public class FilenameUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected FilenameUtil() {
    }

    /**
     * 获得文件名称
     * @param path 文件路径
     * @return 文件名称
     */
    public static String getName(String path) {
        if (path == null) {
            return null;
        }
        int index = indexOfLastSeparator(path);
        return path.substring(index + 1);
    }

    /**
     * 获得基础文件名，不包含文件路径和后缀的文件名。
     * 
     * <pre>
     * a/b/c.txt → c
     * a.txt     → a
     * a/b/c     → c
     * a/b/c/    → ""
     * </pre>
     * 
     * @param path 文件路径
     * @return 基础文件名，如果不存在，则为空字符串
     */
    public static String getBaseName(String path) {
        return removeExtension(getName(path));
    }

    /**
     * 获得文件扩展名。 返回文件名最后一个点后的文本部分，点后不能有目录分隔符。
     * 
     * <pre>
     * foo.txt      → "txt"
     * a/b/c.jpg    → "jpg"
     * a/b.txt/c    → ""
     * a/b/c        → ""
     * </pre>
     * 
     * @param path 文件路径
     * @return 文件的扩展名，如果路径为{@code null}返回{@code null}，如果没有扩展名返回空字符串
     */
    public static String getExtension(String path) {
        if (path == null) {
            return null;
        }
        int index = indexOfExtension(path);
        if (index == -1) {
            return "";
        } else {
            return path.substring(index + 1);
        }
    }

    /**
     * 获得文件相对路径
     * @param root 根目录
     * @param path 文件路径
     * @return 拆分后的相对路径
     */
    public static String relative(String root, String path) {
        root = separatorsToUnix(root);
        path = separatorsToUnix(path);
        if (root == null || path == null) {
            return path;
        }
        if (path.indexOf(root) == 0) {
            return new String(path.substring(root.length()));
        }
        return path;
    }

    /**
     * 规范化文件路径，将所有分隔符转换为正斜杠(/)的Unix分隔符
     * @param path 规范化文件路径
     * @return 符合规范的文件路径
     */
    public static String separatorsToUnix(String path) {
        if ((path == null) || (path.indexOf(FilenameConstant.WINDOWS_SEPARATOR) == -1)) {
            return path;
        }
        return path.replace(FilenameConstant.WINDOWS_SEPARATOR, FilenameConstant.UNIX_SEPARATOR);
    }

    /**
     * 获得去除文件扩展名的文件路径。<br>
     * 
     * <pre>
     * foo.txt    → foo
     * a\b\c.jpg  → a\b\c
     * a\b\c      → a\b\c
     * a.b\c      → a.b\c
     * </pre>
     * <p>
     * @param path 文件路径
     * @return 去除文件扩展名的文件路径，如果文件路径为null则返回null。
     */
    public static String removeExtension(String path) {
        if (path == null) {
            return null;
        }
        int index = indexOfExtension(path);
        if (index == -1) {
            return path;
        } else {
            return path.substring(0, index);
        }
    }

    /**
     * 获得文件名最后一个路径分割符的索引
     * @param path 文件路径
     * @return 最后一个路径分割的索引
     */
    private static int indexOfLastSeparator(String path) {
        if (path == null) {
            return -1;
        }
        int lastUnixPos = path.lastIndexOf(FilenameConstant.UNIX_SEPARATOR);
        int lastWindowsPos = path.lastIndexOf(FilenameConstant.WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * 返回文件路径扩展名分隔符字符的索引
     * @param path 文件路径
     * @return 文件路径扩展名分隔符字符的索引，如果文件没有扩展名返回 -1
     */
    private static int indexOfExtension(String path) {
        if (path == null) {
            return -1;
        }
        int extensionPos = path.lastIndexOf(FilenameConstant.EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(path);
        return lastSeparator > extensionPos ? -1 : extensionPos;
    }
}
