package com.github.relucent.base.common.http.jdk8;

import java.io.IOException;
import java.io.InputStream;

import com.github.relucent.base.common.http.jdk8.internal.HttpResponseHandlers;

/**
 * HTTP 响应类
 * @param <T> 响应体数据类型
 */
public interface HttpResponse<T> {

    int statusCode();

    HttpHeaders headers();

    T body();

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

        public static BodyHandler<byte[]> ofByteArray() {
            return new HttpResponseHandlers.ByteArrayBodyHandler();
        }
    }
}
