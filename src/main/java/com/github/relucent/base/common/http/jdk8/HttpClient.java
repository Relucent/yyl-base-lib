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
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import com.github.relucent.base.common.concurrent.GlobalThreadPool;
import com.github.relucent.base.common.http.jdk8.HttpRequest.BodyPublisher;
import com.github.relucent.base.common.http.jdk8.HttpResponse.BodyHandler;
import com.github.relucent.base.common.http.jdk8.HttpResponse.ResponseInfo;
import com.github.relucent.base.common.http.jdk8.internal.HttpResponseImpl;
import com.github.relucent.base.common.http.jdk8.internal.HttpResponseInfoImpl;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.net.SslUtil;

/**
 * HTTP工具类<br>
 * 代码风格参考 JDK17 的 java.net.http.HttpClient，可在 JDK8 版本使用。<br>
 */
public class HttpClient implements AutoCloseable {

	/** 默认 User-Agent，请求未显式设置时自动补上 */
	private static final String DEFAULT_USER_AGENT = "Mozilla/5.0";

	private final ExecutorService executor;
	private final boolean ownsExecutor;
	private final Proxy proxy;
	private final int connectTimeoutMillis;
	private final int readTimeoutMillis;
	private final boolean ignoreSslVerification;
	private final boolean followRedirects;
	private final String userAgent;

	private HttpClient(Builder builder) {
		if (builder.executor != null) {
			this.executor = builder.executor;
			this.ownsExecutor = true;
		} else {
			// 默认复用库内有界全局线程池（核心线程数随 CPU 自适应，最大 200），避免无界 cached 线程池泄漏资源
			this.executor = GlobalThreadPool.getInstance().getThreadPool();
			this.ownsExecutor = false;
		}
		this.proxy = builder.proxy;
		this.connectTimeoutMillis = builder.connectTimeoutMillis;
		this.readTimeoutMillis = builder.readTimeoutMillis;
		this.ignoreSslVerification = builder.ignoreSslVerification;
		this.followRedirects = builder.followRedirects;
		this.userAgent = builder.userAgent;
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
		// 仅当用户显式传入 executor 时才负责关闭；复用共享 GlobalThreadPool 时不关闭，避免影响其他调用方
		if (!ownsExecutor) {
			return;
		}
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
		// 是否自动跟随重定向。默认 true 与 HttpURLConnection 原有行为一致（保持向后兼容）；
		// 设为 false 可避免 3xx 把 POST 静默转成 GET 的隐患。
		conn.setInstanceFollowRedirects(followRedirects);

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

		// 默认 User-Agent：请求未显式设置时补上，便于服务端识别/统计
		if (StringUtil.isEmpty(requestHeaders.firstValue("User-Agent"))) {
			conn.setRequestProperty("User-Agent", userAgent);
		}

		BodyPublisher bodyPublisher = request.bodyPublisher();
		if (StringUtil.isEmpty(requestHeaders.firstValue("Content-Type")) && bodyPublisher != null
				&& StringUtil.isNotBlank(bodyPublisher.contentType())) {
			conn.setRequestProperty("Content-Type", bodyPublisher.contentType());
		}

		// contentLength() > 0：已知长度，用定长模式；
		// == -1：长度未知（如 multipart），走分块模式；
		// == 0：无请求体，跳过。
		if (bodyPublisher != null && bodyPublisher.contentLength() != 0) {
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
		ResponseInfo info = new HttpResponseInfoImpl(status, conn);
		HttpResponse.BodySubscriber<T> subscriber = handler.apply(info);

		try (InputStream input = is) {
			T body = subscriber.getBody(is);
			return new HttpResponseImpl<>(status, info.headers(), body);
		}
		// 注：此处不再调用 disconnect()），以便 JVM 的keep-alive 连接池复用连接，降低同主机高频请求开销。
		// 响应流已在上面的 try-with-resources中关闭，连接已处于可复用状态。
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
		private boolean followRedirects = true;
		private String userAgent = DEFAULT_USER_AGENT;

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

		/**
		 * 配置是否自动跟随 3xx 重定向。默认 true（与 HttpURLConnection 原有行为一致）。<br>
		 * 注意：跟随重定向时，HTTP 规范会把 POST 的 302 静默转成 GET；若需严格保留方法，请设为 false。
		 * @param follow true 表示自动跟随，false 表示不跟随
		 */
		public Builder followRedirects(boolean follow) {
			this.followRedirects = follow;
			return this;
		}

		/**
		 * 设置请求的 User-Agent
		 * @param userAgent 用户代理字符串
		 */
		public Builder userAgent(String userAgent) {
			this.userAgent = userAgent;
			return this;
		}

		public HttpClient build() {
			return new HttpClient(this);
		}
	}
}
