package com.github.relucent.base.common.web;

/**
 * 文件下载模式
 */
public enum DownloadMode {

    /** 附件 */
    ATTACHMENT("attachment"),
    /** 内联 */
    INLINE("inline");

    /** 标识值 */
    private String value;

    /**
     * 构造函数
     * @param value 标识值
     */
    private DownloadMode(String value) {
        this.value = value;
    }

    /**
     * 获得请求头完整的 content-disposition 值
     * @param filename 文件名称
     * @return 完整的 content-disposition 值
     */
    public String getContentDisposition(String filename) {
        return value + ";filename=" + filename;
    }
}