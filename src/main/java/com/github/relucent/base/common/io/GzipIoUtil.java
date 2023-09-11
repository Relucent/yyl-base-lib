package com.github.relucent.base.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP压缩流工具类<br>
 */
public class GzipIoUtil {

    /**
     * 将{@code OutputStream}转换为 {@code GZIPOutputStream}
     * @param output 输出流
     * @return GZIP输出流
     */
    public static GZIPOutputStream toGZIPOutputStream(final OutputStream output) {
        try {
            return output instanceof GZIPOutputStream ? (GZIPOutputStream) output : new GZIPOutputStream(output);
        } catch (final IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 将{@code InputStream}转换为 {@code GZIPInputStream}
     * @param input 输入流
     * @return GZIP输入流
     */
    public static GZIPInputStream toGZIPInputStream(final InputStream input) {
        try {
            return input instanceof GZIPInputStream ? (GZIPInputStream) input : new GZIPInputStream(input);
        } catch (final IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 将来源流压缩到目标流
     * @param source 来源流
     * @param target 目标流
     */
    public static void gzip(final InputStream source, final OutputStream target) {
        try {
            GZIPOutputStream gzos = toGZIPOutputStream(target);
            IoUtil.copy(source, gzos);
            gzos.finish();
        } catch (Exception e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 将来源流解压到目标流
     * @param source 来源流
     * @param target 目标流
     */
    public static void ungzip(final InputStream source, final OutputStream target) {
        try {
            GZIPInputStream gzis = toGZIPInputStream(source);
            IoUtil.copy(gzis, target);
        } catch (Exception e) {
            throw IoRuntimeException.wrap(e);
        }
    }
}
