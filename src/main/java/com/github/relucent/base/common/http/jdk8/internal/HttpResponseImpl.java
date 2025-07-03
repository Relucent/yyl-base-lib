package com.github.relucent.base.common.http.jdk8.internal;

import com.github.relucent.base.common.http.jdk8.HttpHeaders;
import com.github.relucent.base.common.http.jdk8.HttpResponse;

/**
 * HTTP 响应类
 * @param <T> 响应体数据类型
 */
public class HttpResponseImpl<T> implements HttpResponse<T> {

    private final int statusCode;
    private final HttpHeaders headers;
    private final T body;

    public HttpResponseImpl(int statusCode, HttpHeaders headers, T body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public int statusCode() {
        return statusCode;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public T body() {
        return body;
    }

}
