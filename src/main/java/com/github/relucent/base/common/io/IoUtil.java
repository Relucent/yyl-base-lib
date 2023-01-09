package com.github.relucent.base.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
     * @throws IOException IO异常
     */
    public static int write(String text, OutputStream output, Charset encoding) throws IOException {
        int length = 0;
        if (text != null) {
            byte[] data = text.getBytes(encoding);
            length = data.length;
            output.write(data);
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
     * @throws NullPointerException 如果输入输出参数为空
     * @throws IOException 如果发生I/O错误
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
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
     * @throws NullPointerException 如果输入输出参数为空
     * @throws IOException 如果发生I/O错误
     */
    public static long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[IoConstant.DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 将数据从大容量输入流 (超过2GB) <code>InputStream</code>拷贝到输出流 <code>OutputStream</code>
     * @param input 输入流
     * @param output 输出流 缓冲用于复制的缓冲区
     * @param buffer 用于拷贝的缓冲区
     * @return 拷贝的字节数
     * @throws NullPointerException 如果输入输出参数为空
     * @throws IOException 如果发生I/O错误
     */
    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (IoConstant.EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    // copy from Reader
    /**
     * 将数据从大容量读取字符流 (超过2GB) <code>Reader</code>拷贝到写入字符流 <code>Writer</code>。<br>
     * 大型流（超过2GB）将在复制完成后返回<code>-1</code>的，因为正确的拷贝字节数已经超过了int的最大值。<br>
     * 如果需要获得正确的拷贝字节数，应使用<code>copyLarge(Reader, Writer)</code>方法。<br>
     * @param input 读取字符流
     * @param output 写入字符流
     * @return 贝的字符数，如果拷贝字符数大于 Integer.MAX_VALUE，则返回 -1
     * @throws NullPointerException 如果输入输出参数为空
     * @throws IOException 如果发生I/O错误
     */
    public static int copy(Reader input, Writer output) throws IOException {
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
     * @throws NullPointerException 如果输入输出参数为空
     * @throws IOException 如果发生I/O错误
     */
    public static long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[IoConstant.DEFAULT_BUFFER_SIZE]);
    }

    /**
     * 将数据从大容量读取字符流 (超过2GB) <code>Reader</code>拷贝到写入字符流 <code>Writer</code>
     * @param input 读取字符流
     * @param output 写入字符流
     * @param buffer 用于拷贝的缓冲区
     * @return 拷贝的字符数
     * @throws NullPointerException 如果输入输出参数为空
     * @throws IOException 如果发生I/O错误
     */
    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (IoConstant.EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
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
     * 转换字节流 {@code OutputStream} 为字符流 {@code Writer}，字符集使用 UTF8
     * @param output 字节输出流
     * @return 字符流({@code Writer})
     */
    public static Writer toWriter(final OutputStream output) {
        return toWriter(output, StandardCharsets.UTF_8);
    }

    /**
     * 转换字节流 {@code OutputStream} 为字符流 {@code Writer}
     * @param output 字节输出流
     * @param charset 字符编码
     * @return 字符流({@code Writer})
     */
    public static Writer toWriter(final OutputStream output, final Charset charset) {
        return new OutputStreamWriter(output, charset);
    }

    /**
     * 转换字节流 {@code InputStream} 为字符流 {@code Reader} ，字符集使用 UTF8
     * @param input 字节输入流
     * @return 字符流(读取)
     */
    public static Reader toReader(final InputStream input) {
        return toReader(input, StandardCharsets.UTF_8);
    }

    /**
     * 转换字节流 {@code InputStream} 为字符流 {@code Reader}
     * @param input 字节输入流
     * @param charset 字符编码
     * @return 字符流(读取)
     */
    public static Reader toReader(final InputStream input, final Charset charset) {
        return new InputStreamReader(input, charset);
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
     * @param charset 字符编码
     * @return 字符串
     * @throws IOException 出现IO异常时抛出
     */
    public static String toString(final InputStream input, final Charset charset) throws IOException {
        try (final Writer writer = new StringWriter()) {
            copyLarge(new InputStreamReader(input, charset), writer);
            return writer.toString();
        }
    }

    /**
     * 获取读取器的内容作为字符串列表，每行一个条目。
     * @param input 字符读取流
     * @return 字符串列表（不会为空）
     * @throws IOException 出现IO错误，抛出异常
     */
    public static List<String> readLines(final Reader input) throws IOException {
        final BufferedReader reader = buffer(input);
        final List<String> list = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }
}
