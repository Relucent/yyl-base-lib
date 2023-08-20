package com.github.relucent.base.common.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.github.relucent.base.common.constant.CharsetConstant;
import com.github.relucent.base.common.lang.AssertUtil;

/**
 * NIO中Path对象操作封装
 */
public class PathUtil {

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected PathUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 获取{@link Path}文件名
     * @param path {@link Path}
     * @return 文件名
     */
    public static String getName(Path path) {
        return path == null ? null : path.getFileName().toString();
    }

    /**
     * 判断是否为文件，如果file为null，则返回false
     * @param path 文件
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 如果为文件true
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    public static boolean isFile(Path path, boolean isFollowLinks) {
        if (path == null) {
            return false;
        }
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[] { LinkOption.NOFOLLOW_LINKS };
        return Files.isRegularFile(path, options);
    }

    /**
     * 判断是否为符号链接文件
     * @param path 被检查的文件
     * @return 是否为符号链接文件
     */
    public static boolean isSymlink(Path path) {
        return Files.isSymbolicLink(path);
    }

    /**
     * 判断是否为目录，如果file为null，则返回false<br>
     * 此方法不会追踪到软链对应的真实地址，即软链被当作文件
     * @param path {@link Path}
     * @return 是否为目录
     */
    public static boolean isDirectory(Path path) {
        return isDirectory(path, false);
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     * @param path {@link Path}
     * @param isFollowLinks 是否追踪到软链对应的真实地址
     * @return 是否为目录
     */
    public static boolean isDirectory(Path path, boolean isFollowLinks) {
        if (path == null) {
            return false;
        }
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[] { LinkOption.NOFOLLOW_LINKS };
        return Files.isDirectory(path, options);
    }

    /**
     * 判断目录是否为空
     * @param dir 目录
     * @return 是否为空
     */
    public static boolean isDirectoryEmpty(Path dir) {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
            return !dirStream.iterator().hasNext();
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 判断文件或目录是否存在
     * @param path 文件
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 是否存在
     */
    public static boolean exists(Path path, boolean isFollowLinks) {
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[] { LinkOption.NOFOLLOW_LINKS };
        return Files.exists(path, options);
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指Path对象是否指向同一个文件或文件夹
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @see Files#isSameFile(Path, Path)
     */
    public static boolean equals(Path file1, Path file2) {
        try {
            return Files.isSameFile(file1, file2);
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 判断是否存在且为非目录
     * <ul>
     * <li>如果path为{@code null}，返回{@code false}</li>
     * <li>如果path不存在，返回{@code false}</li>
     * </ul>
     * @param path {@link Path}
     * @param isFollowLinks 是否追踪到软链对应的真实地址
     * @return 如果为目录true
     */
    public static boolean isExistsAndNotDirectory(final Path path, final boolean isFollowLinks) {
        return exists(path, isFollowLinks) && false == isDirectory(path, isFollowLinks);
    }

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     * @param parent 父目录
     * @param sub 子目录
     * @return 子目录是否为父目录的子目录
     */
    public static boolean isSub(Path parent, Path sub) {
        return toAbsoluteNormal(sub).startsWith(toAbsoluteNormal(parent));
    }

    /**
     * 将Path路径转换为标准的绝对路径
     * @param path 文件或目录Path
     * @return 转换后的Path
     */
    public static Path toAbsoluteNormal(Path path) {
        AssertUtil.notNull(path);
        return path.toAbsolutePath().normalize();
    }

    /**
     * 获得文件的MimeType
     * @param path 文件
     * @return MimeType
     * @see Files#probeContentType(Path)
     */
    public static String getMimeType(Path path) {
        try {
            return Files.probeContentType(path);
        } catch (IOException ignore) {
            return null;
        }
    }

    /**
     * 获取文件属性
     * @param path 文件路径{@link Path}
     * @param isFollowLinks 是否跟踪到软链对应的真实路径
     * @return {@link BasicFileAttributes}
     */
    public static BasicFileAttributes getAttributes(Path path, boolean isFollowLinks) {
        if (path == null) {
            return null;
        }

        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[] { LinkOption.NOFOLLOW_LINKS };
        try {
            return Files.readAttributes(path, BasicFileAttributes.class, options);
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    // ==============================LoopMethods======================================
    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供path为文件，直接返回过滤结果
     * @param path 当前遍历文件或目录
     * @param filter 文件过滤规则对象，选择要保留的文件null表示接收全部文件
     * @return 文件列表
     */
    public static List<File> loopFiles(Path path, FileFilter filter) {
        return loopFiles(path, -1, filter);
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供path为文件，直接返回过滤结果
     * @param path 当前遍历文件或目录
     * @param maxDepth 遍历最大深度，-1表示不做限制
     * @param filter 文件过滤规则对象，选择要保留的文件，null表示接收全部文件
     * @return 文件列表
     */
    public static List<File> loopFiles(Path path, int maxDepth, FileFilter filter) {
        List<File> fileList = new ArrayList<>();

        // 目录不存在
        if (path == null || !Files.exists(path)) {
            return fileList;
        }

        // 不是目录
        if (!isDirectory(path)) {
            File file = path.toFile();
            if (filter == null || filter.accept(file)) {
                fileList.add(file);
            }
            return fileList;
        }

        // 遍历递归目录树
        walkFiles(path, maxDepth, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                final File file = path.toFile();
                // 符合规则添加到列表
                if (filter == null || filter.accept(file)) {
                    fileList.add(file);
                }
                // 继续遍历
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // 出现异常（不允许访问），跳过子树
                if (exc instanceof AccessDeniedException) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return super.visitFileFailed(file, exc);
            }
        });

        return fileList;
    }

    /**
     * 递归遍历path下的文件并做处理
     * @param start 起始路径，必须为目录
     * @param visitor {@link FileVisitor} 接口，用于自定义在访问文件时，访问目录前后等节点做的操作
     * @see Files#walkFileTree(Path, java.util.Set, int, FileVisitor)
     */
    public static void walkFiles(Path start, FileVisitor<? super Path> visitor) {
        walkFiles(start, -1, visitor);
    }

    /**
     * 递归遍历path下的文件并做处理
     * @param start 起始路径，必须为目录
     * @param maxDepth 最大遍历深度，-1表示不限制深度
     * @param visitor {@link FileVisitor} ，用于自定义在访问文件时，访问目录前后等节点做的操作
     * @see Files#walkFileTree(Path, java.util.Set, int, FileVisitor)
     */
    public static void walkFiles(Path start, int maxDepth, FileVisitor<? super Path> visitor) {

        // -1 表示不做限制
        if (maxDepth < 0) {
            maxDepth = Integer.MAX_VALUE;
        }

        try {
            Files.walkFileTree(start, EnumSet.noneOf(FileVisitOption.class), maxDepth, visitor);
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    // ==============================IoMethods========================================
    /**
     * 获得输入流
     * @param path Path
     * @return 输入流
     */
    public static BufferedInputStream openInputStream(Path path) {
        final InputStream input;
        try {
            input = Files.newInputStream(path);
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
        return IoUtil.buffer(input);
    }

    /**
     * 获得一个文件读取器
     * @param path 文件Path
     * @return BufferedReader对象
     */
    public static BufferedReader openReader(Path path) {
        return openReader(path, CharsetConstant.UTF_8);
    }

    /**
     * 获得一个文件读取器
     * @param path 文件Path
     * @param encoding 字符集
     * @return BufferedReader对象
     */
    public static BufferedReader openReader(Path path, Charset encoding) {
        return IoUtil.buffer(IoUtil.toReader(openInputStream(path), encoding));
    }

    /**
     * 读取文件的所有内容为byte数组
     * @param path 文件
     * @return byte数组
     */
    public static byte[] readBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 获得输出流
     * @param path Path
     * @return 输入流
     */
    public static BufferedOutputStream openOutputStream(Path path) {
        final OutputStream output;
        try {
            output = Files.newOutputStream(path);
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
        return IoUtil.buffer(output);
    }
}
