package com.github.relucent.base.common.net;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SslHelper {

	/**
	 * 获得 KeyStore
	 * @param input 数据流
	 * @param password 密码
	 * @return KeyStore
	 */
	public static KeyStore getKeyStore(InputStream input, String password) {
		try {
			KeyStore keyStore = KeyStore.getInstance("");
			keyStore.load(input, password.toCharArray());
			return keyStore;
		} catch (Exception exception) {
			return null;
		}
	}

	/**
	 * 获得 KeyStore
	 * @param content 数据内容
	 * @param password 密码
	 * @return KeyStore
	 */
	public static KeyStore getKeyStore(byte[] content, String password) {
		InputStream input = null;
		try {
			return getKeyStore(input = new ByteArrayInputStream(content), password);
		} finally {
			closeQuietly(input);
		}
	}

	/**
	 * 获得X509证书
	 * @param input 证书的数据流
	 * @return X509证书
	 */
	public static X509Certificate getX509Certificate(InputStream input) {
		try {
			return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(input);
		} catch (Exception exception) {
			return null;
		}
	}

	/**
	 * 获得X509证书
	 * @param content 证书的内容
	 * @return X509证书
	 */
	public static X509Certificate getX509Certificate(byte[] content) {
		InputStream input = null;
		try {
			return getX509Certificate(input = new ByteArrayInputStream(content));
		} finally {
			closeQuietly(input);
		}
	}

	/**
	 * 获得X509可信任证书管理器(忽略证书验证)
	 * @return X509可信任证书管理器
	 */
	public static X509TrustManager getSkipX509TrustManager() {
		return SKIP_X509_TRUST_MANAGER;
	}

	/**
	 * 获得域名校验器(忽略域名校验)
	 * @return 域名校验器
	 */
	public static HostnameVerifier getSkipHostnameVerifier() {
		return SKIP_HOSTNAME_VERIFIER;
	}

	/**
	 * 获得SSL套接字工厂(忽略SSL验证)
	 * @return SSL套接字工厂
	 */
	public static SSLSocketFactory getSkipSSLSocketFactory() {
		return SKIP_SSL_SOCKET_FACTORY;
	}

	/**
	 * 关闭对象(流)
	 * @param closeable 可关闭对象
	 */
	private static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	/** X509可信任证书管理器 */
	private static final X509TrustManager SKIP_X509_TRUST_MANAGER;
	/** 域名校验器 */
	private static final HostnameVerifier SKIP_HOSTNAME_VERIFIER;
	/** SSL套接字工厂 */
	private static final SSLSocketFactory SKIP_SSL_SOCKET_FACTORY;
	static {
		SKIP_X509_TRUST_MANAGER = new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
		};
	}
	static {
		SKIP_HOSTNAME_VERIFIER = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	}
	static {
		String protocol = "TLS";
		String provider = "SunJSSE";
		try {
			SSLContext sslContext = SSLContext.getInstance(protocol, provider);
			sslContext.init(null, new TrustManager[] { SKIP_X509_TRUST_MANAGER }, new java.security.SecureRandom());
			SKIP_SSL_SOCKET_FACTORY = sslContext.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException("Can't create unsecure trust manager", e);
		}
	}
}
