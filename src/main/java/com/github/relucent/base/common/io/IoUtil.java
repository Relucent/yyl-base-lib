package com.github.relucent.base.common.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.github.relucent.base.common.constant.CharsetConstant;
import com.github.relucent.base.common.constant.IoConstant;

/**
 * IO工具类 <br>
 */
public class IoUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected IoUtil() {
    }

    /**
     * 将文本写入到输出流中
     * @param text 文本
     * @param output 输出流中
     * @param encoding 字符编码
     * @return 写入的字节数
     */
    public static int write(String text, OutputStream output, Charset encoding) {
        int length = 0;
        if (text != null) {
            Charset charset = defaultEncoding(encoding);
            byte[] data = text.getBytes(charset);
            length = data.length;
            try {
                output.write(data);
            } catch (IOException e) {
                throw IoRuntimeException.wrap(e);
            }
        }
        return length;
    }

    // copy from InputStream
    /**
     * 将数据从输入流 <code>InputStream</code>拷贝到输出流 <code>OutputStream</code>。<br>
     * 大型流（超过2GB）将在复制完成后返回<code>-1</code>的，因为正确的拷贝字节数已经超过了int的最大值。<br>
     * 如果需要获得正确的拷贝字节数，应使用 <code>copyLarge(InputStream input, OutputStream output)</code>方法。<br>
     * @param input 输入流
     * @param output 输出流
     * @return 拷贝的字节数，如果拷贝字节数大于 Integer.MAX_VALUE，则返回 -1
     */
    public static int copy(InputStream input, OutputStream output) {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * 将数据从大容量输入流 (超过2GB)<code>InputStream</code>拷贝到输出流 <code>OutputStream</code>
     * @param input 输入流
     * @param output 输出流 缓冲用于复制的缓冲区
     * @return 拷贝的字节数
     */
    public static long copyLarge(InputStream input, OutputStream output) {
        return copyLarge(input, output, new byte[IoConstant.DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 将数据从大容量输入流 (超过2GB) <code>InputStream</code>拷贝到输出流 <code>OutputStream</code>
     * @param input 输入流
     * @param output 输出流 缓冲用于复制的缓冲区
     * @param buffer 用于拷贝的缓冲区
     * @return 拷贝的字节数
     */
    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) {
        try {
            long count = 0;
            int n = 0;
            while (IoConstant.EOF != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    // copy from Reader
    /**
     * 将数据从大容量读取字符流 (超过2GB) <code>Reader</code>拷贝到写入字符流 <code>Writer</code>。<br>
     * 大型流（超过2GB）将在复制完成后返回<code>-1</code>的，因为正确的拷贝字节数已经超过了int的最大值。<br>
     * 如果需要获得正确的拷贝字节数，应使用<code>copyLarge(Reader, Writer)</code>方法。<br>
     * @param input 读取字符流
     * @param output 写入字符流
     * @return 拷贝的字符数，如果拷贝字符数大于 Integer.MAX_VALUE，则返回 -1
     */
    public static int copy(Reader input, Writer output) {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * 将数据从大容量读取字符流 (超过2GB) <code>Reader</code>拷贝到写入字符流 <code>Writer</code>
     * @param input 读取字符流
     * @param output 写入字符流
     * @return 拷贝的字符数
     */
    public static long copyLarge(Reader input, Writer output) {
        return copyLarge(input, output, new char[IoConstant.DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 将数据从大容量读取字符流 (超过2GB) <code>Reader</code>拷贝到写入字符流 <code>Writer</code>
     * @param input 读取字符流
     * @param output 写入字符流
     * @param buffer 用于拷贝的缓冲区
     * @return 拷贝的字符数
     */
    public static long copyLarge(Reader input, Writer output, char[] buffer) {
        try {
            long count = 0;
            int n = 0;
            while (IoConstant.EOF != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    // close
    /**
     * 关闭URL连接
     * @param connection 要关闭的连接
     */
    public static void closeQuietly(URLConnection connection) {
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).disconnect();
        }
    }

    /**
     * 无条件地的关闭可关闭{@link Closeable}对象，忽略任何异常。<br>
     * 
     * <pre>
     * Closeable closeable = null;
     * try {
     *     closeable = new FileReader("foo.txt");
     *     // process closeable
     *     closeable.close();
     * } catch (Exception e) {
     *     // error handling
     * } finally {
     *     IOUtils.closeQuietly(closeable);
     * }
     * </pre>
     * 
     * @param closeable 要关闭的可关闭{@link Closeable}对象，该对象可能为空或已关闭
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * 如果给定字符流是{@link BufferedWriter}，则返回该字符流，否则包装一个 BufferedWriter返回。
     * @param writer 字符写入流
     * @return {@link BufferedWriter}
     */
    public static BufferedWriter buffer(final Writer writer) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    /**
     * 如果给定字符流是{@link BufferedReader}，则返回该字符流，否则包装一个 BufferedReader返回。
     * @param reader 字符读取流
     * @return {@link BufferedReader}
     */
    public static BufferedReader buffer(final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /**
     * 转换为{@link BufferedOutputStream}
     * @param output {@link OutputStream}
     * @return {@link BufferedOutputStream}
     */
    public static BufferedOutputStream buffer(OutputStream output) {
        return (output instanceof BufferedOutputStream) ? (BufferedOutputStream) output : new BufferedOutputStream(output);
    }

    /**
     * 转换为{@link BufferedInputStream}
     * @param input {@link InputStream}
     * @return {@link BufferedInputStream}
     */
    public static BufferedInputStream buffer(InputStream input) {
        return (input instanceof BufferedInputStream) ? (BufferedInputStream) input : new BufferedInputStream(input);
    }

    /**
     * 转换字节流 {@code OutputStream} 为字符流 {@code Writer}，字符集使用 UTF8
     * @param output 字节输出流
     * @return 字符流({@code Writer})
     */
    public static Writer toWriter(final OutputStream output) {
        return toWriter(output, CharsetConstant.DEFAULT);
    }

    /**
     * 转换字节流 {@code OutputStream} 为字符流 {@code Writer}
     * @param output 字节输出流
     * @param encoding 字符编码
     * @return 字符流({@code Writer})
     */
    public static Writer toWriter(final OutputStream output, final Charset encoding) {
        return new OutputStreamWriter(output, encoding);
    }

    /**
     * 转换字节流 {@code InputStream} 为字符流 {@code Reader} ，字符集使用 UTF8
     * @param input 字节输入流
     * @return 字符流(读取)
     */
    public static Reader toReader(final InputStream input) {
        return toReader(input, CharsetConstant.DEFAULT);
    }

    /**
     * 转换字节流 {@code InputStream} 为字符流 {@code Reader}
     * @param input 字节输入流
     * @param encoding 字符编码
     * @return 字符流(读取)
     */
    public static Reader toReader(final InputStream input, final Charset encoding) {
        return input == null ? null : buffer(new InputStreamReader(input, defaultEncoding(encoding)));
    }

    /**
     * 字节数组转为{@link InputStream}
     * @param content 字节数组
     * @return 字节流
     */
    public static ByteArrayInputStream toInputStream(byte[] content) {
        return content == null ? null : new ByteArrayInputStream(content);
    }

    /**
     * 获取数据流数据。该方法不会{@code close}数据流。
     * @param input 流数据
     * @return 流数据的字节数组
     * @throws IOException 出现IO异常时抛出
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * 以字符串形式获取<code>InputStream</code>的内容。
     * @param input 输入流
     * @return 字符串
     * @throws IOException 出现IO异常时抛出
     */
    public static String toString(final InputStream input) throws IOException {
        return toString(input, CharsetConstant.DEFAULT);
    }

    /**
     * 以字符串形式获取<code>InputStream</code>的内容。
     * @param input 输入流
     * @param encoding 字符编码
     * @return 字符串
     */
    public static String toString(final InputStream input, final Charset encoding) {
        try (final Writer writer = new StringWriter()) {
            copyLarge(new InputStreamReader(input, defaultEncoding(encoding)), writer);
            return writer.toString();
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 以字符串形式获取<code>Reader</code>的内容。
     * @param input 字符读取流
     * @return 字符串列表（不会为空）
     */
    public static String toString(final Reader input) {
        final StringBuilder builder = new StringBuilder();
        try {
            final BufferedReader reader = buffer(input);
            final CharBuffer buffer = CharBuffer.allocate(IoConstant.DEFAULT_BUFFER_SIZE);
            while (-1 != reader.read(buffer)) {
                builder.append(buffer.flip());
            }
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
        return builder.toString();
    }

    /**
     * 获取读取器的内容作为字符串列表，每行一个条目。
     * @param input 字符读取流
     * @return 字符串列表（不会为空）
     */
    public static List<String> readLines(final Reader input) {
        try {
            final BufferedReader reader = buffer(input);
            final List<String> list = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
            return list;
        } catch (IOException e) {
            throw IoRuntimeException.wrap(e);
        }
    }

    /**
     * 获取读取器的内容作为字符串列表，每行一个条目。
     * @param input 字符读取流
     * @param action 每行处理方法
     * @throws IOException 出现IO错误，抛出异常
     */
    public static void readLines(final Reader input, final Consumer<String> action) throws IOException {
        final BufferedReader reader = buffer(input);
        String line = reader.readLine();
        while (line != null) {
            action.accept(line);
            line = reader.readLine();
        }
    }

    /**
     * 返回传入的字符编码，如果字符编码为{@code null}，则返回UTF_8编码
     * @param encoding 字符编码
     */
    static Charset defaultEncoding(final Charset encoding) {
        return encoding == null ? CharsetConstant.DEFAULT : encoding;
    }
}
