package com.github.relucent.base.common.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 字符集常量
 */
public class CharsetConstants {

    /**
     * UNICODE 字符集 <br>
     * {@link java.nio.charset.StandardCharsets#US_ASCII} instead.
     */
    public static final Charset US_ASCII = StandardCharsets.US_ASCII;

    /**
     * ISO-8859-1 字符集 <br>
     * {@link java.nio.charset.StandardCharsets#ISO_8859_1}
     */
    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    /**
     * UTF-8 字符集 <br>
     * {@link java.nio.charset.StandardCharsets#UTF_8}
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * UTF-16BE 字符集 <br>
     * {@link java.nio.charset.StandardCharsets#UTF_16BE}
     */
    public static final Charset UTF_16BE = StandardCharsets.UTF_16BE;

    /**
     * UTF-16LE 字符集 <br>
     * {@link java.nio.charset.StandardCharsets#UTF_16LE}
     */
    public static final Charset UTF_16LE = StandardCharsets.UTF_16LE;

    /**
     * UTF-16 字符集 <br>
     * {@link java.nio.charset.StandardCharsets#UTF_16}
     */
    public static final Charset UTF_16 = StandardCharsets.UTF_16;

    private CharsetConstants() {
    }
}
