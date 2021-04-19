package com.github.relucent.base.common.web;

import java.io.OutputStream;

/**
 * 下载文件(接口类)
 */
public interface DownloadFile {
    /**
     * 获得文件名称
     * @return 文件名称
     */
    String getName();

    /**
     * 获得内容类型
     * @return 内容类型
     */
    String getContentType();

    /**
     * 将内容写到输出流
     * @param output 输出流
     */
    void writeTo(OutputStream output);

    /**
     * 获得文件长度
     * @return 文件长度
     */
    long getLength();
}
