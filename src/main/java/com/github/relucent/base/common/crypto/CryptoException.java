package com.github.relucent.base.common.crypto;

/**
 * 加密解密异常
 */
@SuppressWarnings("serial")
public class CryptoException extends RuntimeException {

    public CryptoException(Throwable e) {
        super(e.toString(), e);
    }

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
