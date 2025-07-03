package com.github.relucent.base.common.http.jdk8.internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.github.relucent.base.common.codec.CodecUtil;
import com.github.relucent.base.common.http.jdk8.HttpRequest.BodyPublisher;

public class HttpRequestPublishers {

    HttpRequestPublishers() {
    }

    /**
     * 不需要发布数据
     */
    public static class EmptyPublisher implements BodyPublisher {

        @Override
        public long contentLength() {
            return 0;
        }

        @Override
        public String contentType() {
            return null;
        }

        @Override
        public void writeTo(OutputStream out) {
        }
    }

    /**
     * 发布 String 数据（如 JSON）
     */
    public static class StringBodyPublisher implements BodyPublisher {

        private final byte[] body;
        private final String contentType;

        public StringBodyPublisher(String content, Charset charset) {
            this.body = content.getBytes(charset);
            this.contentType = "text/plain; charset=" + charset.name();
        }

        @Override
        public long contentLength() {
            return body.length;
        }

        @Override
        public String contentType() {
            return contentType;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(body);
        }
    }

    /**
     * 发布 application/x-www-form-urlencoded 表单
     */
    public static class FormBodyPublisher implements BodyPublisher {

        private final byte[] body;

        public FormBodyPublisher(Map<String, String> formData) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(CodecUtil.encodeURI(entry.getKey()));
                sb.append("=");
                sb.append(CodecUtil.encodeURI(entry.getValue()));
            }
            this.body = sb.toString().getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public long contentLength() {
            return body.length;
        }

        @Override
        public String contentType() {
            return "application/x-www-form-urlencoded; charset=UTF-8";
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(body);
        }
    }

    /**
     * 发布原始 byte[] 数据，如二进制、图片
     */
    public static class ByteArrayBodyPublisher implements BodyPublisher {

        private final byte[] body;
        private final String contentType;

        public ByteArrayBodyPublisher(byte[] body, String contentType) {
            if (body == null) {
                throw new IllegalArgumentException("body cannot be null");
            }
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }
            this.body = body;
            this.contentType = contentType;
        }

        @Override
        public long contentLength() {
            return body.length;
        }

        @Override
        public String contentType() {
            return contentType;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(body);
        }
    }

    public static class MultipartBodyPublisher implements BodyPublisher {

        public static class Part {
            private final String name;
            private final String filename;
            private final String contentType;
            private final Supplier<InputStream> contentSupplier;

            public Part(String name, String filename, String contentType, Supplier<InputStream> contentSupplier) {
                this.name = name;
                this.filename = filename;
                this.contentType = contentType;
                this.contentSupplier = contentSupplier;
            }

            public String getName() {
                return name;
            }

            public String getFilename() {
                return filename;
            }

            public String getContentType() {
                return contentType;
            }

            public Supplier<InputStream> getContentSupplier() {
                return contentSupplier;
            }
        }

        private final String boundary;
        private final List<Part> parts = new ArrayList<>();
        private final Map<String, String> formFields = new LinkedHashMap<>();

        public MultipartBodyPublisher() {
            this.boundary = "----MultipartBoundary" + UUID.randomUUID().toString().replace("-", "");
        }

        public MultipartBodyPublisher addFormField(String name, String value) {
            formFields.put(name, value);
            return this;
        }

        public MultipartBodyPublisher addFile(String fieldName, File file, String contentType) {
            return addPart(fieldName, file.getName(), contentType, () -> {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }

        public MultipartBodyPublisher addBytes(String fieldName, String filename, byte[] data, String contentType) {
            return addPart(fieldName, filename, contentType, () -> new ByteArrayInputStream(data));
        }

        public MultipartBodyPublisher addStream(String fieldName, String filename, InputStream stream,
                String contentType) {
            return addPart(fieldName, filename, contentType, () -> stream);
        }

        public MultipartBodyPublisher addPart(String name, String filename, String contentType,
                Supplier<InputStream> contentSupplier) {
            parts.add(new Part(name, filename, contentType, contentSupplier));
            return this;
        }

        @Override
        public long contentLength() {
            return -1;
        }

        @Override
        public String contentType() {
            return "multipart/form-data; boundary=" + boundary;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            final byte[] LINE_FEED = "\r\n".getBytes(StandardCharsets.UTF_8);

            // Write form fields
            for (Map.Entry<String, String> entry : formFields.entrySet()) {
                out.write(("--" + boundary).getBytes(StandardCharsets.UTF_8));
                out.write(LINE_FEED);
                out.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"")
                        .getBytes(StandardCharsets.UTF_8));
                out.write(LINE_FEED);
                out.write(LINE_FEED);
                out.write(entry.getValue().getBytes(StandardCharsets.UTF_8));
                out.write(LINE_FEED);
            }

            // Write file parts
            for (Part part : parts) {
                out.write(("--" + boundary).getBytes(StandardCharsets.UTF_8));
                out.write(LINE_FEED);
                out.write(("Content-Disposition: form-data; name=\"" + part.getName() + "\"; filename=\""
                        + part.getFilename() + "\"").getBytes(StandardCharsets.UTF_8));
                out.write(LINE_FEED);
                out.write(("Content-Type: " + part.getContentType()).getBytes(StandardCharsets.UTF_8));
                out.write(LINE_FEED);
                out.write(LINE_FEED);
                try (InputStream inputStream = part.getContentSupplier().get()) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
                out.write(LINE_FEED);
            }

            // End boundary
            out.write(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
            out.write(LINE_FEED);
        }
    }
}
