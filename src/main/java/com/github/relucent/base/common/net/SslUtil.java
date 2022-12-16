package com.github.relucent.base.common.net;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SslUtil {

	/** X509可信任证书管理器(忽略证书验证) */
	public static final X509TrustManager SKIP_X509_TRUST_MANAGER;
	/** 域名校验器(忽略域名校验) */
	public static final HostnameVerifier SKIP_HOSTNAME_VERIFIER;
	/** SSL套接字工厂(忽略SSL验证) */
	public static final SSLSocketFactory SKIP_SSL_SOCKET_FACTORY;

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
