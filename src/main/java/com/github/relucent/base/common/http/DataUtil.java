package com.github.relucent.base.common.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.relucent.base.common.lang.AssertUtil;

/**
 * 用于处理数据的工具类
 */
public final class DataUtil {

    static final String DEFAULT_CHARSET = "UTF-8"; // used if not found in header or meta charset
    private static final Pattern CHARSET_PATTERN = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
    private static final int BOUNDARY_LENGTH = 32;
    private static final int BUFFER_SIZE = 0x20000; // ~130K.
    private static final char[] mimeBoundaryChars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private DataUtil() {
    }

    /**
     * 将输入流读入字节缓冲区
     * @param inStream 要读取的输入流
     * @param maxSize从流中读取的最大大小（以字节为单位）。设置为0将不受限制。
     * @return 已填充字节缓冲区
     * @throws IOException 如果从输入流读取时发生异常
     */
    static ByteBuffer readToByteBuffer(InputStream inStream, int maxSize) throws IOException {
        AssertUtil.isTrue(maxSize >= 0, "maxSize must be 0 (unlimited) or larger");
        final boolean capped = maxSize > 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(BUFFER_SIZE);
        int read;
        int remaining = maxSize;
        while (true) {
            read = inStream.read(buffer);
            if (read == -1)
                break;
            if (capped) {
                if (read > remaining) {
                    outStream.write(buffer, 0, remaining);
                    break;
                }
                remaining -= read;
            }
            outStream.write(buffer, 0, read);
        }
        return ByteBuffer.wrap(outStream.toByteArray());
    }

    static ByteBuffer emptyByteBuffer() {
        return ByteBuffer.allocate(0);
    }

    /**
     * 从内容类型头解析出字符集。如果不支持该字符集，则返回null（因此默认值将生效）
     * @param contentType 内容类型，例如“text/html;charset=UTF-8”
     * @return 返回字符集，例如"UTF-8"，如果没找到会返回NULL，字符集字符串为大写
     */
    static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        Matcher m = CHARSET_PATTERN.matcher(contentType);
        if (m.find()) {
            String charset = m.group(1).trim();
            charset = charset.replace("charset=", "");
            return validateCharset(charset);
        }
        return null;
    }

    private static String validateCharset(String cs) {
        if (cs == null || cs.length() == 0)
            return null;
        cs = cs.trim().replaceAll("[\"']", "");
        try {
            if (Charset.isSupported(cs))
                return cs;
            cs = cs.toUpperCase(Locale.ENGLISH);
            if (Charset.isSupported(cs))
                return cs;
        } catch (IllegalCharsetNameException e) {
            // if our this charset matching fails.... we just take the default
        }
        return null;
    }

    /**
     * 创建适合用作mime边界的随机字符串
     */
    static String mimeBoundary() {
        final StringBuilder mime = new StringBuilder(BOUNDARY_LENGTH);
        final Random rand = new Random();
        for (int i = 0; i < BOUNDARY_LENGTH; i++) {
            mime.append(mimeBoundaryChars[rand.nextInt(mimeBoundaryChars.length)]);
        }
        return mime.toString();
    }

    static String encodeUrl(String url) {
        if (url == null)
            return null;
        return url.replaceAll(" ", "%20");
    }

    static String encodeMimeName(String val) {
        if (val == null)
            return null;
        return val.replaceAll("\"", "%22");
    }
}
