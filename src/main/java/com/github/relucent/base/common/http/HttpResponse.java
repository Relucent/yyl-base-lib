package com.github.relucent.base.common.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.lang.AssertUtil;

/**
 * 表示HTTP响应
 */
public class HttpResponse extends HttpBase<HttpResponse> {

    public static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    private static final int HTTP_TEMP_REDIRECT_STATUS = 307;

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

    HttpResponse() {
        super();
    }

    private HttpResponse(HttpResponse previousResponse) throws IOException {
        super();
        if (previousResponse != null) {
            numRedirects = previousResponse.numRedirects + 1;
            if (numRedirects >= MAX_REDIRECTS)
                throw new IOException(String.format("Too many redirects occurred trying to load URL %s", previousResponse.getUrl()));
        }
    }

    static HttpResponse execute(HttpRequest req) throws IOException {
        return execute(req, null);
    }

    static HttpResponse execute(HttpRequest req, HttpResponse previousResponse) throws IOException {
        AssertUtil.notNull(req, "Request must not be null");
        String protocol = req.getUrl().getProtocol();
        if (!protocol.equals("http") && !protocol.equals("https"))
            throw new MalformedURLException("Only http & https protocols supported");
        final boolean methodHasBody = req.getMethod().hasBody();
        final boolean hasRequestBody = req.getRequestBody() != null;
        if (!methodHasBody)
            AssertUtil.isFalse(hasRequestBody, "Cannot set a request body for HTTP method " + req.getMethod());

        // set up the request for execution
        String mimeBoundary = null;
        if (req.getData().size() > 0 && (!methodHasBody || hasRequestBody))
            serialiseRequestUrl(req);
        else if (methodHasBody)
            mimeBoundary = setOutputContentType(req);

        HttpURLConnection conn = createConnection(req);
        HttpResponse res;
        try {
            conn.connect();
            if (conn.getDoOutput())
                writePost(req, conn.getOutputStream(), mimeBoundary);

            int status = conn.getResponseCode();
            res = new HttpResponse(previousResponse);
            res.setupFromConnection(conn, previousResponse);

            // redirect if there's a location header (from 3xx, or 201 etc)
            if (res.hasHeader(LOCATION) && req.isFollowRedirects()) {
                if (status != HTTP_TEMP_REDIRECT_STATUS) {
                    req.setMethod(HttpMethod.GET); // always redirect with a get. any data param from
                    // original req are dropped.
                    req.getData().clear();
                }

                String location = res.getHeader(LOCATION);
                if (location != null && location.startsWith("http:/") && location.charAt(6) != '/') // fix
                                                                                                    // broken
                                                                                                    // Location:
                                                                                                    // http:/temp/AAG_New/en/index.php
                    location = location.substring(6);
                req.setUrl(resolve(req.getUrl(), DataUtil.encodeUrl(location)));

                for (Map.Entry<String, String> cookie : res.cookies.entrySet()) { // add
                                                                                  // response
                                                                                  // cookies to
                                                                                  // request
                                                                                  // (for e.g.
                                                                                  // login
                                                                                  // posts)
                    req.setCookie(cookie.getKey(), cookie.getValue());
                }
                return execute(req, res);
            }
            if ((status < 200 || status >= 400) && !req.isIgnoreHttpErrors())
                throw new IOException("HTTP error fetching URL " + status + " " + req.getUrl().toString());

            // check that we can handle the returned content type; if not, abort before fetching
            // it
            String contentType = res.getContentType();
            if (contentType != null && !req.isIgnoreContentType() && !contentType.startsWith("text/")
                    && !xmlContentTypeRxp.matcher(contentType).matches())
                throw new IOException("Unhandled content type[" + contentType + "]. Must be text/*, application/xml, or application/xhtml+xml "
                        + req.getUrl().toString());

            res.charset = DataUtil.getCharsetFromContentType(res.contentType); // may be null,
                                                                               // readInputStream
                                                                               // deals with it
            if (conn.getContentLength() != 0 && req.getMethod() != HttpMethod.HEAD) { // -1 means
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

                    res.byteData = DataUtil.readToByteBuffer(bodyStream, req.getMaxBodySize());
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

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getCharset() {
        return charset;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBodyAsString() {
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

    public byte[] getBodyAsBytes() {
        AssertUtil.isTrue(executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
        return byteData.array();
    }

    // set up connection defaults, and details from request
    private static HttpURLConnection createConnection(HttpRequest req) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) (req.getProxy() == null ? req.getUrl().openConnection()
                : req.getUrl().openConnection(req.getProxy()));

        conn.setRequestMethod(req.getMethod().name());
        conn.setInstanceFollowRedirects(false); // don't rely on native redirection support
        conn.setConnectTimeout(req.getConnectTimeoutMillis());
        conn.setReadTimeout(req.getReadTimeoutMillis());

        if (conn instanceof HttpsURLConnection) {
            if (!req.isValidateTLSCertificates()) {
                initUnSecureTSL();
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
                ((HttpsURLConnection) conn).setHostnameVerifier(getInsecureVerifier());
            }
        }

        if (req.getMethod().hasBody())
            conn.setDoOutput(true);
        if (req.cookies().size() > 0)
            conn.addRequestProperty("Cookie", getRequestCookieString(req));
        for (Map.Entry<String, String> header : req.getHeaders().entrySet()) {
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
    private void setupFromConnection(HttpURLConnection conn, HttpResponse previousResponse) throws IOException {
        method = HttpMethod.valueOf(conn.getRequestMethod());
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
                    setCookie(prevCookie.getKey(), prevCookie.getValue());
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
                        setCookie(cookieName, cookieValue);
                    }
                }
            } else {
                // combine same header names with comma:
                // http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
                if (values.size() == 1) {
                    setHeader(name, values.get(0));
                } else if (values.size() > 1) {
                    StringBuilder accum = new StringBuilder();
                    for (int i = 0; i < values.size(); i++) {
                        final String val = values.get(i);
                        if (i != 0) {
                            accum.append(", ");
                        }
                        accum.append(val);
                    }
                    setHeader(name, accum.toString());
                }
            }
        }
    }

    private static String setOutputContentType(final HttpRequest req) {
        String bound = null;
        if (DataUtil.needsMultipart(req)) {
            bound = DataUtil.mimeBoundary();
            req.setHeader(CONTENT_TYPE, MULTIPART_FORM_DATA + "; boundary=" + bound);
        } else {
            req.setHeader(CONTENT_TYPE, FORM_URL_ENCODED + "; charset=" + req.getPostDataCharset());
        }
        return bound;
    }

    private static void writePost(final HttpRequest req, final OutputStream outputStream, final String bound) throws IOException {
        final Collection<KeyValue> data = req.getData();
        final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(outputStream, req.getPostDataCharset()));

        if (bound != null) {
            // boundary will be set if we're in multipart mode
            for (KeyValue keyVal : data) {
                w.write("--");
                w.write(bound);
                w.write("\r\n");
                w.write("Content-Disposition: form-data; name=\"");
                w.write(DataUtil.encodeMimeName(keyVal.geyKey())); // encodes " to %22
                w.write("\"");
                if (keyVal.hasInputStream()) {
                    w.write("; filename=\"");
                    w.write(DataUtil.encodeMimeName(keyVal.setValue()));
                    w.write("\"\r\nContent-Type: application/octet-stream\r\n\r\n");
                    w.flush(); // flush
                    IoUtil.copy(keyVal.getInputStream(), outputStream);
                    outputStream.flush();
                } else {
                    w.write("\r\n\r\n");
                    w.write(keyVal.setValue());
                }
                w.write("\r\n");
            }
            w.write("--");
            w.write(bound);
            w.write("--");
        } else if (req.getRequestBody() != null) {
            // data will be in query string, we're sending a plaintext body
            w.write(req.getRequestBody());
        } else {
            // regular form data (application/x-www-form-urlencoded)
            boolean first = true;
            for (KeyValue keyVal : data) {
                if (!first)
                    w.append('&');
                else
                    first = false;

                w.write(URLEncoder.encode(keyVal.geyKey(), req.getPostDataCharset()));
                w.write('=');
                w.write(URLEncoder.encode(keyVal.setValue(), req.getPostDataCharset()));
            }
        }
        w.close();
    }

    private static String getRequestCookieString(HttpRequest req) {
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
    private static void serialiseRequestUrl(HttpRequest req) throws IOException {
        URL in = req.getUrl();
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
        for (KeyValue keyVal : req.getData()) {
            AssertUtil.isFalse(keyVal.hasInputStream(), "InputStream data not supported in URL query string.");
            if (!first)
                url.append('&');
            else
                first = false;
            url.append(URLEncoder.encode(keyVal.geyKey(), DataUtil.DEFAULT_CHARSET)).append('=')
                    .append(URLEncoder.encode(keyVal.setValue(), DataUtil.DEFAULT_CHARSET));
        }
        req.setUrl(new URL(url.toString()));
        req.getData().clear(); // moved into url as get params
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
