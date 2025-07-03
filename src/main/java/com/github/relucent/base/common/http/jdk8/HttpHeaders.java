package com.github.relucent.base.common.http.jdk8;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.github.relucent.base.common.collection.CollectionUtil;
import com.github.relucent.base.common.convert.ConvertUtil;

public class HttpHeaders {

    private static final HttpHeaders NO_HEADERS = new HttpHeaders(Collections.emptyMap());
    private final Map<String, List<String>> headers;

    public static HttpHeaders of(Map<String, List<String>> headerMap) {
        requireNonNull(headerMap);
        return headersOf(headerMap);
    }

    private HttpHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String firstValue(String name) {
        return CollectionUtil.getFirst(allValues(name));
    }

    public Long firstValueAsLong(String name) {
        return ConvertUtil.toLong(firstValue(name));
    }

    public List<String> allValues(String name) {
        requireNonNull(name);
        List<String> values = headers.get(name);
        return values != null ? values : Collections.emptyList();
    }

    public Map<String, List<String>> map() {
        return headers;
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof HttpHeaders)) {
            return false;
        }
        HttpHeaders that = (HttpHeaders) obj;
        return headers.equals(that.map());
    }

    @Override
    public final int hashCode() {
        int hash = 0;
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            int keyHash = key.toLowerCase(Locale.ROOT).hashCode();
            int valueHash = value.hashCode();
            hash += keyHash ^ valueHash;
        }
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(" { ");
        sb.append(map());
        sb.append(" }");
        return sb.toString();
    }

    public static HttpHeaders headersOf(Map<String, List<String>> map) {
        TreeMap<String, List<String>> other = new TreeMap<>(CASE_INSENSITIVE_ORDER);
        TreeSet<String> notAdded = new TreeSet<>(CASE_INSENSITIVE_ORDER);
        map.forEach((key, value) -> {
            ArrayList<String> headerValues = new ArrayList<>();
            String headerName = requireNonNull(key).trim();
            if (headerName.isEmpty()) {
                throw new IllegalArgumentException("empty key");
            }
            requireNonNull(value).forEach(headerValue -> headerValues.add(requireNonNull(headerValue).trim()));
            if (headerValues.isEmpty()) {
                if (other.containsKey(headerName) || notAdded.contains(headerName.toLowerCase(Locale.ROOT))) {
                    throw new IllegalArgumentException("duplicate key: " + headerName);
                }
                notAdded.add(headerName.toLowerCase(Locale.ROOT));
            } else {
                if (other.put(headerName, headerValues) != null) {
                    throw new IllegalArgumentException("duplicate key: " + headerName);
                }
            }
        });
        return other.isEmpty() ? NO_HEADERS : new HttpHeaders(unmodifiableMap(other));
    }
}
