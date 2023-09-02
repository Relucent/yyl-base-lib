package com.github.relucent.base.common.net.ssl;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;

/**
 * 跳过安全校验的信任管理器，默认信任所有客户端和服务端证书<br>
 */
public class SkipTrustManager extends X509ExtendedTrustManager {

    /**
     * 信任管理器，默认信任所有客户端和服务端证书
     */
    public static final SkipTrustManager INSTANCE = new SkipTrustManager();

    private static final X509Certificate[] EMPTY_X509_CERTIFICATE_ARRAY = {};

    private SkipTrustManager() {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return EMPTY_X509_CERTIFICATE_ARRAY;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
    }
}
