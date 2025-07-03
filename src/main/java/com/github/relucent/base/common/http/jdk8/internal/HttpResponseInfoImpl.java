package com.github.relucent.base.common.http.jdk8.internal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.relucent.base.common.http.jdk8.HttpHeaders;
import com.github.relucent.base.common.http.jdk8.HttpResponse.ResponseInfo;

public class HttpResponseInfoImpl implements ResponseInfo {

    private final int statusCode;
    private final HttpHeaders headers;

    public HttpResponseInfoImpl(HttpURLConnection conn) throws IOException {
        this.statusCode = conn.getResponseCode();
        // HttpURLConnection.getHeaderFields() 返回 Map<String, List<String>>，但 key 可能为 null（表示状态行）
        Map<String, List<String>> headersMap = new LinkedHashMap<>();
        Map<String, List<String>> rawHeaders = conn.getHeaderFields();
        if (rawHeaders != null) {
            for (Map.Entry<String, List<String>> entry : rawHeaders.entrySet()) {
                String key = entry.getKey();
                if (key != null) {
                    headersMap.put(key, entry.getValue());
                }
            }
        }
        this.headers = HttpHeaders.of(headersMap);
    }

    public int statusCode() {
        return statusCode;
    }

    public HttpHeaders headers() {
        return headers;
    }
}
