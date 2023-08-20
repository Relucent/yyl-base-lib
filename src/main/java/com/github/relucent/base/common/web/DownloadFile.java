package com.github.relucent.base.common.web;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.relucent.base.common.io.IoUtil;

/**
 * 下载文件类
 */
public class DownloadFile implements AutoCloseable {

    /** 文件名称 */
    private String name;
    /** 内容类型 */
    private String contentType;
    /** 文件内容 */
    private InputStream input;
    /** 文件长度 */
    private long length;

    /**
     * 构造函数
     * @param name 文件名
     * @param contentType 内容类型
     * @param content 文件内容
     */
    public DownloadFile(String name, String contentType, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.input = new ByteArrayInputStream(content);
        this.length = content.length;
    }

    /**
     * 构造函数
     * @param name 文件名
     * @param contentType 内容类型
     * @param input 文件流
     * @param length 文件长度
     */
    public DownloadFile(String name, String contentType, InputStream input, long length) {
        this.name = name;
        this.contentType = contentType;
        this.input = input;
        this.length = length;
    }

    /**
     * 获得文件名称
     * @return 文件名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获得内容类型
     * @return 内容类型
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 获得文件长度
     * @return 文件长度
     */
    public long getLength() {
        return length;
    }

    /**
     * 将内容写到输出流
     * @param output 输出流
     */
    public void writeTo(OutputStream output) {
        try {
            IoUtil.copyLarge(input, output);
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * 关闭文件流
     */
    @Override
    public void close() throws Exception {
        IoUtil.closeQuietly(input);
    }
}
