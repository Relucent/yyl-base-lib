package com.github.relucent.base.common.constant;

/**
 * I/O 常量
 */
public class IoConstant {

    /** 默认缓冲区大小 4kb */
    public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /** 4 KB：默认小缓冲，通用文件 IO */
    public static final int BUFFER_SIZE_4KB = 1024 * 4;

    /** 8 KB：稍大缓冲，常用于网络流、文本处理 */
    public static final int BUFFER_SIZE_8KB = 1024 * 8;

    /** 16 KB：中等缓冲，适合中等文件 */
    public static final int BUFFER_SIZE_16KB = 1024 * 16;

    /** 64 KB：大文件传输常用 */
    public static final int BUFFER_SIZE_64KB = 1024 * 64;

    /** 128 KB：特大文件或高速拷贝时使用 */
    public static final int BUFFER_SIZE_128KB = 1024 * 128;

    /** 数据流末尾 */
    public static final int EOF = -1;

    private IoConstant() {
    }
}
