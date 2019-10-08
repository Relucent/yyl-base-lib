package com.github.relucent.base.util.http;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.github.relucent.base.util.collection.CaseInsensitiveKeyMap;
import com.github.relucent.base.util.net.SslHelper;

/**
 * HTTP工具类.
 * @author YYL
 * @version 2005-12-06 09:30
 */
public class HttpUtil {

    // =================================Fields=================================================
    // =================================Constants==============================================
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String USER_AGENT = "User-Agent";

    public static final String TEXT_PLAIN = "text/plain";
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    public static final String USER_AGENT_FOR_MOZILLA = ""//
            + "Mozilla/5.0 (Windows NT 6.3; WOW64) "//
            + "AppleWebKit/537.36 (KHTML, like Gecko) "//
            + "Chrome/33.0.1750.146 Safari/537.36";//

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String UTF_8 = "UTF-8";
    private static final int CONNECT_TIMEOUT = 5 * 1000;// 连接超时(单位毫秒)
    private static final int READ_TIMEOUT = 30 * 1000;// 读取超时(单位毫秒)

    // =================================Methods================================================
    /**
     * 发送GET请求
     * @param url 请求地址
     * @return 请求的结果
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * 发送GET请求
     * @param url 请求地址
     * @param queryParams URL参数
     * @return 请求的结果
     */
    public static String get(String url, Map<String, String> queryParams) {
        return get(url, queryParams, null);
    }

    /**
     * 发送GET请求
     * @param url 请求地址
     * @param queryParams URL参数
     * @param headers HTTP头数据
     * @return 请求的结果
     */
    public static String get(String url, Map<String, String> queryParams, Map<String, String> headers) {
        String query = buildWithQueryString(queryParams);
        if (!query.isEmpty()) {
            url += (url.indexOf("?") == -1 ? "?" : "&") + query;
        }
        headers = castHeaderMap(headers);
        return execute(url, "GET", null, headers);
    }

    /**
     * 发送POST请求
     * @param url 请求地址
     * @return 请求的结果
     */
    public static String post(String url) {
        return post(url, null);
    }

    /**
     * 发送POST请求
     * @param url 请求地址
     * @param queryParams 查询参数
     * @return 请求的结果
     */
    public static String post(String url, Map<String, String> queryParams) {
        return post(url, queryParams, null);
    }

    /**
     * 发送POST请求
     * @param url 请求地址
     * @param queryParams 查询参数
     * @param headers HTTP头数据
     * @return 请求的结果
     */
    public static String post(String url, Map<String, String> queryParams, Map<String, String> headers) {
        String data = buildWithQueryString(queryParams);
        return post(url, data, headers);
    }

    /**
     * 发送POST请求
     * @param url 请求地址
     * @param data 提交数据
     * @param headers HTTP头数据
     * @return 请求的结果
     */
    public static String post(String url, String data, Map<String, String> headers) {
        return execute(url, POST, data, headers);
    }

    /**
     * 执行请求
     * @param url 请求地址
     * @param method 请求方法
     * @param body 请求主体 (针对POST请求,GET请求应为NULL)
     * @param headers HTTP头数据
     * @return 请求的结果
     */
    public static String execute(String url, String method, String body, Map<String, String> headers) {
        HttpURLConnection conn = null;
        try {
            conn = getConnection(url, method, headers);
            conn.connect();
            if (POST.equals(method)) {
                writeAndClose(conn.getOutputStream(), body);
            }
            return readAndClose(conn.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            closeQuietly(conn);
        }
    }


    /**
     * 获得一个HTTP连接
     * @param url 请求地址
     * @param method 请求的方法
     * @param headers HTTP头数据
     * @return HTTP连接对象
     * @throws IOException 出现IO错误，抛出异常
     */
    public static HttpURLConnection getConnection(String url, String method, Map<String, String> headers) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        ignoreTLS(conn);
        conn.setRequestMethod(method);
        if (GET.equals(method)) {
            conn.setDoOutput(false);// GET,DELETE,HEAD,OPTIONS,TRACE
        } else {
            conn.setDoOutput(true);// POST,PUT,PATCH
        }
        conn.setDoInput(true);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        headers = headers != null ? new CaseInsensitiveKeyMap<String>(headers) : new CaseInsensitiveKeyMap<String>();
        if (!headers.containsKey(CONTENT_TYPE)) {
            conn.setRequestProperty(CONTENT_TYPE, FORM_URL_ENCODED + "; charset=UTF-8");
        }
        if (!headers.containsKey(USER_AGENT)) {
            conn.setRequestProperty(USER_AGENT, USER_AGENT_FOR_MOZILLA);
        }
        for (Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        return conn;
    }

    /**
     * 忽略 HTTPS
     * @param conn HTTP连接
     */
    public static void ignoreTLS(HttpURLConnection conn) {
        if (conn instanceof HttpsURLConnection) {
            ((HttpsURLConnection) conn).setSSLSocketFactory(SslHelper.getTrustAnySSLSocketFactory());
            ((HttpsURLConnection) conn).setHostnameVerifier(SslHelper.getTrustAnyHostnameVerifier());
        }
    }

    /**
     * 拼装参数字符串
     * @param url URL地址
     * @param queryParams 参数
     * @return 参数字符串
     */
    public static String buildUrlWithQueryString(String url, Map<String, String> queryParams) {
        String query = buildWithQueryString(queryParams);
        if (!query.isEmpty()) {
            url += (url.indexOf("?") == -1 ? "?" : "&") + query;
        }
        return url;
    }

    /**
     * 拼装参数字符串
     * @param queryParams 参数
     * @return 参数字符串
     */
    public static String buildWithQueryString(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Entry<String, String> entry : queryParams.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append("&");
            }
            String name = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                builder.append(encodeURL(name)).append("=").append(encodeURL(value));
            }
        }
        return builder.toString();
    }

    /**
     * UEL字符转码
     * @param value 字符串
     * @return 转码后的字符串
     */
    public static String encodeURL(String value) {
        try {
            return URLEncoder.encode(value, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭连接
     * @param conn HTTP连接
     */
    public static void closeQuietly(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {}
        }
    }

    /**
     * 转换请求头MAP(请求头的名称应该是大小写不敏感的)
     * @param headers 请求头MAP
     * @return 大小写不敏感的请求头MAP
     */
    private static CaseInsensitiveKeyMap<String> castHeaderMap(Map<String, String> headers) {
        if (headers == null) {
            return new CaseInsensitiveKeyMap<String>();
        }
        if (headers instanceof CaseInsensitiveKeyMap) {
            return (CaseInsensitiveKeyMap<String>) headers;
        }
        return new CaseInsensitiveKeyMap<String>(headers);
    }

    private static void writeAndClose(OutputStream output, String data) throws IOException {
        try {
            output.write(data.getBytes(Charset.forName(UTF_8)));
            output.flush();
        } finally {
            closeQuietly(output);
        }
    }

    private static String readAndClose(InputStream input) throws IOException {
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            for (int n = 0; -1 != (n = input.read(buffer));) {
                output.write(buffer, 0, n);
            }
            return new String(output.toByteArray(), UTF_8);
        } finally {
            closeQuietly(input);
            closeQuietly(output);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {}
        }
    }
}
