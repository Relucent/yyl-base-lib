package com.github.relucent.base.common.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.relucent.base.common.lang.AssertUtil;

/**
 * 请求和响应的常用方法
 * @param <T> 实现类的类型， Request 或者 or Response
 */
@SuppressWarnings({ "unchecked" })
public abstract class HttpBase<T extends HttpBase<T>> {

    URL url;
    HttpMethod method;
    Map<String, String> headers;
    Map<String, String> cookies;

    HttpBase() {
        headers = new LinkedHashMap<String, String>();
        cookies = new LinkedHashMap<String, String>();
    }

    /**
     * 获得请求的URL
     * @return 请求的URL
     */
    public URL getUrl() {
        return url;
    }

    /**
     * 设置请求的URL
     * @param url 请求的URL
     * @return 当前对象
     */
    public T setUrl(URL url) {
        AssertUtil.notNull(url, "URL must not be null");
        this.url = url;
        return (T) this;
    }

    /**
     * 设置要获取的请求URL。协议必须是HTTP或HTTPS
     * @param url URL字符串
     * @return 当前对象
     */
    public T setUrl(String url) {
        AssertUtil.notEmpty(url, "Must supply a valid URL");
        try {
            setUrl(new URL(DataUtil.encodeUrl(url)));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
        return (T) this;
    }

    /**
     * 获得请求方法.
     * @return 请求方法
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * 设置请求方法，默认是 GET.
     * @param method 请求方法
     * @return 当前对象
     */
    public T setMethod(HttpMethod method) {
        AssertUtil.notNull(method, "Method must not be null");
        this.method = method;
        return (T) this;
    }

    /**
     * 获得报文头信息
     * @param name 报文头名称
     * @return 报文头信息
     */
    public String getHeader(String name) {
        AssertUtil.notNull(name, "Header name must not be null");
        return getHeaderCaseInsensitive(name);
    }

    /**
     * 设置报文头
     * @param name 报文头名称
     * @param value 报文头值
     * @return 当前对象
     */
    public T setHeader(String name, String value) {
        AssertUtil.notEmpty(name, "Header name must not be empty");
        AssertUtil.notNull(value, "Header value must not be null");
        removeHeader(name); // ensures we don't get an "accept-encoding" and a "Accept-Encoding"
        headers.put(name, value);
        return (T) this;
    }

    /**
     * 判断是否存在该头信息
     * @param name 报文头名称
     * @return 如果存在则返回 {@code true}，否则返回 {@code false}
     */
    public boolean hasHeader(String name) {
        AssertUtil.notEmpty(name, "Header name must not be empty");
        return getHeaderCaseInsensitive(name) != null;
    }

    /**
     * 判断是否存在该头信息
     * @param name 报文头名称
     * @param value 报文头值
     * @return 如果存在则返回 {@code true}，否则返回 {@code false}
     */
    public boolean hasHeaderWithValue(String name, String value) {
        return hasHeader(name) && getHeader(name).equalsIgnoreCase(value);
    }

    /**
     * 删除报文头
     * @param name 报文头名称
     * @return 如果存在则返回 {@code true}，否则返回 {@code false}
     */
    public T removeHeader(String name) {
        AssertUtil.notEmpty(name, "Header name must not be empty");
        Map.Entry<String, String> entry = scanHeaders(name); // remove is case insensitive too
        if (entry != null)
            headers.remove(entry.getKey()); // ensures correct case
        return (T) this;
    }

    /**
     * 获得报文头
     * @return 获得报文头
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 获得 cookie 值
     * @param name Cookie名称
     * @return cookie 值
     */
    public String getCookie(String name) {
        AssertUtil.notEmpty(name, "Cookie name must not be empty");
        return cookies.get(name);
    }

    /**
     * 设置 cookie
     * @param name Cookie名称
     * @param value Cookie值
     * @return 当前对象
     */
    public T setCookie(String name, String value) {
        AssertUtil.notEmpty(name, "Cookie name must not be empty");
        AssertUtil.notNull(value, "Cookie value must not be null");
        cookies.put(name, value);
        return (T) this;
    }

    /**
     * 设置 cookie
     * @param cookies Cookie集合
     * @return 当前对象
     */
    public T setCookies(Map<String, String> cookies) {
        AssertUtil.notNull(cookies, "Cookie map must not be null");
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            setCookie(entry.getKey(), entry.getValue());
        }
        return (T) this;
    }

    /**
     * 判断 cookie是否存在
     * @param name Cookie名称
     * @return 如果Cookie名称存在，返回{@code true}，否则返回 {@code false}
     */
    public boolean hasCookie(String name) {
        AssertUtil.notEmpty(name, "Cookie name must not be empty");
        return cookies.containsKey(name);
    }

    /**
     * 删除 cookie
     * @param name Cookie名称
     * @return 当前对象
     */
    public T removeCookie(String name) {
        AssertUtil.notEmpty(name, "Cookie name must not be empty");
        cookies.remove(name);
        return (T) this;
    }

    /**
     * 获得全部 cookie
     * @return Cookie表
     */
    public Map<String, String> cookies() {
        return cookies;
    }

    /**
     * 获得报文头（忽略报文头名称的大小写）
     * @param name 报文头名称
     * @return 获得报文头值
     */
    private String getHeaderCaseInsensitive(String name) {
        AssertUtil.notNull(name, "Header name must not be null");
        // quick evals for common case of title case, lower case, then scan for mixed
        String value = headers.get(name);
        if (value == null)
            value = headers.get(name.toLowerCase());
        if (value == null) {
            Map.Entry<String, String> entry = scanHeaders(name);
            if (entry != null)
                value = entry.getValue();
        }
        return value;
    }

    /**
     * 获得报文头（忽略报文头名称的大小写）
     * @return 获得报文头项
     */
    private Map.Entry<String, String> scanHeaders(String name) {
        String lc = name.toLowerCase();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().toLowerCase().equals(lc))
                return entry;
        }
        return null;
    }
}
