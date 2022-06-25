package com.github.relucent.base.common.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.lang.AssertUtil;

/**
 * HTTP连接的实现类
 */
public class Connection {

    public static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final int HTTP_TEMP_REDIRECT_STATUS = 307;

    private Request request;
    private Response response;

    public static Connection connect(String url) {
        Connection con = new Connection();
        con.url(url);
        return con;
    }

    public static Connection connect(URL url) {
        Connection con = new Connection();
        con.url(url);
        return con;
    }

    private Connection() {
        request = new Request();
        response = new Response();
    }

    /**
     * 设置要获取的请求URL。协议必须是HTTP或HTTPS
     * @param url URL 对象
     * @return 当前连接对象
     */
    public Connection url(URL url) {
        request.url(url);
        return this;
    }

    /**
     * 设置要获取的请求URL。协议必须是HTTP或HTTPS
     * @param url URL字符串
     * @return 当前连接对象
     */
    public Connection url(String url) {
        AssertUtil.notEmpty(url, "Must supply a valid URL");
        try {
            request.url(new URL(DataUtil.encodeUrl(url)));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
        return this;
    }

    /**
     * 设置请求使用的代理，如果设置成null表示不使用代理
     * @param proxy 使用的代理
     * @return 当前连接对象
     */
    public Connection proxy(Proxy proxy) {
        request.proxy(proxy);
        return this;
    }

    /**
     * 设置请求使用的代理，如果设置成null表示不使用代理
     * @param host 使用的代理地址
     * @param port 使用的代理端口
     * @return 当前连接对象
     */
    public Connection proxy(String host, int port) {
        request.proxy(host, port);
        return this;
    }

    /**
     * 设置请求使用的代理，如果设置成null表示不使用代理
     * @param proxy 使用的代理
     * @param username 用户名
     * @param password 密码
     * @return 当前连接对象
     */
    public Connection proxy(Proxy proxy, String username, String password) {
        request.proxy(proxy);
        header("Proxy-Authorization", "Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes()));
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
    public Connection proxy(String host, int port, String username, String password) {
        request.proxy(host, port);
        header("Proxy-Authorization", "Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes()));
        return this;
    }

    /**
     * 设置浏览器标识(User-Agent)请求头
     * @param userAgent 浏览器标识
     * @return 当前连接对象
     */
    public Connection userAgent(String userAgent) {
        AssertUtil.notNull(userAgent, "User agent must not be null");
        request.header("User-Agent", userAgent);
        return this;
    }

    /**
     * 设置请求超时（连接）。零超时被视为无限制。
     * @param millis 超时时间的毫秒数
     * @return 当前连接对象
     */
    public Connection connectTimeoutMillis(int millis) {
        request.connectTimeoutMillis(millis);
        return this;
    }

    /**
     * 设置请求连接的超时时间，零表示无限制。
     * @param duration 超时时间
     * @param unit 时间单位
     * @return 当前连接对象
     */
    public Connection connectTimeout(int duration, TimeUnit unit) {
        request.connectTimeoutMillis((int) unit.toMillis(duration));
        return this;
    }

    /**
     * 设置请求超时（连接和读取），零表示无限制。
     * @param millis 超时时间的毫秒数
     * @return 当前连接对象
     */
    public Connection readTimeoutMillis(int millis) {
        request.readTimeoutMillis(millis);
        return this;
    }

    /**
     * 设置请求超时（连接和读取），零表示无限制。
     * @param duration 超时时间
     * @param unit 时间单位
     * @return 当前连接对象
     */
    public Connection readTimeout(int duration, TimeUnit unit) {
        request.readTimeoutMillis((int) unit.toMillis(duration));
        return this;
    }

    /**
     * 在连接关闭和输入被截断之前，设置从（未压缩的）连接读入正文的最大字节数。默认最大值为1MB。最大大小为零被视为无限量（仅受用户的耐心和机器上可用内存的限制）。
     * @param bytes 读取的字节数限制
     * @return 当前连接对象
     */
    public Connection maxBodySize(int bytes) {
        request.maxBodySize(bytes);
        return this;
    }

    /**
     * 设置是否跟随重定向
     * @param followRedirects 是否跟随重定向
     * @return 当前连接对象
     */
    public Connection followRedirects(boolean followRedirects) {
        request.followRedirects(followRedirects);
        return this;
    }

    /**
     * 设置请求的 Referer 头，该请求头一般用于表示服务器该网页是从哪个页面链接过来的
     * @param referrer Referrer 请求头
     * @return 当前连接对象
     */
    public Connection referrer(String referrer) {
        AssertUtil.notNull(referrer, "Referrer must not be null");
        request.header("Referer", referrer);
        return this;
    }

    /**
     * 设置请求方法，默认是 GET.
     * @param method 请求方法
     * @return 当前连接对象
     */
    public Connection method(Method method) {
        request.method(method);
        return this;
    }

    /**
     * 设置是否忽略 HTTP 响应状态的异常（状态码为4xx-5xx，例如404或500）。<br>
     * 如果设置为<code>false</code>；如果遇到状态异常，将引发IOException。<br>
     * 如果设置为<code>true</code>，则响应将填充错误正文，并且状态消息将反映错误。<br>
     * @param ignoreHttpErrors 是否忽略响应状态的异常（默认是false)
     * @return 当前连接对象
     */
    public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
        request.ignoreHttpErrors(ignoreHttpErrors);
        return this;
    }

    /**
     * 设置解析响应时忽略文档的内容类型。<br>
     * 默认情况下，这是<code>false</code>，无法识别的内容类型将导致引发IOException。<br>
     * （例如，这是为了通过尝试解析JPEG二进制图像来防止产生垃圾。）设置为true可强制解析尝试，而不考虑内容类型。
     * @param ignoreContentType 解析响应时忽略文档的内容类型
     * @return 当前连接对象
     */
    public Connection ignoreContentType(boolean ignoreContentType) {
        request.ignoreContentType(ignoreContentType);
        return this;
    }

    /**
     * 禁用/启用HTTPS请求的TSL证书验证
     * @param value 是否启用TSL证书验证(默认true)
     * @return 当前连接对象
     */
    public Connection validateTLSCertificates(boolean value) {
        request.validateTLSCertificates(value);
        return this;
    }

    /**
     * 添加请求参数
     * @param key 数据键(表单元素名称)
     * @param value 数值值
     * @return 当前连接对象
     */
    public Connection data(String key, String value) {
        request.data(KeyValue.create(key, value));
        return this;
    }

    /**
     * 添加一个输入流作为请求数据参数。对于GETs，没有效果，但是对于POST，这将上传输入流。<br>
     * @param key 数据键(表单元素名称)
     * @param filename 文件名
     * @param inputStream 要上载的输入流，可能是从{@link java.io.FileInputStream}获得的，该方法不会主动流对象，需要在方法外{@code finally}代码块中关闭{@code close}
     * @return 当前连接对象
     */
    public Connection data(String key, String filename, InputStream inputStream) {
        request.data(KeyValue.create(key, filename, inputStream));
        return this;
    }

    /**
     * 将所有提供的数据添加到请求数据参数
     * @param data 参数集合
     * @return 当前连接对象
     */
    public Connection data(Map<String, String> data) {
        AssertUtil.notNull(data, "Data map must not be null");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            request.data(KeyValue.create(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * 将所有提供的数据添加到请求数据参数
     * @param data 参数集合
     * @return 当前连接对象
     */
    public Connection data(Collection<KeyValue> data) {
        AssertUtil.notNull(data, "Data collection must not be null");
        for (KeyValue entry : data) {
            request.data(entry);
        }
        return this;
    }

    /**
     * 获得参数值
     * @param key 参数键
     * @return 参数值
     */
    public KeyValue data(String key) {
        AssertUtil.notEmpty(key, "Data key must not be empty");
        for (KeyValue keyVal : request().data()) {
            if (keyVal.key().equals(key)) {
                return keyVal;
            }
        }
        return null;
    }

    /**
     * 设置POST（或PUT）请求主体<br>
     * <code>connect(url).requestBody(json).header("Content-Type", "application/json").post();</code>
     * @param body 请求主体数据
     * @return 当前连接对象
     */
    public Connection requestBody(String body) {
        request.requestBody(body);
        return this;
    }

    /**
     * 设置请求头
     * @param name 请求头名称
     * @param value 请求头值
     * @return 当前连接对象
     */
    public Connection header(String name, String value) {
        request.header(name, value);
        return this;
    }

    /**
     * 设置要在请求中发送的cookie
     * @param name Cookie 名
     * @param value Cookie 值
     * @return 当前连接对象
     */
    public Connection cookie(String name, String value) {
        request.cookie(name, value);
        return this;
    }

    /**
     * 设置要在请求中发送的cookie
     * @param cookies Cookie集合
     * @return 当前连接对象
     */
    public Connection cookies(Map<String, String> cookies) {
        AssertUtil.notNull(cookies, "Cookie map must not be null");
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            request.cookie(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 执行请求
     * @return 响应对象
     * @throws IOException 请求出现错误抛出异常
     */
    public Response execute() throws IOException {
        response = Response.execute(request);
        return response;
    }

    /**
     * 获取与此连接关联的请求对象
     * @return 请求对象
     */
    public Request request() {
        return request;
    }

    /**
     * Set the connection's request
     * @param request new request object
     * @return 当前连接对象
     */
    public Connection request(Request request) {
        this.request = request;
        return this;
    }

    /**
     * Get the response, once the request has been executed
     * @return response
     */
    public Response response() {
        return response;
    }

    /**
     * Set the connection's response
     * @param response new response
     * @return 当前连接对象
     */
    public Connection response(Response response) {
        this.response = response;
        return this;
    }

    /**
     * 为 x-www-form-urlencoded POST 请求数据设置字符集
     * @param charset 数据字符集
     * @return 当前连接对象
     */
    public Connection postDataCharset(String charset) {
        request.postDataCharset(charset);
        return this;
    }

    /**
     * 请求和响应的常用方法
     * @param <T> 实现类的类型， Request 或者 or Response
     */
    @SuppressWarnings({ "unchecked" })
    private static abstract class Base<T extends Base<T>> {

        URL url;
        Method method;
        Map<String, String> headers;
        Map<String, String> cookies;

        private Base() {
            headers = new LinkedHashMap<String, String>();
            cookies = new LinkedHashMap<String, String>();
        }

        public URL url() {
            return url;
        }

        public T url(URL url) {
            AssertUtil.notNull(url, "URL must not be null");
            this.url = url;
            return (T) this;
        }

        public Method method() {
            return method;
        }

        public T method(Method method) {
            AssertUtil.notNull(method, "Method must not be null");
            this.method = method;
            return (T) this;
        }

        public String header(String name) {
            AssertUtil.notNull(name, "Header name must not be null");
            return getHeaderCaseInsensitive(name);
        }

        public T header(String name, String value) {
            AssertUtil.notEmpty(name, "Header name must not be empty");
            AssertUtil.notNull(value, "Header value must not be null");
            removeHeader(name); // ensures we don't get an "accept-encoding" and a "Accept-Encoding"
            headers.put(name, value);
            return (T) this;
        }

        public boolean hasHeader(String name) {
            AssertUtil.notEmpty(name, "Header name must not be empty");
            return getHeaderCaseInsensitive(name) != null;
        }

        public boolean hasHeaderWithValue(String name, String value) {
            return hasHeader(name) && header(name).equalsIgnoreCase(value);
        }

        public T removeHeader(String name) {
            AssertUtil.notEmpty(name, "Header name must not be empty");
            Map.Entry<String, String> entry = scanHeaders(name); // remove is case insensitive too
            if (entry != null)
                headers.remove(entry.getKey()); // ensures correct case
            return (T) this;
        }

        public Map<String, String> headers() {
            return headers;
        }

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

        private Map.Entry<String, String> scanHeaders(String name) {
            String lc = name.toLowerCase();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey().toLowerCase().equals(lc))
                    return entry;
            }
            return null;
        }

        public String cookie(String name) {
            AssertUtil.notEmpty(name, "Cookie name must not be empty");
            return cookies.get(name);
        }

        public T cookie(String name, String value) {
            AssertUtil.notEmpty(name, "Cookie name must not be empty");
            AssertUtil.notNull(value, "Cookie value must not be null");
            cookies.put(name, value);
            return (T) this;
        }

        public boolean hasCookie(String name) {
            AssertUtil.notEmpty(name, "Cookie name must not be empty");
            return cookies.containsKey(name);
        }

        public T removeCookie(String name) {
            AssertUtil.notEmpty(name, "Cookie name must not be empty");
            cookies.remove(name);
            return (T) this;
        }

        public Map<String, String> cookies() {
            return cookies;
        }
    }

    /**
     * 表示HTTP请求
     */
    public static class Request extends Base<Request> {
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

        private Request() {
            connectTimeoutMillis = 3000;
            readTimeoutMillis = 30000;
            maxBodySizeBytes = 1024 * 1024; // 1MB
            followRedirects = true;
            data = new ArrayList<KeyValue>();
            method = Method.GET;
            headers.put("Accept-Encoding", "gzip");
        }

        public Proxy proxy() {
            return proxy;
        }

        public Request proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Request proxy(String host, int port) {
            this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
            return this;
        }

        public int connectTimeoutMillis() {
            return connectTimeoutMillis;
        }

        public Request connectTimeoutMillis(int millis) {
            AssertUtil.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            connectTimeoutMillis = millis;
            return this;
        }

        public int readTimeoutMillis() {
            return readTimeoutMillis;
        }

        public Request readTimeoutMillis(int millis) {
            AssertUtil.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            readTimeoutMillis = millis;
            return this;
        }

        public int maxBodySize() {
            return maxBodySizeBytes;
        }

        public Request maxBodySize(int bytes) {
            AssertUtil.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
            maxBodySizeBytes = bytes;
            return this;
        }

        public boolean followRedirects() {
            return followRedirects;
        }

        public Request followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public boolean ignoreHttpErrors() {
            return ignoreHttpErrors;
        }

        public Request ignoreHttpErrors(boolean ignoreHttpErrors) {
            this.ignoreHttpErrors = ignoreHttpErrors;
            return this;
        }

        public boolean ignoreContentType() {
            return ignoreContentType;
        }

        public Request ignoreContentType(boolean ignoreContentType) {
            this.ignoreContentType = ignoreContentType;
            return this;
        }

        public boolean validateTLSCertificates() {
            return validateTLSCertificates;
        }

        public void validateTLSCertificates(boolean value) {
            this.validateTLSCertificates = value;
        }

        public Request data(KeyValue keyval) {
            AssertUtil.notNull(keyval, "Key val must not be null");
            data.add(keyval);
            return this;
        }

        public Collection<KeyValue> data() {
            return data;
        }

        public Request requestBody(String body) {
            this.body = body;
            return this;
        }

        public String requestBody() {
            return body;
        }

        public Request postDataCharset(String charset) {
            AssertUtil.notNull(charset, "Charset must not be null");
            if (!Charset.isSupported(charset))
                throw new IllegalCharsetNameException(charset);
            this.postDataCharset = charset;
            return this;
        }

        public String postDataCharset() {
            return postDataCharset;
        }
    }

    /**
     * 表示HTTP响应
     */
    public static class Response extends Base<Response> {
        private static final int MAX_REDIRECTS = 20;
        private static SSLSocketFactory sslSocketFactory;
        private static final String LOCATION = "Location";
        private int statusCode;
        private String statusMessage;
        private ByteBuffer byteData;
        private String charset;
        private String contentType;
        private boolean executed = false;
        private int numRedirects = 0;
        /* Matches XML content types (like text/xml, application/xhtml+xml;charset=UTF8, etc) */
        private static final Pattern xmlContentTypeRxp = Pattern.compile("(application|text)/\\w*\\+?xml.*");

        Response() {
            super();
        }

        private Response(Response previousResponse) throws IOException {
            super();
            if (previousResponse != null) {
                numRedirects = previousResponse.numRedirects + 1;
                if (numRedirects >= MAX_REDIRECTS)
                    throw new IOException(String.format("Too many redirects occurred trying to load URL %s", previousResponse.url()));
            }
        }

        static Response execute(Request req) throws IOException {
            return execute(req, null);
        }

        static Response execute(Request req, Response previousResponse) throws IOException {
            AssertUtil.notNull(req, "Request must not be null");
            String protocol = req.url().getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https"))
                throw new MalformedURLException("Only http & https protocols supported");
            final boolean methodHasBody = req.method().hasBody();
            final boolean hasRequestBody = req.requestBody() != null;
            if (!methodHasBody)
                AssertUtil.isFalse(hasRequestBody, "Cannot set a request body for HTTP method " + req.method());

            // set up the request for execution
            String mimeBoundary = null;
            if (req.data().size() > 0 && (!methodHasBody || hasRequestBody))
                serialiseRequestUrl(req);
            else if (methodHasBody)
                mimeBoundary = setOutputContentType(req);

            HttpURLConnection conn = createConnection(req);
            Response res;
            try {
                conn.connect();
                if (conn.getDoOutput())
                    writePost(req, conn.getOutputStream(), mimeBoundary);

                int status = conn.getResponseCode();
                res = new Response(previousResponse);
                res.setupFromConnection(conn, previousResponse);

                // redirect if there's a location header (from 3xx, or 201 etc)
                if (res.hasHeader(LOCATION) && req.followRedirects()) {
                    if (status != HTTP_TEMP_REDIRECT_STATUS) {
                        req.method(Method.GET); // always redirect with a get. any data param from
                                                // original req are dropped.
                        req.data().clear();
                    }

                    String location = res.header(LOCATION);
                    if (location != null && location.startsWith("http:/") && location.charAt(6) != '/') // fix
                                                                                                        // broken
                                                                                                        // Location:
                                                                                                        // http:/temp/AAG_New/en/index.php
                        location = location.substring(6);
                    req.url(resolve(req.url(), DataUtil.encodeUrl(location)));

                    for (Map.Entry<String, String> cookie : res.cookies.entrySet()) { // add
                                                                                      // response
                                                                                      // cookies to
                                                                                      // request
                                                                                      // (for e.g.
                                                                                      // login
                                                                                      // posts)
                        req.cookie(cookie.getKey(), cookie.getValue());
                    }
                    return execute(req, res);
                }
                if ((status < 200 || status >= 400) && !req.ignoreHttpErrors())
                    throw new IOException("HTTP error fetching URL " + status + " " + req.url().toString());

                // check that we can handle the returned content type; if not, abort before fetching
                // it
                String contentType = res.contentType();
                if (contentType != null && !req.ignoreContentType() && !contentType.startsWith("text/")
                        && !xmlContentTypeRxp.matcher(contentType).matches())
                    throw new IOException("Unhandled content type[" + contentType + "]. Must be text/*, application/xml, or application/xhtml+xml "
                            + req.url().toString());

                res.charset = DataUtil.getCharsetFromContentType(res.contentType); // may be null,
                                                                                   // readInputStream
                                                                                   // deals with it
                if (conn.getContentLength() != 0 && req.method() != Method.HEAD) { // -1 means
                                                                                   // unknown,
                                                                                   // chunked. sun
                                                                                   // throws an IO
                                                                                   // exception on
                                                                                   // 500
                                                                                   // response with
                                                                                   // no content
                                                                                   // when trying to
                                                                                   // read body
                    InputStream bodyStream = null;
                    try {
                        bodyStream = conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream();
                        if (res.hasHeaderWithValue(CONTENT_ENCODING, "gzip"))
                            bodyStream = new GZIPInputStream(bodyStream);

                        res.byteData = DataUtil.readToByteBuffer(bodyStream, req.maxBodySize());
                    } finally {
                        if (bodyStream != null)
                            bodyStream.close();
                    }
                } else {
                    res.byteData = DataUtil.emptyByteBuffer();
                }
            } finally {
                // per Java's documentation, this is not necessary, and precludes keepalives.
                // However in practise,
                // connection errors will not be released quickly enough and can cause a too many
                // open files error.
                conn.disconnect();
            }

            res.executed = true;
            return res;
        }

        public int statusCode() {
            return statusCode;
        }

        public String statusMessage() {
            return statusMessage;
        }

        public String charset() {
            return charset;
        }

        public String contentType() {
            return contentType;
        }

        public String body() {
            AssertUtil.isTrue(executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            // charset gets set from header on execute, and from meta-equiv on parse. parse may not
            // have happened yet
            String body;
            if (charset == null)
                body = Charset.forName(DataUtil.DEFAULT_CHARSET).decode(byteData).toString();
            else
                body = Charset.forName(charset).decode(byteData).toString();
            byteData.rewind();
            return body;
        }

        public byte[] bodyAsBytes() {
            AssertUtil.isTrue(executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
            return byteData.array();
        }

        // set up connection defaults, and details from request
        private static HttpURLConnection createConnection(Request req) throws IOException {
            final HttpURLConnection conn = (HttpURLConnection) (req.proxy() == null ? req.url().openConnection()
                    : req.url().openConnection(req.proxy()));

            conn.setRequestMethod(req.method().name());
            conn.setInstanceFollowRedirects(false); // don't rely on native redirection support
            conn.setConnectTimeout(req.connectTimeoutMillis());
            conn.setReadTimeout(req.readTimeoutMillis());

            if (conn instanceof HttpsURLConnection) {
                if (!req.validateTLSCertificates()) {
                    initUnSecureTSL();
                    ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
                    ((HttpsURLConnection) conn).setHostnameVerifier(getInsecureVerifier());
                }
            }

            if (req.method().hasBody())
                conn.setDoOutput(true);
            if (req.cookies().size() > 0)
                conn.addRequestProperty("Cookie", getRequestCookieString(req));
            for (Map.Entry<String, String> header : req.headers().entrySet()) {
                conn.addRequestProperty(header.getKey(), header.getValue());
            }
            return conn;
        }

        /**
         * 实例化不执行任何操作的主机名验证器，这用于禁用SSL证书验证的连接。
         * @return 主机名验证器
         */
        private static HostnameVerifier getInsecureVerifier() {
            return new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
        }

        /**
         * 初始化不验证证书链的信任管理器，并将其添加到当前SSLContext。<br>
         * 请注意，此方法仅在sslSocketFactory尚未实例化时执行操作。<br>
         * @throws IOException 生成证书链错误
         */
        private static synchronized void initUnSecureTSL() throws IOException {
            if (sslSocketFactory == null) {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

                    public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                    }

                    public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                } };

                // Install the all-trusting trust manager
                final SSLContext sslContext;
                try {
                    sslContext = SSLContext.getInstance("SSL");
                    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                    // Create an ssl socket factory with our all-trusting manager
                    sslSocketFactory = sslContext.getSocketFactory();
                } catch (NoSuchAlgorithmException e) {
                    throw new IOException("Can't create unsecure trust manager");
                } catch (KeyManagementException e) {
                    throw new IOException("Can't create unsecure trust manager");
                }
            }

        }

        // set up url, method, header, cookies
        private void setupFromConnection(HttpURLConnection conn, Response previousResponse) throws IOException {
            method = Method.valueOf(conn.getRequestMethod());
            url = conn.getURL();
            statusCode = conn.getResponseCode();
            statusMessage = conn.getResponseMessage();
            contentType = conn.getContentType();

            Map<String, List<String>> resHeaders = createHeaderMap(conn);
            processResponseHeaders(resHeaders);

            // if from a redirect, map previous response cookies into this response
            if (previousResponse != null) {
                for (Map.Entry<String, String> prevCookie : previousResponse.cookies().entrySet()) {
                    if (!hasCookie(prevCookie.getKey()))
                        cookie(prevCookie.getKey(), prevCookie.getValue());
                }
            }
        }

        private static LinkedHashMap<String, List<String>> createHeaderMap(HttpURLConnection conn) {
            // the default sun impl of conn.getHeaderFields() returns header values out of order
            final LinkedHashMap<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
            int i = 0;
            while (true) {
                final String key = conn.getHeaderFieldKey(i);
                final String val = conn.getHeaderField(i);
                if (key == null && val == null)
                    break;
                i++;
                if (key == null || val == null)
                    continue; // skip http1.1 line

                if (headers.containsKey(key))
                    headers.get(key).add(val);
                else {
                    final ArrayList<String> vals = new ArrayList<String>();
                    vals.add(val);
                    headers.put(key, vals);
                }
            }
            return headers;
        }

        void processResponseHeaders(Map<String, List<String>> resHeaders) {
            for (Map.Entry<String, List<String>> entry : resHeaders.entrySet()) {
                String name = entry.getKey();
                if (name == null)
                    continue; // http/1.1 line

                List<String> values = entry.getValue();
                if (name.equalsIgnoreCase("Set-Cookie")) {
                    for (String value : values) {
                        if (value == null) {
                            continue;
                        }
                        int pos = 0;
                        int offset = 0;
                        String cookieName;
                        String cookieValue;
                        if ((offset = value.indexOf('=', pos)) != -1) {
                            pos += (cookieName = value.substring(pos, offset)).length() + 1;
                        } else {
                            cookieName = value.substring(pos, pos = value.length());
                        }
                        if ((offset = value.indexOf(';', pos)) != -1) {
                            pos = (cookieValue = value.substring(pos, offset)).length() + 1;
                        } else {
                            cookieValue = value.substring(pos, pos = value.length());
                        }
                        cookieName = cookieName.trim();
                        cookieValue = cookieValue.trim();
                        if (cookieName.length() > 0) {
                            cookie(cookieName, cookieValue);
                        }
                    }
                } else {
                    // combine same header names with comma:
                    // http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
                    if (values.size() == 1) {
                        header(name, values.get(0));
                    } else if (values.size() > 1) {
                        StringBuilder accum = new StringBuilder();
                        for (int i = 0; i < values.size(); i++) {
                            final String val = values.get(i);
                            if (i != 0) {
                                accum.append(", ");
                            }
                            accum.append(val);
                        }
                        header(name, accum.toString());
                    }
                }
            }
        }

        private static String setOutputContentType(final Request req) {
            String bound = null;
            if (needsMultipart(req)) {
                bound = DataUtil.mimeBoundary();
                req.header(CONTENT_TYPE, MULTIPART_FORM_DATA + "; boundary=" + bound);
            } else {
                req.header(CONTENT_TYPE, FORM_URL_ENCODED + "; charset=" + req.postDataCharset());
            }
            return bound;
        }

        private static void writePost(final Request req, final OutputStream outputStream, final String bound) throws IOException {
            final Collection<KeyValue> data = req.data();
            final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, req.postDataCharset()));

            if (bound != null) {
                // boundary will be set if we're in multipart mode
                for (KeyValue keyVal : data) {
                    w.write("--");
                    w.write(bound);
                    w.write("\r\n");
                    w.write("Content-Disposition: form-data; name=\"");
                    w.write(DataUtil.encodeMimeName(keyVal.key())); // encodes " to %22
                    w.write("\"");
                    if (keyVal.hasInputStream()) {
                        w.write("; filename=\"");
                        w.write(DataUtil.encodeMimeName(keyVal.value()));
                        w.write("\"\r\nContent-Type: application/octet-stream\r\n\r\n");
                        w.flush(); // flush
                        IoUtil.copy(keyVal.inputStream(), outputStream);
                        outputStream.flush();
                    } else {
                        w.write("\r\n\r\n");
                        w.write(keyVal.value());
                    }
                    w.write("\r\n");
                }
                w.write("--");
                w.write(bound);
                w.write("--");
            } else if (req.requestBody() != null) {
                // data will be in query string, we're sending a plaintext body
                w.write(req.requestBody());
            } else {
                // regular form data (application/x-www-form-urlencoded)
                boolean first = true;
                for (KeyValue keyVal : data) {
                    if (!first)
                        w.append('&');
                    else
                        first = false;

                    w.write(URLEncoder.encode(keyVal.key(), req.postDataCharset()));
                    w.write('=');
                    w.write(URLEncoder.encode(keyVal.value(), req.postDataCharset()));
                }
            }
            w.close();
        }

        private static String getRequestCookieString(Request req) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> cookie : req.cookies().entrySet()) {
                if (!first)
                    sb.append("; ");
                else
                    first = false;
                sb.append(cookie.getKey()).append('=').append(cookie.getValue());
                // todo: spec says only ascii, no escaping / encoding defined. validate on set? or
                // escape somehow here?
            }
            return sb.toString();
        }

        // for get url reqs, serialise the data map into the url
        private static void serialiseRequestUrl(Request req) throws IOException {
            URL in = req.url();
            StringBuilder url = new StringBuilder();
            boolean first = true;
            // reconstitute the query, ready for appends
            url.append(in.getProtocol()).append("://").append(in.getAuthority()) // includes host,
                                                                                 // port
                    .append(in.getPath()).append("?");
            if (in.getQuery() != null) {
                url.append(in.getQuery());
                first = false;
            }
            for (KeyValue keyVal : req.data()) {
                AssertUtil.isFalse(keyVal.hasInputStream(), "InputStream data not supported in URL query string.");
                if (!first)
                    url.append('&');
                else
                    first = false;
                url.append(URLEncoder.encode(keyVal.key(), DataUtil.DEFAULT_CHARSET)).append('=')
                        .append(URLEncoder.encode(keyVal.value(), DataUtil.DEFAULT_CHARSET));
            }
            req.url(new URL(url.toString()));
            req.data().clear(); // moved into url as get params
        }

        private static URL resolve(URL base, String relUrl) throws MalformedURLException {
            // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not
            // '//path/file?foo' as desired
            if (relUrl.startsWith("?"))
                relUrl = base.getPath() + relUrl;
            // workaround: //example.com + ./foo = //example.com/./foo, not //example.com/foo
            if (relUrl.indexOf('.') == 0 && base.getFile().indexOf('/') != 0) {
                base = new URL(base.getProtocol(), base.getHost(), base.getPort(), "/" + base.getFile());
            }
            return new URL(base, relUrl);
        }

    }

    private static boolean needsMultipart(Request req) {
        // multipart mode, for files. add the header if we see something with an inputstream, and
        // return a non-null boundary
        boolean needsMulti = false;
        for (KeyValue keyVal : req.data()) {
            if (keyVal.hasInputStream()) {
                needsMulti = true;
                break;
            }
        }
        return needsMulti;
    }

    /**
     * 键值元组(tuple)
     */
    public static class KeyValue {
        private String key;
        private String value;
        private InputStream stream;

        public static KeyValue create(String key, String value) {
            return new KeyValue().key(key).value(value);
        }

        public static KeyValue create(String key, String filename, InputStream stream) {
            return new KeyValue().key(key).value(filename).inputStream(stream);
        }

        private KeyValue() {
        }

        public KeyValue key(String key) {
            AssertUtil.notEmpty(key, "Data key must not be empty");
            this.key = key;
            return this;
        }

        public String key() {
            return key;
        }

        public KeyValue value(String value) {
            AssertUtil.notNull(value, "Data value must not be null");
            this.value = value;
            return this;
        }

        public String value() {
            return value;
        }

        public KeyValue inputStream(InputStream inputStream) {
            AssertUtil.notNull(value, "Data input stream must not be null");
            this.stream = inputStream;
            return this;
        }

        public InputStream inputStream() {
            return stream;
        }

        public boolean hasInputStream() {
            return stream != null;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * HTTP方法枚举类
     */
    public static enum Method {

        GET(false), POST(true), PUT(true), DELETE(false), PATCH(true), HEAD(false), OPTIONS(false), TRACE(false);

        private final boolean hasBody;

        Method(boolean hasBody) {
            this.hasBody = hasBody;
        }

        /**
         * 检查此HTTP方法是否包含请求体
         * @return 是否包含请求体
         */
        public final boolean hasBody() {
            return hasBody;
        }
    }
}
