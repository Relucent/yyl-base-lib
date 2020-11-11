package com.github.relucent.base.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件工具类
 */
public class FileUtil {

    // -----------------------------------------------------------------------
    /**
     * 获得系统临时目录{@link File}
     * @return 获得系统临时目录
     */
    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }

    /**
     * 获得系统临时目录路径
     * @return 获得系统临时目录路径
     */
    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }

    // -----------------------------------------------------------------------
    /**
     * 打开文件输入流{@link FileInputStream}(用于从文件中读取数据)。<br>
     * @param file 要打开的文件, 不能为空 {@code null}
     * @return 文件的输入流 {@link FileOutputStream} (用于从文件中读取数据)
     * @throws FileNotFoundException 如果文件不存在
     * @throws IOException 如果文件是目录
     * @throws IOException 如果文件不能被读取
     */
    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    // -----------------------------------------------------------------------
    /**
     * 打开文件输出流{@link FileOutputStream}(用于将数据写入文件)。 如果文件不存在，将创建该文件；如果父目录不存在，则检查并创建父目录。<br>
     * @param file 要打开的文件, 不能为{@code null}
     * @return 文件输出流 {@link FileOutputStream}(用于将数据写入文件)
     * @throws IOException 如果指定的文件是一个目录
     * @throws IOException 如果指定的文件不可写入
     * @throws IOException 如果需要创建父目录但创建失败
     */
    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    /**
     * 打开文件输出流{@link FileOutputStream}(用于将数据写入文件)。 如果文件不存在，将创建该文件；如果父目录不存在，则检查并创建父目录。<br>
     * @param file 要打开的文件, 不能为{@code null}
     * @param append 如果为{@code true}, 字节将被添加到文件末尾，而不是覆盖。
     * @return 文件输出流 {@link FileOutputStream}(用于将数据写入文件)
     * @throws IOException 如果指定的文件是一个目录
     * @throws IOException 如果指定的文件不可写入
     * @throws IOException 如果需要创建父目录但创建失败
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    /**
     * 开放文件权限读写权限(rw/rw/rw)
     * @param file 文件
     */
    public static void chmod666(File file) {
        if (file.exists()) {
            // 读允许
            if (file.setReadable(true, false)) {
                // ignore
            }
            // 写允许
            if (file.setWritable(true, false)) {
                // ignore
            }
        }
    }
}
