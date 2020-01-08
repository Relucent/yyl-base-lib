package com.github.relucent.base.common.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class CipherUtil {

    /**
     * 创建加密解密器{@link Cipher}
     * @param algorithm 算法
     * @return 加密解密器
     */
    public static Cipher createCipher(String algorithm) {
        try {
            Cipher cipher = null;
            Provider provider = ProviderFactory.getProvider();
            if (provider == null) {
                cipher = Cipher.getInstance(algorithm);
            } else {
                cipher = Cipher.getInstance(algorithm, provider);
            }
            return cipher;
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }
}
