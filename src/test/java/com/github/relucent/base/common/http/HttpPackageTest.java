package com.github.relucent.base.common.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.relucent.base.common.http.jdk8.HttpClient;
import com.github.relucent.base.common.http.jdk8.HttpHeaders;
import com.github.relucent.base.common.http.jdk8.HttpRequest;
import com.github.relucent.base.common.http.jdk8.HttpResponse;
import com.sun.net.httpserver.HttpServer;

/**
 * http 包集成测试：用 JDK 内置 HttpServer 起本地服务，覆盖遗留 HttpUtil 与现代 jdk8.HttpClient。
 */
@SuppressWarnings("restriction")
public class HttpPackageTest {

	private HttpServer server;
	private String base;

	@Before
	public void setUp() throws IOException {
		server = HttpServer.create(new InetSocketAddress(0), 0);

		server.createContext("/get", exchange -> {
			byte[] body = "hello".getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, body.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(body);
			}
		});

		server.createContext("/crlf", exchange -> {
			byte[] body = "line1\r\nline2\r\n".getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
			exchange.sendResponseHeaders(200, body.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(body);
			}
		});

		server.createContext("/error", exchange -> {
			byte[] body = "boom".getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(500, body.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(body);
			}
		});

		server.createContext("/upload", exchange -> {
			byte[] buf = new byte[8192];
			ByteArrayOutputStream received = new ByteArrayOutputStream();
			int n;
			try (InputStream in = exchange.getRequestBody()) {
				while ((n = in.read(buf)) != -1) {
					received.write(buf, 0, n);
				}
			}
			// 回显收到的请求体，便于断言 multipart 内容确实被发送
			byte[] resp = ("received:" + new String(received.toByteArray(), StandardCharsets.UTF_8))
					.getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, resp.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(resp);
			}
		});

		server.createContext("/redirect", exchange -> {
			exchange.getResponseHeaders().add("Location", base + "/get");
			exchange.sendResponseHeaders(302, -1);
		});

		server.createContext("/gbk", exchange -> {
			// 以 GBK 编码返回"中文"，并在 Content-Type 声明 charset=GBK，验证响应解码尊重字符集
			byte[] body = "中文".getBytes(java.nio.charset.Charset.forName("GBK"));
			exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=GBK");
			exchange.sendResponseHeaders(200, body.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(body);
			}
		});

		server.createContext("/ua", exchange -> {
			// 回显请求携带的 User-Agent，便于断言默认 UA 与覆盖行为
			String ua = exchange.getRequestHeaders().getFirst("User-Agent");
			byte[] body = (ua == null ? "" : ua).getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(200, body.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(body);
			}
		});

		server.start();
		base = "http://localhost:" + server.getAddress().getPort();
	}

	@After
	public void tearDown() {
		if (server != null) {
			server.stop(0);
		}
	}

	// ============================ 遗留 HttpUtil ============================
	@Test
	public void testHttpUtilGetOk() {
		String body = HttpUtil.get(base + "/get");
		assertEquals("hello", body);
	}

	@Test
	public void testHttpUtilRespectsCharset() {
		// P2-4：遗留 HttpUtil 应按响应 Content-Type 的 charset 解码，而非硬编码 UTF-8
		String body = HttpUtil.get(base + "/gbk");
		assertEquals("中文", body);
	}

	@Test
	public void testHttpUtilExecuteDoesNotThrowOnError() {
		// execute 保持原契约：返回响应体字符串，不在 4xx/5xx 抛异常
		String body = HttpUtil.get(base + "/error");
		assertEquals("boom", body);
	}

	// ============================ 现代 jdk8.HttpClient ============================

	@Test
	public void testHttpClientSyncGet() throws IOException {
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/get").GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, resp.statusCode());
		assertEquals("hello", resp.body());
	}

	@Test
	public void testHttpClientErrorReturnsStatusNotThrow() throws IOException {
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/error").GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(500, resp.statusCode());
		assertEquals("boom", resp.body());
	}

	@Test
	public void testHttpClientAsyncGet() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		CompletableFuture<HttpResponse<String>> future = client.sendAsync(
				HttpRequest.newBuilder().uri(base + "/get").GET().build(), HttpResponse.BodyHandlers.ofString());
		HttpResponse<String> resp = future.get();
		assertEquals(200, resp.statusCode());
		assertEquals("hello", resp.body());
	}

	@Test
	public void testStringBodyPreservesCrlf() throws IOException {
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/crlf").GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals("line1\r\nline2\r\n", resp.body());
	}

	@Test
	public void testHttpClientRespectsCharset() throws IOException {
		// P1-1：现代 jdk8 API 通过 CodecUtil.parseCharset 解析字符集（不再依赖遗留 HttpUtil）
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/gbk").GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals("中文", resp.body());
	}

	@Test
	public void testResponseBodyTooLargeThrows() throws Exception {
		// P2-1：响应体超过上限应抛 IOException，避免超大响应撑爆堆内存
		HttpClient client = HttpClient.newHttpClient();
		try {
			client.send(HttpRequest.newBuilder().uri(base + "/get").GET().build(),
					HttpResponse.BodyHandlers.ofByteArray(1));
			assertTrue("响应体超过上限应当抛 IOException", false);
		} catch (IOException e) {
			// 期望：/get 返回 5 字节 > 上限 1 字节
		}
	}

	@Test
	public void testMultipartContentTypeInjectionPrevented() throws IOException {
		// P2-3：part 的 Content-Type 含 CRLF 注入时，应被剔除，避免破坏 multipart 结构
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher multipart = HttpRequest.BodyPublishers.ofMultipart(m -> {
			m.addBytes("file", "evil.txt", "secret-content".getBytes(StandardCharsets.UTF_8),
					"application/octet-stream\r\nX-Injected: evil");
		});
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/upload").POST(multipart).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, resp.statusCode());
		// 修复后 CRLF 被剔除：注入的 \r\n 不再产生独立头行（不应出现 "\r\nX-Injected: evil"）；
		// 合法内容仍完整到达。
		assertTrue("合法文件内容应到达", resp.body().contains("secret-content"));
		assertTrue("Content-Type 中的 CRLF 注入不应形成独立头行", !resp.body().contains("\r\nX-Injected: evil"));
	}

	@Test
	public void testDefaultUserAgentSent() throws IOException {
		// P2-7：未显式设置 UA 时，客户端应补默认 UA
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/ua").GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertTrue(resp.body().startsWith("Mozilla/5.0"));
	}

	@Test
	public void testCustomUserAgentOverride() throws IOException {
		// P2-7：显式设置的 UA 应覆盖默认值
		HttpClient client = HttpClient.newBuilder().userAgent("MyAgent/1.0").build();
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/ua").GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals("MyAgent/1.0", resp.body());
	}

	@Test
	public void testKeepAliveConsecutiveRequests() throws IOException {
		// P2-5：连续请求应正常复用连接（不 disconnect），无回归
		HttpClient client = HttpClient.newHttpClient();
		for (int i = 0; i < 3; i++) {
			HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/get").GET().build(),
					HttpResponse.BodyHandlers.ofString());
			assertEquals(200, resp.statusCode());
			assertEquals("hello", resp.body());
		}
	}

	@Test
	public void testHttpClientMultipartUpload() throws IOException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest.BodyPublisher multipart = HttpRequest.BodyPublishers.ofMultipart(m -> {
			m.addFormField("field", "value");
			m.addBytes("file", "test.txt", "content".getBytes(StandardCharsets.UTF_8), "text/plain");
		});
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/upload").POST(multipart).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, resp.statusCode());
		// P2-7 修复前 contentLength()==-1 会让请求体根本不发送，received 为空；
		// 修复后 multipart 内容应被真正写入连接，这里断言字段与文件内容都到达服务端。
		assertTrue("multipart 请求体应被发送", resp.body().contains("received:"));
		assertTrue("表单字段应到达服务端", resp.body().contains("field"));
		assertTrue("表单字段值应到达服务端", resp.body().contains("value"));
		assertTrue("文件内容应到达服务端", resp.body().contains("content"));
	}

	@Test
	public void testHttpClientFollowsRedirectByDefault() throws IOException {
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/redirect").GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, resp.statusCode());
	}

	@Test
	public void testHttpClientNoFollowRedirect() throws IOException {
		HttpClient client = HttpClient.newBuilder().followRedirects(false).build();
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder().uri(base + "/redirect").GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(302, resp.statusCode());
	}

	// ============================ HttpHeaders ============================

	@Test
	public void testHttpHeadersFirstValueCaseInsensitive() {
		Map<String, List<String>> map = new HashMap<>();
		map.put("X-Test", Arrays.asList("a", "b"));
		HttpHeaders headers = HttpHeaders.of(map);
		assertEquals("a", headers.firstValue("x-test"));
		assertEquals(2, headers.allValues("X-Test").size());
	}
}
