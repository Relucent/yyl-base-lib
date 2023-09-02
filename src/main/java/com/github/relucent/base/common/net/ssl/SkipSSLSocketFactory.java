package com.github.relucent.base.common.net.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * 忽略安全校验的 SSLSocket工厂类
 */
public class SkipSSLSocketFactory extends SSLSocketFactory {

    /**
     * 忽略安全校验的 SSLSocket工厂类
     */
    public static SkipSSLSocketFactory INSTANCE = new SkipSSLSocketFactory();

    private final SSLSocketFactory sslSocketFactory;

    private SkipSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
            sslContext.init(null, new TrustManager[] { SkipTrustManager.INSTANCE }, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("Can't create unsecure trust manager", e);
        }
    }

    public Socket createSocket() throws IOException {
        return sslSocketFactory.createSocket();
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return sslSocketFactory.createSocket(address, port, localAddress, localPort);
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        return sslSocketFactory.createSocket(host, port);
    }

    public Socket createSocket(Socket socket, InputStream consumed, boolean autoClose) throws IOException {
        return sslSocketFactory.createSocket(socket, consumed, autoClose);
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return sslSocketFactory.createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(host, port, localHost, localPort);
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(host, port);
    }

    public String[] getDefaultCipherSuites() {
        return sslSocketFactory.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return sslSocketFactory.getSupportedCipherSuites();
    }
}
