package com.github.relucent.base.common.http.jdk8.internal;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import com.github.relucent.base.common.http.HttpMethod;
import com.github.relucent.base.common.http.jdk8.HttpRequest;
import com.github.relucent.base.common.http.jdk8.HttpRequest.BodyPublisher;
import com.github.relucent.base.common.http.jdk8.HttpRequest.BodyPublishers;

public class HttpRequestBuilderImpl implements HttpRequest.Builder {

    URI uri;
    HttpMethod method = HttpMethod.GET;
    BodyPublisher bodyPublisher;
    HttpHeadersBuilderImpl headers = new HttpHeadersBuilderImpl();

    @Override
    public HttpRequestBuilderImpl uri(URI uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public HttpRequestBuilderImpl uri(String uri) {
        return uri(URI.create(uri));
    }

    @Override
    public HttpRequestBuilderImpl method(HttpMethod method, BodyPublisher bodyPublisher) {
        this.method = Objects.requireNonNull(method, "method must not be null");
        this.bodyPublisher = bodyPublisher == null ? BodyPublishers.noBody() : bodyPublisher;
        return this;
    }

    @Override
    public HttpRequestBuilderImpl GET() {
        return method(HttpMethod.GET, BodyPublishers.noBody());
    }

    @Override
    public HttpRequestBuilderImpl POST(BodyPublisher bodyPublisher) {
        return method(HttpMethod.POST, bodyPublisher);
    }

    @Override
    public HttpRequestBuilderImpl PUT(BodyPublisher bodyPublisher) {
        return method(HttpMethod.PUT, bodyPublisher);
    }

    @Override
    public HttpRequestBuilderImpl DELETE() {
        return method(HttpMethod.DELETE, BodyPublishers.noBody());
    }

    @Override
    public HttpRequestBuilderImpl header(String name, String value) {
        headers.addHeader(name, value);
        return this;
    }

    @Override
    public HttpRequestBuilderImpl setHeader(String name, String value) {
        headers.setHeader(name, value);
        return this;
    }

    @Override
    public HttpRequestBuilderImpl headers(Map<String, String> headers) {
        for (Map.Entry<String, String> e : headers.entrySet()) {
            header(e.getKey(), e.getValue());
        }
        return this;
    }

    @Override
    public HttpRequest build() {
        return new HttpRequestImpl(this);
    }
}
