package com.github.relucent.base.common.http.jdk8.internal;

import java.net.URI;
import java.util.Map;

import com.github.relucent.base.common.http.jdk8.HttpRequest;
import com.github.relucent.base.common.http.jdk8.HttpRequest.BodyPublisher;
import com.github.relucent.base.common.http.jdk8.HttpRequest.BodyPublishers;

public class HttpRequestBuilderImpl implements HttpRequest.Builder {

    URI uri;
    String method = "GET";
    BodyPublisher bodyPublisher;
    HttpHeadersBuilderImpl headers = new HttpHeadersBuilderImpl();

    public HttpRequestBuilderImpl uri(URI uri) {
        this.uri = uri;
        return this;
    }

    public HttpRequestBuilderImpl uri(String uri) {
        return uri(URI.create(uri));
    }

    public HttpRequestBuilderImpl method(String method, BodyPublisher bodyPublisher) {
        this.method = method;
        this.bodyPublisher = bodyPublisher;
        return this;
    }

    public HttpRequestBuilderImpl GET() {
        return method("GET", BodyPublishers.noBody());
    }

    public HttpRequestBuilderImpl POST(BodyPublisher bodyPublisher) {
        return method("POST", bodyPublisher);
    }

    public HttpRequestBuilderImpl PUT(BodyPublisher bodyPublisher) {
        return method("PUT", bodyPublisher);
    }

    public HttpRequestBuilderImpl DELETE() {
        this.method = "DELETE";
        return this;
    }

    public HttpRequestBuilderImpl header(String name, String value) {
        headers.addHeader(name, value);
        return this;
    }

    public HttpRequestBuilderImpl setHeader(String name, String value) {
        headers.setHeader(name, value);
        return this;
    }

    public HttpRequestBuilderImpl headers(Map<String, String> headers) {
        for (Map.Entry<String, String> e : headers.entrySet()) {
            header(e.getKey(), e.getValue());
        }
        return this;
    }

    public HttpRequest build() {
        return new HttpRequestImpl(this);
    }
}
