package com.github.relucent.base.common.web;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.relucent.base.common.collection.CollectionUtil;

/**
 * Cookie 工具类，提供一些解析的方法
 */
public class CookieUtil {

    /**
     * 解析【请求头 Cookie】<br>
     * 格式：name=ABC; age=18<br>
     * @param cookieHeader 请求头 Cookie 字符串
     * @return Cookie 映射表
     */
    public static Map<String, String> parseRequestCookie(String cookieHeader) {
        Map<String, String> map = new LinkedHashMap<>();
        if (cookieHeader == null || cookieHeader.trim().isEmpty()) {
            return map;
        }
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            cookie = cookie.trim();
            int idx = cookie.indexOf('=');
            if (idx > 0 && idx < cookie.length() - 1) {
                String key = cookie.substring(0, idx).trim();
                String value = cookie.substring(idx + 1).trim();
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 解析【响应头 Set-Cookie】 支持标准 HttpCookie 格式
     * @param setCookieHeaders Set-Cookie 字符串集合
     * @return List<HttpCookie>
     */
    public static List<HttpCookie> parseResponseCookie(Collection<String> setCookieHeaders) {
        List<HttpCookie> list = new ArrayList<>();
        if (CollectionUtil.isEmpty(setCookieHeaders)) {
            return list;
        }
        for (String header : setCookieHeaders) {
            header = header.trim();
            if (!header.isEmpty()) {
                list.addAll(HttpCookie.parse(header));
            }
        }
        return list;
    }
}