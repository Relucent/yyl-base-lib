package com.github.relucent.base.common.http.jdk8.internal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import com.github.relucent.base.common.http.jdk8.HttpResponse.BodyHandler;
import com.github.relucent.base.common.http.jdk8.HttpResponse.BodySubscriber;
import com.github.relucent.base.common.http.jdk8.HttpResponse.ResponseInfo;
import com.github.relucent.base.common.web.WebUtil;

public class HttpResponseHandlers {

    HttpResponseHandlers() {
    }

    public static class StringBodyHandler implements BodyHandler<String> {
        @Override
        public BodySubscriber<String> apply(ResponseInfo responseInfo) {
            String contentType = responseInfo.headers().firstValue("Content-Type");
            Charset charset = WebUtil.parseCharset(contentType);
            return inputStream -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            };
        }
    }

    public static class ByteArrayBodyHandler implements BodyHandler<byte[]> {
        @Override
        public BodySubscriber<byte[]> apply(ResponseInfo responseInfo) {
            return inputStream -> {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    return baos.toByteArray();
                }
            };
        }
    }
}
