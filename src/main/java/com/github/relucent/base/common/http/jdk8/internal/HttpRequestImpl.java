package com.github.relucent.base.common.http.jdk8.internal;

import java.net.URI;

import com.github.relucent.base.common.http.jdk8.HttpHeaders;
import com.github.relucent.base.common.http.jdk8.HttpRequest;

/**
 * HTTP 请求类
 */
public class HttpRequestImpl implements HttpRequest {

    private final URI uri;
    private final String method;
    private final HttpHeaders headers;
    private BodyPublisher bodyPublisher;

    HttpRequestImpl(HttpRequestBuilderImpl builder) {
        this.uri = builder.uri;
        this.method = builder.method;
        this.headers = builder.headers.build();
        this.bodyPublisher = builder.bodyPublisher;
    }

    public URI uri() {
        return uri;
    }

    public String method() {
        return method;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public BodyPublisher bodyPublisher() {
        return bodyPublisher;
    }

}