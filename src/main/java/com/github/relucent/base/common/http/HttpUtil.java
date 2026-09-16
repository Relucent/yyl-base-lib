package com.github.relucent.base.common.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.github.relucent.base.common.collection.CaseInsensitiveKeyMap;
import com.github.relucent.base.common.exception.GeneralException;
import com.github.relucent.base.common.io.IoUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.net.SslUtil;

/**
 * HTTP 工具类，提供简单的 HTTP 请求方法（GET/POST/PUT/DELETE 等）。<br>
 * 本类作为轻量级工具类长期保留，适合简单场景直接使用；<br>
 * 新业务场景更推荐使用 {@link com.github.relucent.base.common.http.jdk8.HttpClient}<br>
 * （仿 JDK17 {@code java.net.http.HttpClient}，兼容 JDK8，支持异步、重定向策略、TLS 配置等）。<br>
 * @author YYL
 * @version 2005-12-06 09:30
 */
public class HttpUtil {

	// ==============================Fields===========================================
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	public static final String HEADER_USER_AGENT = "User-Agent";

	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	public static final String CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String USER_AGENT_FOR_MOZILLA = "Mozilla/5.0";
	private static final String UTF_8 = "UTF-8";
	private static final int CONNECT_TIMEOUT = 5 * 1000;// 连接超时(单位毫秒)
	private static final int READ_TIMEOUT = 30 * 1000;// 读取超时(单位毫秒)

	/**
	 * 是否跳过 HTTPS 的证书与主机名校验。默认 false（安全优先，正常校验）。<br>
	 * 仅当设置为 true 时 {@link #ignoreTLS(HttpURLConnection)} 才会生效，用于兼容自签名证书，<br>
	 * 但会带来中间人攻击风险，生产环境请谨慎使用。<br>
	 */
	private static volatile boolean ignoreSslVerification = false;

	// ==============================Constructors=====================================
	/**
	 * 工具类方法，实例不应在标准编程中构造。
	 */
	protected HttpUtil() {
	}

	// ==============================Methods==========================================
	/**
	 * 发送GET请求
	 * @param url 请求地址
	 * @return 请求的结果
	 */
	public static String get(String url) {
		return get(url, null);
	}

	/**
	 * 发送GET请求
	 * @param url         请求地址
	 * @param queryParams URL参数
	 * @return 请求的结果
	 */
	public static String get(String url, Map<String, String> queryParams) {
		return get(url, queryParams, null);
	}

	/**
	 * 发送GET请求
	 * @param url         请求地址
	 * @param queryParams URL参数
	 * @param headers     HTTP头数据
	 * @return 请求的结果
	 */
	public static String get(String url, Map<String, String> queryParams, Map<String, String> headers) {
		String query = buildWithQueryString(queryParams);
		if (!query.isEmpty()) {
			url += (url.indexOf("?") == -1 ? "?" : "&") + query;
		}
		headers = castHeaderMap(headers);
		return execute(url, HttpMethod.GET, null, headers);
	}

	/**
	 * 发送POST请求
	 * @param url 请求地址
	 * @return 请求的结果
	 */
	public static String post(String url) {
		return post(url, null);
	}

	/**
	 * 发送POST请求（以 application/x-www-form-urlencoded 格式提交表单体）<br>
	 * 注意：queryParams 会作为请求体发送，而非拼接到 URL 后面。
	 * @param url         请求地址
	 * @param queryParams 表单参数（作为请求体）
	 * @return 请求的结果
	 */
	public static String post(String url, Map<String, String> queryParams) {
		return post(url, queryParams, null);
	}

	/**
	 * 发送POST请求（以 application/x-www-form-urlencoded 格式提交表单体）<br>
	 * 注意：queryParams 会作为请求体发送，而非拼接到 URL 后面。
	 * @param url         请求地址
	 * @param queryParams 表单参数（作为请求体）
	 * @param headers     HTTP头数据
	 * @return 请求的结果
	 */
	public static String post(String url, Map<String, String> queryParams, Map<String, String> headers) {
		String data = buildWithQueryString(queryParams);
		return post(url, data, headers);
	}

	/**
	 * 发送POST请求
	 * @param url     请求地址
	 * @param data    提交数据
	 * @param headers HTTP头数据
	 * @return 请求的结果
	 */
	public static String post(String url, String data, Map<String, String> headers) {
		return execute(url, HttpMethod.POST, data, headers);
	}

	/**
	 * 发送PUT请求
	 * @param url  请求地址
	 * @param body 提交数据
	 * @return 请求的结果
	 */
	public static String put(String url, String body) {
		return put(url, body, null);
	}

	/**
	 * 发送PUT请求
	 * @param url     请求地址
	 * @param body    提交数据
	 * @param headers HTTP头数据
	 * @return 请求的结果
	 */
	public static String put(String url, String body, Map<String, String> headers) {
		return execute(url, HttpMethod.PUT, body, headers);
	}

	/**
	 * 发送DELETE请求
	 * @param url 请求地址
	 * @return 请求的结果
	 */
	public static String delete(String url) {
		return execute(url, HttpMethod.DELETE, null, null);
	}

	/**
	 * 执行请求
	 * @param url     请求地址
	 * @param method  请求方法
	 * @param body    请求主体 (针对POST请求,GET请求应为NULL)
	 * @param headers HTTP头数据
	 * @return 请求的结果
	 */
	public static String execute(String url, HttpMethod method, String body, Map<String, String> headers) {
		HttpURLConnection conn = null;
		try {
			conn = getConnection(url, method, headers);
			conn.connect();
			if (method.hasBody() && StringUtil.isNotEmpty(body)) {
				writeAndClose(conn.getOutputStream(), body);
			}
			// 先取状态码，再决定读正常流还是错误流（HttpURLConnection 在 4xx/5xx 时
			// getInputStream() 会抛 IOException，必须改读 getErrorStream()）
			int status = conn.getResponseCode();
			InputStream input = status >= 400 ? conn.getErrorStream() : conn.getInputStream();
			if (input == null) {
				input = new ByteArrayInputStream(new byte[0]);
			}
			// 按响应 Content-Type 声明的字符集解码，未声明时回退 UTF-8（修复硬编码 UTF-8 导致 GBK 等乱码）
			Charset charset = parseCharset(conn.getContentType());
			String responseBody = readAndClose(input, charset);
			// LAST_STATUS.set(status);
			return responseBody;
		} catch (IOException e) {
			throw new GeneralException(e);
		} finally {
			closeQuietly(conn);
		}
	}

	/**
	 * 获得一个HTTP连接
	 * @param url     请求地址
	 * @param method  请求的方法
	 * @param headers HTTP头数据
	 * @return HTTP连接对象
	 * @throws IOException 出现IO错误，抛出异常
	 */
	public static HttpURLConnection getConnection(String url, HttpMethod method, Map<String, String> headers)
			throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		if (ignoreSslVerification) {
			ignoreTLS(conn);
		}
		conn.setRequestMethod(method.name());
		if (method.hasBody()) {
			conn.setDoOutput(true);
		} else {
			conn.setDoOutput(false);
		}
		conn.setDoInput(true);
		conn.setConnectTimeout(CONNECT_TIMEOUT);
		conn.setReadTimeout(READ_TIMEOUT);
		headers = headers != null ? new CaseInsensitiveKeyMap<String>(headers) : new CaseInsensitiveKeyMap<String>();
		if (!headers.containsKey(HEADER_CONTENT_TYPE)) {
			conn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM_URL_ENCODED + "; charset=UTF-8");
		}
		if (!headers.containsKey(HEADER_USER_AGENT)) {
			conn.setRequestProperty(HEADER_USER_AGENT, USER_AGENT_FOR_MOZILLA);
		}
		for (Entry<String, String> entry : headers.entrySet()) {
			conn.setRequestProperty(entry.getKey(), entry.getValue());
		}
		return conn;
	}

	/**
	 * 配置是否跳过 HTTPS 的证书与主机名校验。
	 * @param ignore true 表示跳过校验（兼容自签名证书，但有中间人攻击风险），false 表示正常校验（默认）
	 */
	public static void setIgnoreSslVerification(boolean ignore) {
		ignoreSslVerification = ignore;
	}

	/**
	 * 忽略 HTTPS（强制跳过证书与主机名校验）。<br>
	 * 仅在 {@link #ignoreSslVerification} 为 true 时由 {@link #getConnection} 调用；也可由调用方主动调用以跳过校验。
	 * @param conn HTTP连接
	 */
	public static void ignoreTLS(HttpURLConnection conn) {
		if (conn instanceof HttpsURLConnection) {
			((HttpsURLConnection) conn).setSSLSocketFactory(SslUtil.SKIP_SSL_SOCKET_FACTORY);
			((HttpsURLConnection) conn).setHostnameVerifier(SslUtil.SKIP_HOSTNAME_VERIFIER);
		}
	}

	/**
	 * 拼装参数字符串
	 * @param url         URL地址
	 * @param queryParams 参数
	 * @return 参数字符串
	 */
	public static String buildUrlWithQueryString(String url, Map<String, String> queryParams) {
		String query = buildWithQueryString(queryParams);
		if (!query.isEmpty()) {
			url += (url.indexOf("?") == -1 ? "?" : "&") + query;
		}
		return url;
	}

	/**
	 * 拼装参数字符串
	 * @param queryParams 参数
	 * @return 参数字符串
	 */
	public static String buildWithQueryString(Map<String, String> queryParams) {
		if (queryParams == null || queryParams.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Entry<String, String> entry : queryParams.entrySet()) {
			if (first) {
				first = false;
			} else {
				builder.append("&");
			}
			String name = entry.getKey();
			String value = entry.getValue();
			if (value != null && !value.isEmpty()) {
				builder.append(encodeURL(name)).append("=").append(encodeURL(value));
			}
		}
		return builder.toString();
	}

	/**
	 * UEL字符转码
	 * @param value 字符串
	 * @return 转码后的字符串
	 */
	public static String encodeURL(String value) {
		try {
			return URLEncoder.encode(value, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 关闭连接
	 * @param conn HTTP连接
	 */
	public static void closeQuietly(HttpURLConnection conn) {
		if (conn != null) {
			try {
				conn.disconnect();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 从 Content-Type / Content-Disposition 等媒体类型字符串中解析 charset 参数。<br>
	 * 未指定或非法时返回 UTF-8（默认编码）。例如：<br>
	 * - {@code "text/html; charset=UTF-8"} → UTF-8 <br>
	 * - {@code "application/json"} → UTF-8（默认） <br>
	 * - {@code null} → UTF-8（默认） <br>
	 * @param contentType 媒体类型字符串（可为 null）
	 * @return 解析出的字符集，解析失败回退 UTF-8
	 */
	public static Charset parseCharset(String contentType) {
		if (contentType == null) {
			return StandardCharsets.UTF_8;
		}
		int semicolonIndex = contentType.indexOf(';');
		if (semicolonIndex >= 0 && semicolonIndex + 1 < contentType.length()) {
			String params = contentType.substring(semicolonIndex + 1);
			String[] paramPairs = params.split(";");
			for (String param : paramPairs) {
				param = param.trim();
				if (param.toLowerCase().startsWith("charset=")) {
					String charsetName = param.substring(8).trim();
					// 移除可能的引号，如 charset="UTF-8"
					if ((charsetName.startsWith("\"") && charsetName.endsWith("\""))
							|| (charsetName.startsWith("'") && charsetName.endsWith("'"))) {
						charsetName = charsetName.substring(1, charsetName.length() - 1);
					}
					try {
						return Charset.forName(charsetName);
					} catch (Exception e) {
						// 无效 charset，返回默认 UTF-8
						return StandardCharsets.UTF_8;
					}
				}
			}
		}
		return StandardCharsets.UTF_8;
	}

	/**
	 * 转换请求头MAP(请求头的名称应该是大小写不敏感的)
	 * @param headers 请求头MAP
	 * @return 大小写不敏感的请求头MAP
	 */
	private static CaseInsensitiveKeyMap<String> castHeaderMap(Map<String, String> headers) {
		if (headers == null) {
			return new CaseInsensitiveKeyMap<String>();
		}
		if (headers instanceof CaseInsensitiveKeyMap) {
			return (CaseInsensitiveKeyMap<String>) headers;
		}
		return new CaseInsensitiveKeyMap<String>(headers);
	}

	private static void writeAndClose(OutputStream output, String data) throws IOException {
		try {
			output.write(data.getBytes(Charset.forName(UTF_8)));
			output.flush();
		} finally {
			IoUtil.closeQuietly(output);
		}
	}

	private static String readAndClose(InputStream input, Charset charset) throws IOException {
		ByteArrayOutputStream output = null;
		try {
			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			for (int n = 0; -1 != (n = input.read(buffer));) {
				output.write(buffer, 0, n);
			}
			return new String(output.toByteArray(), charset);
		} finally {
			IoUtil.closeQuietly(input);
			IoUtil.closeQuietly(output);
		}
	}
}
