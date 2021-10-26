package com.github.relucent.base.common.http;

import java.net.URL;

/**
 * HTTP 工具类
 */
public class Http {

    /**
     * 获得连接
     * @param url URL地址
     * @return 连接对象
     */
    public static Connection connect(String url) {
        return Connection.connect(url);
    }

    /**
     * 获得连接
     * @param url URL地址
     * @return 连接对象
     */
    public static Connection connect(URL url) {
        return Connection.connect(url);
    }
}
