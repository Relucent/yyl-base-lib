package com.github.relucent.base.common.http.jdk8.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.relucent.base.common.http.jdk8.HttpHeaders;

public class HttpHeadersBuilderImpl {

    private final Map<String, List<String>> headers;

    public HttpHeadersBuilderImpl() {
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public HttpHeadersBuilderImpl addHeader(String name, String value) {
        headers.computeIfAbsent(name, k -> new ArrayList<>(1)).add(value);
        return this;
    }

    public HttpHeadersBuilderImpl setHeader(String name, String value) {
        List<String> values = new ArrayList<>(1);
        values.add(value);
        headers.put(name, values);
        return this;
    }

    public HttpHeadersBuilderImpl remove(String name) {
        headers.remove(name);
        return this;
    }

    public void clear() {
        headers.clear();
    }

    public Map<String, List<String>> map() {
        return headers;
    }

    public HttpHeaders build() {
        return HttpHeaders.headersOf(headers);
    }
}
