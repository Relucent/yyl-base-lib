package com.github.relucent.base.common.io;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.github.relucent.base.common.constants.IoConstants;

/**
 * IO工具类 <br>
 */
public class IoUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected IoUtil() {}

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
        return copyLarge(input, output, new byte[IoConstants.DEFAULT_BUFFER_SIZE]);
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
        while (IoConstants.EOF != (n = input.read(buffer))) {
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
        return copyLarge(input, output, new char[IoConstants.DEFAULT_BUFFER_SIZE]);
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
        while (IoConstants.EOF != (n = input.read(buffer))) {
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
}
