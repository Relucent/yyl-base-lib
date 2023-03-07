package com.github.relucent.base.common.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.github.relucent.base.common.lang.AssertUtil;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * 表示HTTP请求
 */
public class HttpRequest extends HttpBase<HttpRequest> {
    private Proxy proxy; // nullable
    private int connectTimeoutMillis;// milliseconds
    private int readTimeoutMillis;// milliseconds
    private int maxBodySizeBytes;
    private boolean followRedirects;
    private Collection<KeyValue> data;
    private String body = null;
    private boolean ignoreHttpErrors = false;
    private boolean ignoreContentType = false;
    private boolean validateTLSCertificates = true;
    private String postDataCharset = DataUtil.DEFAULT_CHARSET;

    public HttpRequest() {
        connectTimeoutMillis = 3000;
        readTimeoutMillis = 30000;
        maxBodySizeBytes = 1024 * 1024; // 1MB
        followRedirects = true;
        data = new ArrayList<KeyValue>();
        method = HttpMethod.GET;
        headers.put("Accept-Encoding", "gzip");
    }

    public Proxy getProxy() {
        return proxy;
    }

    public HttpRequest setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public HttpRequest setProxy(String host, int port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
        return this;
    }

    /**
     * 设置请求使用的代理，如果设置成null表示不使用代理
     * @param host 使用的代理地址
     * @param port 使用的代理端口
     * @param username 用户名
     * @param password 密码
     * @return 当前连接对象
     */
    public HttpRequest setProxy(String host, int port, String username, String password) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
        setProxy(proxy, username, password);
        return this;
    }

    /**
     * 设置请求使用的代理，如果设置成null表示不使用代理
     * @param proxy 使用的代理
     * @param username 用户名
     * @param password 密码
     * @return 当前连接对象
     */
    public HttpRequest setProxy(Proxy proxy, String username, String password) {
        setProxy(proxy);
        if (StringUtil.isNotEmpty(username) && StringUtil.isNotEmpty(password)) {
            setHeader("Proxy-Authorization", "Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes()));
        }
        return this;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public HttpRequest setConnectTimeoutMillis(int millis) {
        AssertUtil.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
        connectTimeoutMillis = millis;
        return this;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public HttpRequest setReadTimeoutMillis(int millis) {
        AssertUtil.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
        readTimeoutMillis = millis;
        return this;
    }

    public int getMaxBodySize() {
        return maxBodySizeBytes;
    }

    public HttpRequest setMaxBodySize(int bytes) {
        AssertUtil.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
        maxBodySizeBytes = bytes;
        return this;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    /**
     * 设置是否跟随重定向
     * @param followRedirects 是否跟随重定向
     * @return 当前连接对象
     */
    public HttpRequest setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    public boolean isIgnoreHttpErrors() {
        return ignoreHttpErrors;
    }

    /**
     * 设置是否忽略 HTTP 响应状态的异常（状态码为4xx-5xx，例如404或500）。<br>
     * 如果设置为<code>false</code>；如果遇到状态异常，将引发IOException。<br>
     * 如果设置为<code>true</code>，则响应将填充错误正文，并且状态消息将反映错误。<br>
     * @param ignoreHttpErrors 是否忽略响应状态的异常（默认是false)
     * @return 当前连接对象
     */
    public HttpRequest setIgnoreHttpErrors(boolean ignoreHttpErrors) {
        this.ignoreHttpErrors = ignoreHttpErrors;
        return this;
    }

    public boolean isIgnoreContentType() {
        return ignoreContentType;
    }

    /**
     * 设置解析响应时忽略文档的内容类型。<br>
     * 默认情况下，这是<code>false</code>，无法识别的内容类型将导致引发IOException。<br>
     * （例如，这是为了通过尝试解析JPEG二进制图像来防止产生垃圾。）设置为true可强制解析尝试，而不考虑内容类型。
     * @param ignoreContentType 解析响应时忽略文档的内容类型
     * @return 当前连接对象
     */
    public HttpRequest setIgnoreContentType(boolean ignoreContentType) {
        this.ignoreContentType = ignoreContentType;
        return this;
    }

    public boolean isValidateTLSCertificates() {
        return validateTLSCertificates;
    }

    public void setValidateTLSCertificates(boolean value) {
        this.validateTLSCertificates = value;
    }

    /**
     * 添加请求参数
     * @param key 数据键(表单元素名称)
     * @param value 数值值
     * @return 当前连接对象
     */
    public HttpRequest addData(String key, String value) {
        addData(KeyValue.create(key, value));
        return this;
    }

    public HttpRequest addData(KeyValue keyval) {
        AssertUtil.notNull(keyval, "Key val must not be null");
        data.add(keyval);
        return this;
    }

    /**
     * 将所有提供的数据添加到请求数据参数
     * @param data 参数集合
     * @return 当前连接对象
     */
    public HttpRequest addData(Map<String, String> data) {
        AssertUtil.notNull(data, "Data map must not be null");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            addData(KeyValue.create(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    public Collection<KeyValue> getData() {
        return data;
    }

    /**
     * 获得参数值
     * @param key 参数键
     * @return 参数值
     */
    public KeyValue getData(String key) {
        AssertUtil.notEmpty(key, "Data key must not be empty");
        for (KeyValue keyVal : getData()) {
            if (keyVal.geyKey().equals(key)) {
                return keyVal;
            }
        }
        return null;
    }

    public HttpRequest setRequestBody(String body) {
        this.body = body;
        return this;
    }

    public String getRequestBody() {
        return body;
    }

    public HttpRequest setPostDataCharset(String charset) {
        AssertUtil.notNull(charset, "Charset must not be null");
        if (!Charset.isSupported(charset))
            throw new IllegalCharsetNameException(charset);
        this.postDataCharset = charset;
        return this;
    }

    public String getPostDataCharset() {
        return postDataCharset;
    }

    /**
     * 执行请求
     * @return 响应对象
     * @throws IOException 请求出现错误抛出异常
     */
    public HttpResponse execute() throws IOException {
        return HttpResponse.execute(this);
    }
}
