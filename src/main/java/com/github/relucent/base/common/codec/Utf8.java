package com.github.relucent.base.common.codec;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * UTF-8 字符集编码解码
 */
public final class Utf8 {

    /** 默认字符集 */
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
     * 工具类私有构造
     */
    protected Utf8() {
    }

    /**
     * 获得字符串的 UTF-8 编码字节数组
     * @param string 字符串的
     * @return 字节数组
     */
    public static byte[] encode(CharSequence string) {
        try {
            ByteBuffer bytes = CHARSET.newEncoder().encode(CharBuffer.wrap(string));
            byte[] bytesCopy = new byte[bytes.limit()];
            System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());
            return bytesCopy;
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("Encoding failed", e);
        }
    }

    /**
     * 将 UTF-8编码格式的字节数组解码为字符串
     * @param bytes 字节数组(UTF-8格式)
     * @return 字符串
     */
    public static String decode(byte[] bytes) {
        try {
            return CHARSET.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("Decoding failed", e);
        }
    }

    /**
     * 返回字符串UTF8编码的字节数。{@link String#getBytes(Charset)}}
     * @param string 字符串
     * @return 编码的字节数
     */
    public static long size(String string) {
        return size(string, 0, string.length());
    }

    /**
     * 返回字符串UTF8编码的字节数。{@link String#getBytes(Charset)}}
     * @param string 字符串
     * @param beginIndex 字符串开始索引
     * @param endIndex 字符串结束索引
     * @return 编码的字节数
     */
    public static long size(String string, int beginIndex, int endIndex) {
        if (string == null)
            throw new IllegalArgumentException("string == null");
        if (beginIndex < 0)
            throw new IllegalArgumentException("beginIndex < 0: " + beginIndex);
        if (endIndex < beginIndex) {
            throw new IllegalArgumentException("endIndex < beginIndex: " + endIndex + " < " + beginIndex);
        }
        if (endIndex > string.length()) {
            throw new IllegalArgumentException("endIndex > string.length: " + endIndex + " > " + string.length());
        }
        long result = 0;
        for (int i = beginIndex; i < endIndex;) {
            int c = string.charAt(i);
            if (c < 0x80) {
                // A 7-bit character with 1 byte.
                result++;
                i++;
            } else if (c < 0x800) {
                // An 11-bit character with 2 bytes.
                result += 2;
                i++;
            } else if (c < 0xd800 || c > 0xdfff) {
                // A 16-bit character with 3 bytes.
                result += 3;
                i++;
            } else {
                int low = i + 1 < endIndex ? string.charAt(i + 1) : 0;
                if (c > 0xdbff || low < 0xdc00 || low > 0xdfff) {
                    // A malformed surrogate, which yields '?'.
                    result++;
                    i++;
                } else {
                    // A 21-bit character with 4 bytes.
                    result += 4;
                    i += 2;
                }
            }
        }
        return result;
    }
}
