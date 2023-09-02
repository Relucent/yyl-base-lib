package com.github.relucent.base.common.net.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 跳过安全校验的 HTTPS域名校验器，信任所有域名。<br>
 */
public class SkipHostnameVerifier implements HostnameVerifier {

    /**
     * HTTPS域名校验器
     */
    public static SkipHostnameVerifier INSTANCE = new SkipHostnameVerifier();

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
