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
     * 获得SSLSocket工厂类(该工厂类忽略SSL验证)
     * @return SSLSocket工厂类
     */
    public static SSLSocketFactory getTrustAnySSLSocketFactory() {
        return TRUST_ANY_SSL_SOCKET_FACTORY;
    };

    /**
     * 获得HTTPS域名校验(该类忽略SSL验证)
     * @return HTTPS域名校验
     */
    public static final HostnameVerifier getTrustAnyHostnameVerifier() {
        return TRUST_ANY_HOSTNAME_VERIFIER;
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

    /** HTTPS证书管理(信任所有) */
    private static final SSLSocketFactory TRUST_ANY_SSL_SOCKET_FACTORY;
    static {
        boolean trust = true;
        TRUST_ANY_HOSTNAME_VERIFIER = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return trust;
            }
        };
    }
    /** HTTPS域名校验(信任所有) */
    private static final HostnameVerifier TRUST_ANY_HOSTNAME_VERIFIER;
    static {
        String protocol = "TLS";
        String provider = "SunJSSE";
        try {
            SSLContext sslContext = SSLContext.getInstance(protocol, provider);
            sslContext.init(null, new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            } }, new java.security.SecureRandom());
            TRUST_ANY_SSL_SOCKET_FACTORY = sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("Can't create unsecure trust manager", e);
        }
    }
}
