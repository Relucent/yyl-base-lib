package com.github.relucent.base.common.net;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import com.github.relucent.base.common.net.ssl.SkipHostnameVerifier;
import com.github.relucent.base.common.net.ssl.SkipSSLSocketFactory;
import com.github.relucent.base.common.net.ssl.SkipTrustManager;

public class SslUtil {

    /** X509可信任证书管理器(忽略证书验证) */
    public static final X509TrustManager SKIP_X509_TRUST_MANAGER = SkipTrustManager.INSTANCE;
    /** 域名校验器(忽略域名校验) */
    public static final HostnameVerifier SKIP_HOSTNAME_VERIFIER = SkipHostnameVerifier.INSTANCE;
    /** SSL套接字工厂(忽略SSL验证) */
    public static final SSLSocketFactory SKIP_SSL_SOCKET_FACTORY = SkipSSLSocketFactory.INSTANCE;
}
