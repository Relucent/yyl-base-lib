package com.github.relucent.base.common.web;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP方法枚举
 */
public enum HttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, HttpMethod> MAPPINGS = new HashMap<String, HttpMethod>(8);

    static {
        for (HttpMethod httpMethod : values()) {
            MAPPINGS.put(httpMethod.name(), httpMethod);
        }
    }

    /**
     * 将方法名称解析为 {@code HttpMethod}.
     * @param method 方法名称
     * @return 对应的 {@code HttpMethod}, 如果没找到返回{@code null}
     */
    public static HttpMethod resolve(String method) {
        return (method != null ? MAPPINGS.get(method) : null);
    }

    /**
     * 确定此{@code HttpMethod}是否与给定的方法名称匹配。
     * @param method 方法名称
     * @return 如果匹配返回true，否则返回false
     */
    public boolean matches(String method) {
        return (this == resolve(method));
    }
}
