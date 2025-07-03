package com.github.relucent.base.common.http.jdk8;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

import com.github.relucent.base.common.http.jdk8.internal.HttpRequestBuilderImpl;
import com.github.relucent.base.common.http.jdk8.internal.HttpRequestPublishers;
import com.github.relucent.base.common.http.jdk8.internal.HttpRequestPublishers.MultipartBodyPublisher;

/**
 * HTTP 请求类
 */
public interface HttpRequest {

    URI uri();

    String method();

    HttpHeaders headers();

    BodyPublisher bodyPublisher();

    static Builder newBuilder() {
        return new HttpRequestBuilderImpl();
    }

    interface Builder {

        Builder uri(URI uri);

        Builder uri(String uri);

        Builder GET();

        Builder POST(BodyPublisher bodyPublisher);

        Builder PUT(BodyPublisher bodyPublisher);

        Builder DELETE();

        Builder header(String name, String value);

        Builder setHeader(String name, String value);

        Builder headers(Map<String, String> headers);

        HttpRequest build();
    }

    /**
     * 请求体内容发布器
     */
    public interface BodyPublisher {
        /**
         * 请求体内容写入指定的输出流中
         * @param output 指定的输出流中，调用方负责关闭该流。
         * @throws IOException 写入过程中发生的 I/O 异常
         */
        void writeTo(OutputStream output) throws IOException;

        /**
         * 获取请求体内容类型，如 application/json 或 multipart/form-data
         * @return 内容类型
         */
        String contentType();

        /**
         * 获取请求体的内容长度（单位：字节）
         * @return 内容长度，返回 -1 表示未知长度，将启用 chunked 模式
         */
        long contentLength();
    }

    /**
     * 请求体内容发布器工具类
     */
    public static final class BodyPublishers {

        private BodyPublishers() {
        }

        public static BodyPublisher noBody() {
            return new HttpRequestPublishers.EmptyPublisher();
        }

        public static BodyPublisher ofString(String content) {
            return new HttpRequestPublishers.StringBodyPublisher(content, StandardCharsets.UTF_8);
        }

        public static BodyPublisher ofByteArray(byte[] data, String contentType) {
            return new HttpRequestPublishers.ByteArrayBodyPublisher(data, contentType);
        }

        public static BodyPublisher ofByteArray(byte[] data) {
            return new HttpRequestPublishers.ByteArrayBodyPublisher(data, "application/octet-stream");
        }

        public static BodyPublisher ofFormData(Map<String, String> formData) {
            return new HttpRequestPublishers.FormBodyPublisher(formData);
        }

        public static BodyPublisher ofMultipart(Consumer<MultipartBodyPublisher> builder) {
            MultipartBodyPublisher multipart = new MultipartBodyPublisher();
            builder.accept(multipart);
            return multipart;
        }
    }
}