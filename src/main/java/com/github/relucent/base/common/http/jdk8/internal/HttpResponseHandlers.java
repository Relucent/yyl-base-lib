package com.github.relucent.base.common.http.jdk8.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.github.relucent.base.common.http.HttpUtil;
import com.github.relucent.base.common.http.jdk8.HttpResponse.BodyHandler;
import com.github.relucent.base.common.http.jdk8.HttpResponse.BodySubscriber;
import com.github.relucent.base.common.http.jdk8.HttpResponse.ResponseInfo;

public class HttpResponseHandlers {

	/** 响应体默认读取上限（64MB），超过将抛 IOException，避免超大响应撑爆堆内存 */
	private static final long DEFAULT_MAX_RESPONSE_BYTES = 64L * 1024 * 1024;

	HttpResponseHandlers() {
	}

	public static class StringBodyHandler implements BodyHandler<String> {

		private final long maxBytes;

		public StringBodyHandler() {
			this(DEFAULT_MAX_RESPONSE_BYTES);
		}

		public StringBodyHandler(long maxBytes) {
			this.maxBytes = maxBytes;
		}

		@Override
		public BodySubscriber<String> apply(ResponseInfo responseInfo) {
			String contentType = responseInfo.headers().firstValue("Content-Type");
			Charset charset = HttpUtil.parseCharset(contentType);
			return input -> new String(readBytes(input, maxBytes), charset);
		}
	}

	public static class ByteArrayBodyHandler implements BodyHandler<byte[]> {

		private final long maxBytes;

		public ByteArrayBodyHandler() {
			this(DEFAULT_MAX_RESPONSE_BYTES);
		}

		public ByteArrayBodyHandler(long maxBytes) {
			this.maxBytes = maxBytes;
		}

		@Override
		public BodySubscriber<byte[]> apply(ResponseInfo responseInfo) {
			return input -> readBytes(input, maxBytes);
		}
	}

	private static byte[] readBytes(InputStream input, long maxBytes) throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[8192];
			long total = 0;
			int len;
			while ((len = input.read(buffer)) != -1) {
				total += len;
				if (total > maxBytes) {
					throw new IOException("Response body too large, exceeded " + maxBytes + " bytes");
				}
				output.write(buffer, 0, len);
			}
			return output.toByteArray();
		}
	}
}
