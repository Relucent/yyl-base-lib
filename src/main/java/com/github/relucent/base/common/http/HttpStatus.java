package com.github.relucent.base.common.http;

/**
 * HTTP状态代码的枚举<br>
 * 包括 RFC1945 (HTTP/1.0), RFC2616 (HTTP/1.1), RFC2518 (WebDAV) 已定义所有状态代码<br>
 * @see <a href="https://www.iana.org/assignments/http-status-codes">HTTP Status Code Registry</a>
 */
public enum HttpStatus {

    // --- 1xx Informational ---
    /**
     * {@code 100 Continue}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.2.1">HTTP/1.1: Semantics and Content, section 6.2.1</a>
     */
    CONTINUE(100, Series.INFORMATIONAL, "Continue"),
    /**
     * {@code 101 Switching Protocols}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.2.2">HTTP/1.1: Semantics and Content, section 6.2.2</a>
     */
    SWITCHING_PROTOCOLS(101, Series.INFORMATIONAL, "Switching Protocols"),
    /**
     * {@code 102 Processing}.
     * @see <a href="https://tools.ietf.org/html/rfc2518#section-10.1">WebDAV</a>
     */
    PROCESSING(102, Series.INFORMATIONAL, "Processing"),
    /**
     * {@code 103 Checkpoint}.
     * @see <a href="https://code.google.com/p/gears/wiki/ResumableHttpRequestsProposal">A proposal for supporting resumable POST/PUT HTTP requests in HTTP/1.0</a>
     */
    CHECKPOINT(103, Series.INFORMATIONAL, "Checkpoint"),

    // --- 2xx Success ---
    /**
     * {@code 200 OK}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.1">HTTP/1.1: Semantics and Content, section 6.3.1</a>
     */
    OK(200, Series.SUCCESSFUL, "OK"),
    /**
     * {@code 201 Created}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.2">HTTP/1.1: Semantics and Content, section 6.3.2</a>
     */
    CREATED(201, Series.SUCCESSFUL, "Created"),
    /**
     * {@code 202 Accepted}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.3">HTTP/1.1: Semantics and Content, section 6.3.3</a>
     */
    ACCEPTED(202, Series.SUCCESSFUL, "Accepted"),
    /**
     * {@code 203 Non-Authoritative Information}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.4">HTTP/1.1: Semantics and Content, section 6.3.4</a>
     */
    NON_AUTHORITATIVE_INFORMATION(203, Series.SUCCESSFUL, "Non-Authoritative Information"),
    /**
     * {@code 204 No Content}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.5">HTTP/1.1: Semantics and Content, section 6.3.5</a>
     */
    NO_CONTENT(204, Series.SUCCESSFUL, "No Content"),
    /**
     * {@code 205 Reset Content}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.6">HTTP/1.1: Semantics and Content, section 6.3.6</a>
     */
    RESET_CONTENT(205, Series.SUCCESSFUL, "Reset Content"),
    /**
     * {@code 206 Partial Content}.
     * @see <a href="https://tools.ietf.org/html/rfc7233#section-4.1">HTTP/1.1: Range Requests, section 4.1</a>
     */
    PARTIAL_CONTENT(206, Series.SUCCESSFUL, "Partial Content"),
    /**
     * {@code 207 Multi-Status}.
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-13">WebDAV</a>
     */
    MULTI_STATUS(207, Series.SUCCESSFUL, "Multi-Status"),
    /**
     * {@code 208 Already Reported}.
     * @see <a href="https://tools.ietf.org/html/rfc5842#section-7.1">WebDAV Binding Extensions</a>
     */
    ALREADY_REPORTED(208, Series.SUCCESSFUL, "Already Reported"),
    /**
     * {@code 226 IM Used}.
     * @see <a href="https://tools.ietf.org/html/rfc3229#section-10.4.1">Delta encoding in HTTP</a>
     */
    IM_USED(226, Series.SUCCESSFUL, "IM Used"),

    // --- 3xx Redirection ---
    /**
     * {@code 300 Multiple Choices}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.1">HTTP/1.1: Semantics and Content, section 6.4.1</a>
     */
    MULTIPLE_CHOICES(300, Series.REDIRECTION, "Multiple Choices"),
    /**
     * {@code 301 Moved Permanently}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.2">HTTP/1.1: Semantics and Content, section 6.4.2</a>
     */
    MOVED_PERMANENTLY(301, Series.REDIRECTION, "Moved Permanently"),
    /**
     * {@code 302 Found}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.3">HTTP/1.1: Semantics and Content, section 6.4.3</a>
     */
    FOUND(302, Series.REDIRECTION, "Found"),
    /**
     * {@code 303 See Other}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.4">HTTP/1.1: Semantics and Content, section 6.4.4</a>
     */
    SEE_OTHER(303, Series.REDIRECTION, "See Other"),
    /**
     * {@code 304 Not Modified}.
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-4.1">HTTP/1.1: Conditional Requests, section 4.1</a>
     */
    NOT_MODIFIED(304, Series.REDIRECTION, "Not Modified"),
    /**
     * {@code 305 Use Proxy}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.5">HTTP/1.1: Semantics and Content, section 6.4.5</a>
     * @deprecated due to security concerns regarding in-band configuration of a proxy
     */
    @Deprecated
    USE_PROXY(305, Series.REDIRECTION, "Use Proxy"),
    /**
     * {@code 307 Temporary Redirect}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.4.7">HTTP/1.1: Semantics and Content, section 6.4.7</a>
     */
    TEMPORARY_REDIRECT(307, Series.REDIRECTION, "Temporary Redirect"),
    /**
     * {@code 308 Permanent Redirect}.
     * @see <a href="https://tools.ietf.org/html/rfc7238">RFC 7238</a>
     */
    PERMANENT_REDIRECT(308, Series.REDIRECTION, "Permanent Redirect"),

    // --- 4xx Client Error ---

    /**
     * {@code 400 Bad Request}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.1">HTTP/1.1: Semantics and Content, section 6.5.1</a>
     */
    BAD_REQUEST(400, Series.CLIENT_ERROR, "Bad Request"),
    /**
     * {@code 401 Unauthorized}.
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-3.1">HTTP/1.1: Authentication, section 3.1</a>
     */
    UNAUTHORIZED(401, Series.CLIENT_ERROR, "Unauthorized"),
    /**
     * {@code 402 Payment Required}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.2">HTTP/1.1: Semantics and Content, section 6.5.2</a>
     */
    PAYMENT_REQUIRED(402, Series.CLIENT_ERROR, "Payment Required"),
    /**
     * {@code 403 Forbidden}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.3">HTTP/1.1: Semantics and Content, section 6.5.3</a>
     */
    FORBIDDEN(403, Series.CLIENT_ERROR, "Forbidden"),
    /**
     * {@code 404 Not Found}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.4">HTTP/1.1: Semantics and Content, section 6.5.4</a>
     */
    NOT_FOUND(404, Series.CLIENT_ERROR, "Not Found"),
    /**
     * {@code 405 Method Not Allowed}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.5">HTTP/1.1: Semantics and Content, section 6.5.5</a>
     */
    METHOD_NOT_ALLOWED(405, Series.CLIENT_ERROR, "Method Not Allowed"),
    /**
     * {@code 406 Not Acceptable}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.6">HTTP/1.1: Semantics and Content, section 6.5.6</a>
     */
    NOT_ACCEPTABLE(406, Series.CLIENT_ERROR, "Not Acceptable"),
    /**
     * {@code 407 Proxy Authentication Required}.
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-3.2">HTTP/1.1: Authentication, section 3.2</a>
     */
    PROXY_AUTHENTICATION_REQUIRED(407, Series.CLIENT_ERROR, "Proxy Authentication Required"),
    /**
     * {@code 408 Request Timeout}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.7">HTTP/1.1: Semantics and Content, section 6.5.7</a>
     */
    REQUEST_TIMEOUT(408, Series.CLIENT_ERROR, "Request Timeout"),
    /**
     * {@code 409 Conflict}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.8">HTTP/1.1: Semantics and Content, section 6.5.8</a>
     */
    CONFLICT(409, Series.CLIENT_ERROR, "Conflict"),
    /**
     * {@code 410 Gone}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.9"> HTTP/1.1: Semantics and Content, section 6.5.9</a>
     */
    GONE(410, Series.CLIENT_ERROR, "Gone"),
    /**
     * {@code 411 Length Required}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.10"> HTTP/1.1: Semantics and Content, section 6.5.10</a>
     */
    LENGTH_REQUIRED(411, Series.CLIENT_ERROR, "Length Required"),
    /**
     * {@code 412 Precondition failed}.
     * @see <a href="https://tools.ietf.org/html/rfc7232#section-4.2"> HTTP/1.1: Conditional Requests, section 4.2</a>
     */
    PRECONDITION_FAILED(412, Series.CLIENT_ERROR, "Precondition Failed"),
    /**
     * {@code 413 Payload Too Large}.
     * @since 4.1
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.11"> HTTP/1.1: Semantics and Content, section 6.5.11</a>
     */
    PAYLOAD_TOO_LARGE(413, Series.CLIENT_ERROR, "Payload Too Large"),
    /**
     * {@code 414 URI Too Long}.
     * @since 4.1
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.12"> HTTP/1.1: Semantics and Content, section 6.5.12</a>
     */
    URI_TOO_LONG(414, Series.CLIENT_ERROR, "URI Too Long"),
    /**
     * {@code 415 Unsupported Media Type}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.13"> HTTP/1.1: Semantics and Content, section 6.5.13</a>
     */
    UNSUPPORTED_MEDIA_TYPE(415, Series.CLIENT_ERROR, "Unsupported Media Type"),
    /**
     * {@code 416 Requested Range Not Satisfiable}.
     * @see <a href="https://tools.ietf.org/html/rfc7233#section-4.4">HTTP/1.1: Range Requests, section 4.4</a>
     */
    REQUESTED_RANGE_NOT_SATISFIABLE(416, Series.CLIENT_ERROR, "Requested range not satisfiable"),
    /**
     * {@code 417 Expectation Failed}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.14"> HTTP/1.1: Semantics and Content, section 6.5.14</a>
     */
    EXPECTATION_FAILED(417, Series.CLIENT_ERROR, "Expectation Failed"),
    /**
     * {@code 418 I'm a teapot}.
     * @see <a href="https://tools.ietf.org/html/rfc2324#section-2.3.2">HTCPCP/1.0</a>
     */
    I_AM_A_TEAPOT(418, Series.CLIENT_ERROR, "I'm a teapot"),
    /**
     * @deprecated See <a href="https://tools.ietf.org/rfcdiff?difftype=--hwdiff&amp;url2=draft-ietf-webdav-protocol-06.txt"> WebDAV Draft Changes</a>
     */
    @Deprecated
    INSUFFICIENT_SPACE_ON_RESOURCE(419, Series.CLIENT_ERROR, "Insufficient Space On Resource"),
    /**
     * @deprecated See <a href="https://tools.ietf.org/rfcdiff?difftype=--hwdiff&amp;url2=draft-ietf-webdav-protocol-06.txt"> WebDAV Draft Changes</a>
     */
    @Deprecated
    METHOD_FAILURE(420, Series.CLIENT_ERROR, "Method Failure"),
    /**
     * @deprecated See <a href="https://tools.ietf.org/rfcdiff?difftype=--hwdiff&amp;url2=draft-ietf-webdav-protocol-06.txt"> WebDAV Draft Changes</a>
     */
    @Deprecated
    DESTINATION_LOCKED(421, Series.CLIENT_ERROR, "Destination Locked"),
    /**
     * {@code 422 Unprocessable Entity}.
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.2">WebDAV</a>
     */
    UNPROCESSABLE_ENTITY(422, Series.CLIENT_ERROR, "Unprocessable Entity"),
    /**
     * {@code 423 Locked}.
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.3">WebDAV</a>
     */
    LOCKED(423, Series.CLIENT_ERROR, "Locked"),
    /**
     * {@code 424 Failed Dependency}.
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.4">WebDAV</a>
     */
    FAILED_DEPENDENCY(424, Series.CLIENT_ERROR, "Failed Dependency"),
    /**
     * {@code 425 Too Early}.
     * @since 5.2
     * @see <a href="https://tools.ietf.org/html/rfc8470">RFC 8470</a>
     */
    TOO_EARLY(425, Series.CLIENT_ERROR, "Too Early"),
    /**
     * {@code 426 Upgrade Required}.
     * @see <a href="https://tools.ietf.org/html/rfc2817#section-6">Upgrading to TLS Within HTTP/1.1</a>
     */
    UPGRADE_REQUIRED(426, Series.CLIENT_ERROR, "Upgrade Required"),
    /**
     * {@code 428 Precondition Required}.
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-3">Additional HTTP Status Codes</a>
     */
    PRECONDITION_REQUIRED(428, Series.CLIENT_ERROR, "Precondition Required"),
    /**
     * {@code 429 Too Many Requests}.
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-4">Additional HTTP Status Codes</a>
     */
    TOO_MANY_REQUESTS(429, Series.CLIENT_ERROR, "Too Many Requests"),
    /**
     * {@code 431 Request Header Fields Too Large}.
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-5">Additional HTTP Status Codes</a>
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, Series.CLIENT_ERROR, "Request Header Fields Too Large"),
    /**
     * {@code 451 Unavailable For Legal Reasons}.
     * @see <a href="https://tools.ietf.org/html/draft-ietf-httpbis-legally-restricted-status-04"> An HTTP Status Code to Report Legal Obstacles</a>
     * @since 4.3
     */
    UNAVAILABLE_FOR_LEGAL_REASONS(451, Series.CLIENT_ERROR, "Unavailable For Legal Reasons"),

    // --- 5xx Server Error ---

    /**
     * {@code 500 Internal Server Error}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.1">HTTP/1.1: Semantics and Content, section 6.6.1</a>
     */
    INTERNAL_SERVER_ERROR(500, Series.SERVER_ERROR, "Internal Server Error"),
    /**
     * {@code 501 Not Implemented}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.2">HTTP/1.1: Semantics and Content, section 6.6.2</a>
     */
    NOT_IMPLEMENTED(501, Series.SERVER_ERROR, "Not Implemented"),
    /**
     * {@code 502 Bad Gateway}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.3">HTTP/1.1: Semantics and Content, section 6.6.3</a>
     */
    BAD_GATEWAY(502, Series.SERVER_ERROR, "Bad Gateway"),
    /**
     * {@code 503 Service Unavailable}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.4">HTTP/1.1: Semantics and Content, section 6.6.4</a>
     */
    SERVICE_UNAVAILABLE(503, Series.SERVER_ERROR, "Service Unavailable"),
    /**
     * {@code 504 Gateway Timeout}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.5">HTTP/1.1: Semantics and Content, section 6.6.5</a>
     */
    GATEWAY_TIMEOUT(504, Series.SERVER_ERROR, "Gateway Timeout"),
    /**
     * {@code 505 HTTP Version Not Supported}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.6">HTTP/1.1: Semantics and Content, section 6.6.6</a>
     */
    HTTP_VERSION_NOT_SUPPORTED(505, Series.SERVER_ERROR, "HTTP Version not supported"),
    /**
     * {@code 506 Variant Also Negotiates}
     * @see <a href="https://tools.ietf.org/html/rfc2295#section-8.1">Transparent Content Negotiation</a>
     */
    VARIANT_ALSO_NEGOTIATES(506, Series.SERVER_ERROR, "Variant Also Negotiates"),
    /**
     * {@code 507 Insufficient Storage}
     * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.5">WebDAV</a>
     */
    INSUFFICIENT_STORAGE(507, Series.SERVER_ERROR, "Insufficient Storage"),
    /**
     * {@code 508 Loop Detected}
     * @see <a href="https://tools.ietf.org/html/rfc5842#section-7.2">WebDAV Binding Extensions</a>
     */
    LOOP_DETECTED(508, Series.SERVER_ERROR, "Loop Detected"),
    /**
     * {@code 509 Bandwidth Limit Exceeded}
     */
    BANDWIDTH_LIMIT_EXCEEDED(509, Series.SERVER_ERROR, "Bandwidth Limit Exceeded"),
    /**
     * {@code 510 Not Extended}
     * @see <a href="https://tools.ietf.org/html/rfc2774#section-7">HTTP Extension Framework</a>
     */
    NOT_EXTENDED(510, Series.SERVER_ERROR, "Not Extended"),
    /**
     * {@code 511 Network Authentication Required}.
     * @see <a href="https://tools.ietf.org/html/rfc6585#section-6">Additional HTTP Status Codes</a>
     */
    NETWORK_AUTHENTICATION_REQUIRED(511, Series.SERVER_ERROR, "Network Authentication Required");

    // ==============================Fields===========================================
    private final int value;
    private final Series series;
    private final String reasonPhrase;

    // ==============================Constructors=====================================
    HttpStatus(int value, Series series, String reasonPhrase) {
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    // ==============================StaticMethods====================================
    /** 缓存的VALUES */
    private static final HttpStatus[] CACHED_VALUES = values();

    /**
     * 将给定的状态代码解析为{@code HttpStatus}，如果不匹配则返回{@code null}
     * @param statusCode HTTP状态代码
     * @return 对应的{@code HttpStatus}, 如果不匹配则返回则返回 {@code null}
     */
    public static HttpStatus resolve(int statusCode) {
        for (HttpStatus status : CACHED_VALUES) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    // ==============================Methods==========================================
    /**
     * 返回此状态代码的整数值
     * @return 状态代码的整数值
     */
    public int value() {
        return this.value;
    }

    /**
     * 返回此状态代码对应的系列
     * @see HttpStatus.Series
     * @return 状态代码对应的系列
     */
    public Series series() {
        return this.series;
    }

    /**
     * 返回此状态代码的原因短语
     * @return 状态代码对应的原因短语
     */
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    /**
     * 此状态代码是否是 1XX系列，这一类状态码表示服务器收到请求，需要请求者继续执行操作
     * @return 状态码是1XX，则返回{@code true}，否则返回{@code false}
     */
    public boolean is1xxInformational() {
        return (series() == Series.INFORMATIONAL);
    }

    /**
     * 此状态代码是否是 2XX系列，这一类状态码表示操作被成功接收并处理
     * @return 状态码是2XX，则返回{@code true}，否则返回{@code false}
     */
    public boolean is2xxSuccessful() {
        return (series() == Series.SUCCESSFUL);
    }

    /**
     * 此状态代码是否是 3XX系列，这一类状态码表示重定向
     * @return 状态码是3XX，则返回{@code true}，否则返回{@code false}
     */
    public boolean is3xxRedirection() {
        return (series() == Series.REDIRECTION);
    }

    /**
     * 此状态代码是否是 4XX系列，这一类状态码表示客户端错误，请求包含语法错误或无法完成请求
     * @return 状态码是4XX，则返回{@code true}，否则返回{@code false}
     */
    public boolean is4xxClientError() {
        return (series() == Series.CLIENT_ERROR);
    }

    /**
     * 此状态代码是否是 5XX系列，这一类状态码表示服务器在处理请求的过程中发生了错误
     * @return 状态码是5XX，则返回{@code true}，否则返回{@code false}
     */
    public boolean is5xxServerError() {
        return (series() == Series.SERVER_ERROR);
    }

    /**
     * 此状态代码是否是表示异常
     * @see #is4xxClientError()
     * @see #is5xxServerError()
     * @return 状态码是4XX或者5XXX，则返回{@code true}，否则返回{@code false}
     */
    public boolean isError() {
        return (is4xxClientError() || is5xxServerError());
    }

    // ==============================Override=========================================
    @Override
    public String toString() {
        return this.value + " " + name();
    }

    // ==============================InnerClass=======================================
    /**
     * HTTP状态系列
     */
    public enum Series {

        /** 信息，服务器收到请求，需要请求者继续执行操作 */
        INFORMATIONAL(1),
        /** 成功，操作被成功接收并处理 */
        SUCCESSFUL(2),
        /** 重定向，需要进一步的操作以完成请求 */
        REDIRECTION(3),
        /** 客户端错误，请求包含语法错误或无法完成请求 */
        CLIENT_ERROR(4),
        /** 服务器错误，服务器在处理请求的过程中发生了错误 */
        SERVER_ERROR(5);

        private final int value;

        Series(int value) {
            this.value = value;
        }

        /**
         * 返回此状态序列的整数值，范围从1到5
         * @return 状态序列的整数值
         */
        public int value() {
            return this.value;
        }

        /**
         * 如果可能的话，将给定的状态代码解析为{code HttpStatus.Series}
         * @param statusCode HTTP状态代码
         * @return 对应的 {@code Series}，如果不能匹配则返回{@code null}
         */
        public static Series resolve(int statusCode) {
            int seriesCode = statusCode / 100;
            for (Series series : values()) {
                if (series.value == seriesCode) {
                    return series;
                }
            }
            return null;
        }
    }
}
