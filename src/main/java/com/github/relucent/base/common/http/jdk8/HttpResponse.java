package com.github.relucent.base.common.http.jdk8;

import java.io.IOException;
import java.io.InputStream;

import com.github.relucent.base.common.http.jdk8.internal.HttpResponseHandlers;
import com.github.relucent.base.common.http.jdk8.internal.HttpResponseImpl;

/**
 * HTTP 响应类
 * @param <T> 响应体数据类型
 */
public interface HttpResponse<T> {

    int statusCode();

    HttpHeaders headers();

    T body();

    /**
     * 构造一个 HTTP 响应实例。
     * @param statusCode 响应状态码
     * @param headers    响应头
     * @param body       响应体
     * @param <T>        响应体类型
     * @return HttpResponse 实例
     */
    static <T> HttpResponse<T> of(int statusCode, HttpHeaders headers, T body) {
        return new HttpResponseImpl<>(statusCode, headers, body);
    }

    interface ResponseInfo {

        int statusCode();

        HttpHeaders headers();
    }

    interface BodySubscriber<T> {
        T getBody(InputStream is) throws IOException;
    }

    /**
     * 响应体处理接口
     * @param <T> 响应体数据类型
     */
    interface BodyHandler<T> {
        public BodySubscriber<T> apply(ResponseInfo responseInfo);
    }

    /**
     * 响应体处理接口工具类
     */
    static class BodyHandlers {

        private BodyHandlers() {
        }

        public static BodyHandler<String> ofString() {
            return new HttpResponseHandlers.StringBodyHandler();
        }

        public static BodyHandler<String> ofString(long maxBytes) {
            return new HttpResponseHandlers.StringBodyHandler(maxBytes);
        }

        public static BodyHandler<byte[]> ofByteArray() {
            return new HttpResponseHandlers.ByteArrayBodyHandler();
        }

        public static BodyHandler<byte[]> ofByteArray(long maxBytes) {
            return new HttpResponseHandlers.ByteArrayBodyHandler(maxBytes);
        }
    }
}
