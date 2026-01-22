package com.github.relucent.base.common.http.jdk8;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import com.github.relucent.base.common.http.jdk8.HttpRequest.BodyPublisher;
import com.github.relucent.base.common.http.jdk8.HttpResponse.BodyHandler;
import com.github.relucent.base.common.http.jdk8.HttpResponse.ResponseInfo;
import com.github.relucent.base.common.http.jdk8.internal.HttpResponseImpl;
import com.github.relucent.base.common.http.jdk8.internal.HttpResponseInfoImpl;
import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.net.SslUtil;

/**
 * HTTP工具类<br>
 * 代码风格参考 JDK17 的 java.net.http.HttpClient，可在 JDK8 版本使用。<br>
 */
public class HttpClient implements AutoCloseable {

    private final ExecutorService executor;
    private final Proxy proxy;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;
    private final boolean ignoreSslVerification;

    private HttpClient(Builder builder) {
        this.executor = builder.executor != null ? builder.executor : Executors.newCachedThreadPool();
        this.proxy = builder.proxy;
        this.connectTimeoutMillis = builder.connectTimeoutMillis;
        this.readTimeoutMillis = builder.readTimeoutMillis;
        this.ignoreSslVerification = builder.ignoreSslVerification;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static HttpClient newHttpClient() {
        return newBuilder().build();
    }

    /**
     * 同步请求
     * @param <T>     响应体类型
     * @param request 请求对象
     * @param handler 响应内容处理
     * @return 响应对象
     * @throws IOException 网络异常，或者文件流读写异常
     */
    public <T> HttpResponse<T> send(HttpRequest request, BodyHandler<T> handler) throws IOException {
        return sendInternal(request, handler);
    }

    /**
     * 异步请求
     * @param <T>     响应体类型
     * @param request 请求对象
     * @param handler 响应内容处理
     * @return 异步计算的结果
     */
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, BodyHandler<T> handler) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendInternal(request, handler);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    @Override
    public void close() {
        boolean terminated = executor.isTerminated();
        if (!terminated) {
            executor.shutdown();
            boolean interrupted = false;
            while (!terminated) {
                try {
                    terminated = executor.awaitTermination(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    if (!interrupted) {
                        interrupted = true;
                        executor.shutdownNow();
                        if (executor.isTerminated())
                            break;
                    }
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 请求处理
     * @param <T>     响应体类型
     * @param request 请求对象
     * @param handler 响应内容处理
     * @return 响应对象
     * @throws IOException 网络异常，或者文件流读写异常
     */
    private <T> HttpResponse<T> sendInternal(HttpRequest request, BodyHandler<T> handler) throws IOException {
        HttpURLConnection conn = null;
        try {
            if (proxy != null) {
                conn = (HttpURLConnection) request.uri().toURL().openConnection(proxy);
            } else {
                conn = (HttpURLConnection) request.uri().toURL().openConnection();
            }
            if (conn instanceof HttpsURLConnection && ignoreSslVerification) {
                ((HttpsURLConnection) conn).setSSLSocketFactory(SslUtil.SKIP_SSL_SOCKET_FACTORY);
                ((HttpsURLConnection) conn).setHostnameVerifier(SslUtil.SKIP_HOSTNAME_VERIFIER);
            }

            conn.setConnectTimeout(connectTimeoutMillis);
            conn.setReadTimeout(readTimeoutMillis);
            conn.setRequestMethod(request.method());
            conn.setDoInput(true);

            HttpHeaders requestHeaders = request.headers();
            // 设置请求头
            for (Map.Entry<String, List<String>> e : requestHeaders.map().entrySet()) {
                String key = e.getKey();

                // 跳过，Content-Length 由 HttpURLConnection 自动管理
                if ("Content-Length".equalsIgnoreCase(key)) {
                    continue;
                }

                for (String val : e.getValue()) {
                    conn.addRequestProperty(key, val);
                }
            }

            BodyPublisher bodyPublisher = request.bodyPublisher();
            if (StringUtil.isEmpty(requestHeaders.firstValue("Content-Type"))
                    && StringUtil.isNotBlank(bodyPublisher.contentType())) {
                conn.setRequestProperty("Content-Type", bodyPublisher.contentType());
            }

            if (request.bodyPublisher() != null && request.bodyPublisher().contentLength() > 0) {
                long contentLength = bodyPublisher.contentLength();
                if (contentLength >= 0) {
                    conn.setFixedLengthStreamingMode(contentLength);
                } else {
                    conn.setChunkedStreamingMode(8192);// (8KB)
                }
                conn.setDoOutput(true);
                bodyPublisher.writeTo(conn.getOutputStream());
            }

            InputStream is = null;
            int status = conn.getResponseCode();
            if (status >= 400) {
                is = conn.getErrorStream();
                if (is == null) {
                    is = new ByteArrayInputStream(new byte[0]);
                }
            } else {
                is = conn.getInputStream();
            }
            ResponseInfo info = new HttpResponseInfoImpl(conn);
            HttpResponse.BodySubscriber<T> subscriber = handler.apply(info);

            try (InputStream input = is) {
                T body = subscriber.getBody(is);
                return new HttpResponseImpl<>(status, info.headers(), body);
            }
        } finally {
            IoUtil.closeQuietly(conn);
        }
    }

    /**
     * 构造器
     */
    public static class Builder {

        private ExecutorService executor;
        private Proxy proxy;
        private int connectTimeoutMillis = 10 * 1000;
        private int readTimeoutMillis = 10 * 1000;
        private boolean ignoreSslVerification = false;

        public Builder executor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder connectTimeoutMillis(int timeout) {
            this.connectTimeoutMillis = timeout;
            return this;
        }

        public Builder readTimeoutMillis(int timeout) {
            this.readTimeoutMillis = timeout;
            return this;
        }

        public Builder ignoreSslVerification(boolean ignore) {
            this.ignoreSslVerification = ignore;
            return this;
        }

        public HttpClient build() {
            return new HttpClient(this);
        }
    }
}
