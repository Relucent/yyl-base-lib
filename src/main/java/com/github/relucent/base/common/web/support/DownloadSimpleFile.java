package com.github.relucent.base.common.web.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.web.DownloadFile;

/**
 * 下载文件类
 */
public class DownloadSimpleFile implements DownloadFile {

    /** 文件名称 */
    private String name;
    /** 内容类型 */
    private String contentType;
    /** 文件内容 */
    private InputStream input;
    /** 文件长度 */
    private long length;

    public DownloadSimpleFile(String name, String contentType, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.input = new ByteArrayInputStream(content);
        this.length = content.length;
    }

    public DownloadSimpleFile(String name, String contentType, InputStream input, long length) {
        this.name = name;
        this.contentType = contentType;
        this.input = input;
        this.length = length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void writeTo(OutputStream output) {
        try {
            IoUtil.copyLarge(input, output);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Override
    public long getLength() {
        return length;
    }
}
