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
import com.github.relucent.base.common.lang.Assert;

/**
 * A Connection provides a convenient implementation to fetch content from the web.
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

    public Connection url(URL url) {
        request.url(url);
        return this;
    }

    /**
     * Set the request URL to fetch. The protocol must be HTTP or HTTPS.
     * @param url URL to connect to
     * @return this Connection, for chaining
     */
    public Connection url(String url) {
        Assert.notEmpty(url, "Must supply a valid URL");
        try {
            request.url(new URL(DataUtil.encodeUrl(url)));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
        return this;
    }

    /**
     * Set the request URL to fetch. The protocol must be HTTP or HTTPS.
     * @param url URL to connect to
     * @return this Connection, for chaining
     */
    public Connection proxy(Proxy proxy) {
        request.proxy(proxy);
        return this;
    }

    /**
     * Set the proxy to use for this request. Set to <code>null</code> to disable.
     * @param proxy proxy to use
     * @return this Connection, for chaining
     */
    public Connection proxy(String host, int port) {
        request.proxy(host, port);
        return this;
    }

    /**
     * Set the proxy to use for this request. Set to <code>null</code> to disable.
     * @param proxy proxy to use
     * @param port the proxy username
     * @param port the proxy password
     * @return this Connection, for chaining
     */
    public Connection proxy(Proxy proxy, String username, String password) {
        request.proxy(proxy);
        header("Proxy-Authorization", "Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes()));
        return this;
    }

    /**
     * Set the HTTP proxy to use for this request.
     * @param host the proxy hostname
     * @param port the proxy port
     * @param port the proxy username
     * @param port the proxy password
     * @return this Connection, for chaining
     */
    public Connection proxy(String host, int port, String username, String password) {
        request.proxy(host, port);
        header("Proxy-Authorization", "Basic " + DatatypeConverter.printBase64Binary((username + ":" + password).getBytes()));
        return this;
    }

    /**
     * Set the request user-agent header.
     * @param userAgent user-agent to use
     * @return this Connection, for chaining
     */
    public Connection userAgent(String userAgent) {
        Assert.notNull(userAgent, "User agent must not be null");
        request.header("User-Agent", userAgent);
        return this;
    }

    /**
     * Set the request timeouts (connect). A timeout of zero is treated as an infinite timeout.
     * @param millis number of milliseconds (thousandths of a second) before timing out connects or reads.
     * @return this Connection, for chaining
     */
    public Connection connectTimeoutMillis(int millis) {
        request.connectTimeoutMillis(millis);
        return this;
    }

    /**
     * Set the request timeouts (connect). A timeout of zero is treated as an infinite timeout.
     * @param duration the duration
     * @param unit time unit
     * @return this Connection, for chaining
     */
    public Connection connectTimeout(int duration, TimeUnit unit) {
        request.connectTimeoutMillis((int) unit.toMillis(duration));
        return this;
    }

    /**
     * Set the request timeouts (connect and read). A timeout of zero is treated as an infinite timeout.
     * @param millis number of milliseconds (thousandths of a second) before timing out connects or reads.
     * @return this Connection, for chaining
     */
    public Connection readTimeoutMillis(int millis) {
        request.readTimeoutMillis(millis);
        return this;
    }

    /**
     * Set the request timeouts (connect). A timeout of zero is treated as an infinite timeout.
     * @param duration the duration
     * @param unit time unit
     * @return this Connection, for chaining
     */
    public Connection readTimeout(int duration, TimeUnit unit) {
        request.readTimeoutMillis((int) unit.toMillis(duration));
        return this;
    }

    /**
     * Set the maximum bytes to read from the (uncompressed) connection into the body, before the connection is closed, and the input truncated. The default maximum is 1MB. A max size of zero is treated as an infinite amount (bounded only by your patience and
     * the memory available on your machine).
     * @param bytes number of bytes to read from the input before truncating
     * @return this Connection, for chaining
     */
    public Connection maxBodySize(int bytes) {
        request.maxBodySize(bytes);
        return this;
    }

    public Connection followRedirects(boolean followRedirects) {
        request.followRedirects(followRedirects);
        return this;
    }

    /**
     * Set the request referrer (aka "referer") header.
     * @param referrer referrer to use
     * @return this Connection, for chaining
     */
    public Connection referrer(String referrer) {
        Assert.notNull(referrer, "Referrer must not be null");
        request.header("Referer", referrer);
        return this;
    }

    /**
     * Set the request method to use, GET or POST. Default is GET.
     * @param method HTTP request method
     * @return this Connection, for chaining
     */
    public Connection method(Method method) {
        request.method(method);
        return this;
    }

    /**
     * Configures the connection to not throw exceptions when a HTTP error occurs. (4xx - 5xx, e.g. 404 or 500). By default this is <b>false</b>; an IOException is thrown if an error is encountered. If set to <b>true</b>, the response is populated with the
     * error body, and the status message will reflect the error.
     * @param ignoreHttpErrors - false (default) if HTTP errors should be ignored.
     * @return this Connection, for chaining
     */
    public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
        request.ignoreHttpErrors(ignoreHttpErrors);
        return this;
    }

    /**
     * Ignore the document's Content-Type when parsing the response. By default this is <b>false</b>, an unrecognised content-type will cause an IOException to be thrown. (This is to prevent producing garbage by attempting to parse a JPEG binary image, for
     * example.) Set to true to force a parse attempt regardless of content type.
     * @param ignoreContentType set to true if you would like the content type ignored on parsing the response into a Document.
     * @return this Connection, for chaining
     */
    public Connection ignoreContentType(boolean ignoreContentType) {
        request.ignoreContentType(ignoreContentType);
        return this;
    }

    /**
     * Disable/enable TSL certificates validation for HTTPS requests.
     * @param value if should validate TSL (SSL) certificates. <b>true</b> by default.
     * @return this Connection, for chaining
     */
    public Connection validateTLSCertificates(boolean value) {
        request.validateTLSCertificates(value);
        return this;
    }

    /**
     * Add a request data parameter.
     * @param key data key
     * @param value data value
     * @return this Connection, for chaining
     */
    public Connection data(String key, String value) {
        request.data(KeyVal.create(key, value));
        return this;
    }

    /**
     * Add an input stream as a request data paramater. For GETs, has no effect, but for POSTS this will upload the input stream.
     * @param key data key (form item name)
     * @param filename the name of the file to present to the remove server. Typically just the name, not path, component.
     * @param inputStream the input stream to upload, that you probably obtained from a {@link java.io.FileInputStream}. You must close the InputStream in a {@code finally} block.
     * @return this Connections, for chaining
     */
    public Connection data(String key, String filename, InputStream inputStream) {
        request.data(KeyVal.create(key, filename, inputStream));
        return this;
    }

    /**
     * Adds all of the supplied data to the request data parameters
     * @param data collection of data parameters
     * @return this Connection, for chaining
     */
    public Connection data(Map<String, String> data) {
        Assert.notNull(data, "Data map must not be null");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            request.data(KeyVal.create(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    /**
     * Adds all of the supplied data to the request data parameters
     * @param data map of data parameters
     * @return this Connection, for chaining
     */
    public Connection data(Collection<KeyVal> data) {
        Assert.notNull(data, "Data collection must not be null");
        for (KeyVal entry : data) {
            request.data(entry);
        }
        return this;
    }

    /**
     * Get the data KeyVal for this key, if any
     * @param key the data key
     * @return null if not set
     */
    public KeyVal data(String key) {
        Assert.notEmpty(key, "Data key must not be empty");
        for (KeyVal keyVal : request().data()) {
            if (keyVal.key().equals(key))
                return keyVal;
        }
        return null;
    }

    /**
     * Set a POST (or PUT) request body. Useful when a server expects a plain request body, not a set for URL encoded form key/value pairs. E.g.: <code><pre>connect(url)
     * .requestBody(json)
     * .header("Content-Type", "application/json")
     * .post();</pre></code> If any data key/vals are supplied, they will be sent as URL query params.
     * @return this Request, for chaining
     */
    public Connection requestBody(String body) {
        request.requestBody(body);
        return this;
    }

    /**
     * Set a request header.
     * @param name header name
     * @param value header value
     * @return this Connection, for chaining
     */
    public Connection header(String name, String value) {
        request.header(name, value);
        return this;
    }

    /**
     * Set a cookie to be sent in the request.
     * @param name name of cookie
     * @param value value of cookie
     * @return this Connection, for chaining
     */
    public Connection cookie(String name, String value) {
        request.cookie(name, value);
        return this;
    }

    /**
     * Adds each of the supplied cookies to the request.
     * @param cookies map of cookie name {@literal ->} value pairs
     * @return this Connection, for chaining
     */
    public Connection cookies(Map<String, String> cookies) {
        Assert.notNull(cookies, "Cookie map must not be null");
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            request.cookie(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Execute the request.
     * @return a response object
     * @throws java.net.MalformedURLException if the request URL is not a HTTP or HTTPS URL, or is otherwise malformed
     * @throws HttpStatusException if the response is not OK and HTTP response errors are not ignored
     * @throws UnsupportedMimeTypeException if the response mime type is not supported and those errors are not ignored
     * @throws java.net.SocketTimeoutException if the connection times out
     * @throws IOException on error
     */
    public Response execute() throws IOException {
        response = Response.execute(request);
        return response;
    }

    /**
     * Get the request object associated with this connection
     * @return request
     */
    public Request request() {
        return request;
    }

    /**
     * Set the connection's request
     * @param request new request object
     * @return this Connection, for chaining
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
     * @return this Connection, for chaining
     */
    public Connection response(Response response) {
        this.response = response;
        return this;
    }

    /**
     * Sets the default post data character set for x-www-form-urlencoded post data
     * @param charset character set to encode post data
     * @return this Connection, for chaining
     */
    public Connection postDataCharset(String charset) {
        request.postDataCharset(charset);
        return this;
    }

    /**
     * Common methods for Requests and Responses
     * @param <T> Type of Base, either Request or Response
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

        /**
         * Get the URL
         * @return URL
         */
        public URL url() {
            return url;
        }

        /**
         * Set the URL
         * @param url new URL
         * @return this, for chaining
         */
        public T url(URL url) {
            Assert.notNull(url, "URL must not be null");
            this.url = url;
            return (T) this;
        }

        /**
         * Get the request method
         * @return method
         */
        public Method method() {
            return method;
        }

        /**
         * Set the request method
         * @param method new method
         * @return this, for chaining
         */
        public T method(Method method) {
            Assert.notNull(method, "Method must not be null");
            this.method = method;
            return (T) this;
        }

        /**
         * Get the value of a header. This is a simplified header model, where a header may only have one value.
         * <p>
         * Header names are case insensitive.
         * </p>
         * @param name name of header (case insensitive)
         * @return value of header, or null if not set.
         * @see #hasHeader(String)
         * @see #cookie(String)
         */
        public String header(String name) {
            Assert.notNull(name, "Header name must not be null");
            return getHeaderCaseInsensitive(name);
        }

        /**
         * Set a header. This method will overwrite any existing header with the same case insensitive name.
         * @param name Name of header
         * @param value Value of header
         * @return this, for chaining
         */
        public T header(String name, String value) {
            Assert.notEmpty(name, "Header name must not be empty");
            Assert.notNull(value, "Header value must not be null");
            removeHeader(name); // ensures we don't get an "accept-encoding" and a "Accept-Encoding"
            headers.put(name, value);
            return (T) this;
        }

        /**
         * Check if a header is present
         * @param name name of header (case insensitive)
         * @return if the header is present in this request/response
         */
        public boolean hasHeader(String name) {
            Assert.notEmpty(name, "Header name must not be empty");
            return getHeaderCaseInsensitive(name) != null;
        }

        /**
         * Check if a header is present, with the given value
         * @param name header name (case insensitive)
         * @param value value (case insensitive)
         * @return if the header and value pair are set in this req/res
         */
        public boolean hasHeaderWithValue(String name, String value) {
            return hasHeader(name) && header(name).equalsIgnoreCase(value);
        }

        /**
         * Remove a header by name
         * @param name name of header to remove (case insensitive)
         * @return this, for chaining
         */
        public T removeHeader(String name) {
            Assert.notEmpty(name, "Header name must not be empty");
            Map.Entry<String, String> entry = scanHeaders(name); // remove is case insensitive too
            if (entry != null)
                headers.remove(entry.getKey()); // ensures correct case
            return (T) this;
        }

        /**
         * Retrieve all of the request/response headers as a map
         * @return headers
         */
        public Map<String, String> headers() {
            return headers;
        }

        private String getHeaderCaseInsensitive(String name) {
            Assert.notNull(name, "Header name must not be null");
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

        /**
         * Get a cookie value by name from this request/response.
         * <p>
         * Response objects have a simplified cookie model. Each cookie set in the response is added to the response object's cookie key=value map. The cookie's path, domain, and expiry date are ignored.
         * </p>
         * @param name name of cookie to retrieve.
         * @return value of cookie, or null if not set
         */
        public String cookie(String name) {
            Assert.notEmpty(name, "Cookie name must not be empty");
            return cookies.get(name);
        }

        /**
         * Set a cookie in this request/response.
         * @param name name of cookie
         * @param value value of cookie
         * @return this, for chaining
         */
        public T cookie(String name, String value) {
            Assert.notEmpty(name, "Cookie name must not be empty");
            Assert.notNull(value, "Cookie value must not be null");
            cookies.put(name, value);
            return (T) this;
        }

        /**
         * Check if a cookie is present
         * @param name name of cookie
         * @return if the cookie is present in this request/response
         */
        public boolean hasCookie(String name) {
            Assert.notEmpty(name, "Cookie name must not be empty");
            return cookies.containsKey(name);
        }

        /**
         * Remove a cookie by name
         * @param name name of cookie to remove
         * @return this, for chaining
         */
        public T removeCookie(String name) {
            Assert.notEmpty(name, "Cookie name must not be empty");
            cookies.remove(name);
            return (T) this;
        }

        /**
         * Retrieve all of the request/response cookies as a map
         * @return cookies
         */
        public Map<String, String> cookies() {
            return cookies;
        }
    }

    /**
     * Represents a HTTP request.
     */
    public static class Request extends Base<Request> {
        private Proxy proxy; // nullable
        private int connectTimeoutMillis;// milliseconds
        private int readTimeoutMillis;// milliseconds
        private int maxBodySizeBytes;
        private boolean followRedirects;
        private Collection<KeyVal> data;
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
            data = new ArrayList<KeyVal>();
            method = Method.GET;
            headers.put("Accept-Encoding", "gzip");
        }

        /**
         * Get the proxy used for this request.
         * @return the proxy; <code>null</code> if not enabled.
         */
        public Proxy proxy() {
            return proxy;
        }

        /**
         * Update the proxy for this request.
         * @param proxy the proxy ot use; <code>null</code> to disable.
         * @return this Request, for chaining
         */
        public Request proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Set the HTTP proxy to use for this request.
         * @param host the proxy hostname
         * @param port the proxy port
         * @return this Connection, for chaining
         */
        public Request proxy(String host, int port) {
            this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
            return this;
        }

        /**
         * Get the request timeout, in milliseconds.
         * @return the timeout in milliseconds.
         */
        public int connectTimeoutMillis() {
            return connectTimeoutMillis;
        }

        /**
         * Update the request timeout.
         * @param millis timeout, in milliseconds
         * @return this Request, for chaining
         */
        public Request connectTimeoutMillis(int millis) {
            Assert.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            connectTimeoutMillis = millis;
            return this;
        }

        /**
         * Get the request timeout, in milliseconds.
         * @return the timeout in milliseconds.
         */
        public int readTimeoutMillis() {
            return readTimeoutMillis;
        }

        /**
         * Update the request timeout.
         * @param millis timeout, in milliseconds
         * @return this Request, for chaining
         */
        public Request readTimeoutMillis(int millis) {
            Assert.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
            readTimeoutMillis = millis;
            return this;
        }

        /**
         * Get the maximum body size, in bytes.
         * @return the maximum body size, in bytes.
         */
        public int maxBodySize() {
            return maxBodySizeBytes;
        }

        /**
         * Update the maximum body size, in bytes.
         * @param bytes maximum body size, in bytes.
         * @return this Request, for chaining
         */
        public Request maxBodySize(int bytes) {
            Assert.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
            maxBodySizeBytes = bytes;
            return this;
        }

        /**
         * Get the current followRedirects configuration.
         * @return true if followRedirects is enabled.
         */
        public boolean followRedirects() {
            return followRedirects;
        }

        /**
         * Configures the request to (not) follow server redirects. By default this is <b>true</b>.
         * @param followRedirects true if server redirects should be followed.
         * @return this Request, for chaining
         */
        public Request followRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        /**
         * Get the current ignoreHttpErrors configuration.
         * @return true if errors will be ignored; false (default) if HTTP errors will cause an IOException to be thrown.
         */
        public boolean ignoreHttpErrors() {
            return ignoreHttpErrors;
        }

        /**
         * Configures the request to ignore HTTP errors in the response.
         * @param ignoreHttpErrors set to true to ignore HTTP errors.
         * @return this Request, for chaining
         */
        public Request ignoreHttpErrors(boolean ignoreHttpErrors) {
            this.ignoreHttpErrors = ignoreHttpErrors;
            return this;
        }

        /**
         * Get the current ignoreContentType configuration.
         * @return true if invalid content-types will be ignored; false (default) if they will cause an IOException to be thrown.
         */
        public boolean ignoreContentType() {
            return ignoreContentType;
        }

        /**
         * Configures the request to ignore the Content-Type of the response.
         * @param ignoreContentType set to true to ignore the content type.
         * @return this Request, for chaining
         */
        public Request ignoreContentType(boolean ignoreContentType) {
            this.ignoreContentType = ignoreContentType;
            return this;
        }

        /**
         * Get the current state of TLS (SSL) certificate validation.
         * @return true if TLS cert validation enabled
         */
        public boolean validateTLSCertificates() {
            return validateTLSCertificates;
        }

        /**
         * Set TLS certificate validation.
         * @param value set false to ignore TLS (SSL) certificates
         */
        public void validateTLSCertificates(boolean validateTLSCertificates) {
            this.validateTLSCertificates = validateTLSCertificates;
        }

        /**
         * Add a data parameter to the request
         * @param keyval data to add.
         * @return this Request, for chaining
         */
        public Request data(KeyVal keyval) {
            Assert.notNull(keyval, "Key val must not be null");
            data.add(keyval);
            return this;
        }

        /**
         * Get all of the request's data parameters
         * @return collection of keyvals
         */
        public Collection<KeyVal> data() {
            return data;
        }

        /**
         * Set a POST (or PUT) request body. Useful when a server expects a plain request body, not a set for URL encoded form key/value pairs. E.g.: <code><pre>connect(url)
         * .requestBody(json)
         * .header("Content-Type", "application/json")
         * .post();</pre></code> If any data key/vals are supplied, they will be sent as URL query params.
         * @return this Request, for chaining
         */
        public Request requestBody(String body) {
            this.body = body;
            return this;
        }

        /**
         * Get the current request body.
         * @return null if not set.
         */
        public String requestBody() {
            return body;
        }

        /**
         * Sets the post data character set for x-www-form-urlencoded post data
         * @param charset character set to encode post data
         * @return this Request, for chaining
         */
        public Request postDataCharset(String charset) {
            Assert.notNull(charset, "Charset must not be null");
            if (!Charset.isSupported(charset))
                throw new IllegalCharsetNameException(charset);
            this.postDataCharset = charset;
            return this;
        }

        /**
         * Gets the post data character set for x-www-form-urlencoded post data
         * @return character set to encode post data
         */
        public String postDataCharset() {
            return postDataCharset;
        }
    }

    /**
     * Represents a HTTP response.
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
            Assert.notNull(req, "Request must not be null");
            String protocol = req.url().getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https"))
                throw new MalformedURLException("Only http & https protocols supported");
            final boolean methodHasBody = req.method().hasBody();
            final boolean hasRequestBody = req.requestBody() != null;
            if (!methodHasBody)
                Assert.isFalse(hasRequestBody, "Cannot set a request body for HTTP method " + req.method());

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

        /**
         * Get the status code of the response.
         * @return status code
         */
        public int statusCode() {
            return statusCode;
        }

        /**
         * Get the status message of the response.
         * @return status message
         */
        public String statusMessage() {
            return statusMessage;
        }

        /**
         * Get the character set name of the response.
         * @return character set name
         */
        public String charset() {
            return charset;
        }

        /**
         * Get the response content type (e.g. "text/html");
         * @return the response content type
         */
        public String contentType() {
            return contentType;
        }

        /**
         * Get the body of the response as a plain string.
         * @return body
         */
        public String body() {
            Assert.isTrue(executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
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

        /**
         * Get the body of the response as an array of bytes.
         * @return body bytes
         */
        public byte[] bodyAsBytes() {
            Assert.isTrue(executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
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
         * Instantiate Hostname Verifier that does nothing. This is used for connections with disabled SSL certificates validation.
         * @return Hostname Verifier that does nothing and accepts all hostnames
         */
        private static HostnameVerifier getInsecureVerifier() {
            return new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
        }

        /**
         * Initialise Trust manager that does not validate certificate chains and add it to current SSLContext.
         * <p/>
         * please not that this method will only perform action if sslSocketFactory is not yet instantiated.
         * @throws IOException
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
            final Collection<KeyVal> data = req.data();
            final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, req.postDataCharset()));

            if (bound != null) {
                // boundary will be set if we're in multipart mode
                for (KeyVal keyVal : data) {
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
                for (KeyVal keyVal : data) {
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
            for (KeyVal keyVal : req.data()) {
                Assert.isFalse(keyVal.hasInputStream(), "InputStream data not supported in URL query string.");
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
        for (KeyVal keyVal : req.data()) {
            if (keyVal.hasInputStream()) {
                needsMulti = true;
                break;
            }
        }
        return needsMulti;
    }

    /**
     * A Key Value tuple.
     */
    public static class KeyVal {
        private String key;
        private String value;
        private InputStream stream;

        public static KeyVal create(String key, String value) {
            return new KeyVal().key(key).value(value);
        }

        public static KeyVal create(String key, String filename, InputStream stream) {
            return new KeyVal().key(key).value(filename).inputStream(stream);
        }

        private KeyVal() {
        }

        /**
         * Update the key of a keyval
         * @param key new key
         * @return this KeyVal, for chaining
         */
        public KeyVal key(String key) {
            Assert.notEmpty(key, "Data key must not be empty");
            this.key = key;
            return this;
        }

        /**
         * Get the key of a keyval
         * @return the key
         */
        public String key() {
            return key;
        }

        /**
         * Update the value of a keyval
         * @param value the new value
         * @return this KeyVal, for chaining
         */
        public KeyVal value(String value) {
            Assert.notNull(value, "Data value must not be null");
            this.value = value;
            return this;
        }

        /**
         * Get the value of a keyval
         * @return the value
         */
        public String value() {
            return value;
        }

        /**
         * Add or update an input stream to this keyVal
         * @param inputStream new input stream
         * @return this KeyVal, for chaining
         */
        public KeyVal inputStream(InputStream inputStream) {
            Assert.notNull(value, "Data input stream must not be null");
            this.stream = inputStream;
            return this;
        }

        /**
         * Get the input stream associated with this keyval, if any
         * @return input stream if set, or null
         */
        public InputStream inputStream() {
            return stream;
        }

        /**
         * Does this keyval have an input stream?
         * @return true if this keyval does indeed have an input stream
         */
        public boolean hasInputStream() {
            return stream != null;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * GET and POST http methods.
     */
    public static enum Method {

        GET(false), POST(true), PUT(true), DELETE(false), PATCH(true), HEAD(false), OPTIONS(false), TRACE(false);

        private final boolean hasBody;

        Method(boolean hasBody) {
            this.hasBody = hasBody;
        }

        /**
         * Check if this HTTP method has/needs a request body
         * @return if body needed
         */
        public final boolean hasBody() {
            return hasBody;
        }
    }
}
