package com.github.relucent.base.common.crypto.symmetric;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.github.relucent.base.common.constants.CharConstants;
import com.github.relucent.base.common.crypto.CryptoException;
import com.github.relucent.base.common.crypto.ProviderFactory;

/**
 * 秘密(对称)密钥工具类
 */
public class SecretKeyUtil {

    // =================================Fields================================================
    /** Java密钥库(Java Key Store，JKS)存储名 */
    public static final String KEY_STORE = "JKS";
    /** X509证书名 */
    public static final String X509 = "X.509";

    /** 默认密钥字节数 */
    public static final int DEFAULT_KEY_SIZE = 1024;

    /** SM2默认曲线 */
    public static final String SM2_DEFAULT_CURVE = "sm2p256v1";

    /** 随机字符串可选字符 */
    private static final char[] RANDOM_CHARS = {//
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', //
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', //
            'u', 'v', 'w', 'x', 'y', 'z'//
    };

    // =================================Methods================================================
    /**
     * 获取（对称）密钥生成器{@link KeyGenerator}
     * @param algorithm 对称加密算法
     * @return （对称）密钥生成器
     */
    public static KeyGenerator getKeyGenerator(String algorithm) {
        KeyGenerator keyGenerator;
        try {
            Provider provider = ProviderFactory.getProvider();
            if (provider == null) {
                keyGenerator = KeyGenerator.getInstance(getMainAlgorithm(algorithm));
            } else {
                keyGenerator = KeyGenerator.getInstance(getMainAlgorithm(algorithm), provider);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return keyGenerator;
    }

    /**
     * 获取秘密(对称)密钥工厂{@link SecretKeyFactory}
     * @param algorithm 对称加密算法
     * @return 秘密(对称)密钥工厂
     */
    public static SecretKeyFactory getSecretKeyFactory(String algorithm) {
        SecretKeyFactory secretKeyFactory;
        try {
            Provider provider = ProviderFactory.getProvider();
            if (provider == null) {
                secretKeyFactory = SecretKeyFactory.getInstance(getMainAlgorithm(algorithm));
            } else {
                secretKeyFactory = SecretKeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return secretKeyFactory;
    }

    /**
     * 生成秘密（对称）密钥{@link SecretKey}
     * @param algorithm 对称加密算法
     * @param keySpec 密钥内容规范 {@link KeySpec}
     * @return 秘密（对称）密钥
     */
    public static SecretKey generateKey(String algorithm, KeySpec keySpec) {
        final SecretKeyFactory secretKeyFactory = getSecretKeyFactory(algorithm);
        try {
            return secretKeyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 生成PBE(密码的加密法)密钥 {@link SecretKey}
     * @param algorithm PBE算法，包括：PBEWithMD5AndDES、PBEWithSHA1AndDESede、PBEWithSHA1AndRC2_40等
     * @param password 密码，如果为{@code null}则使用随机密码
     * @return 秘密（对称）密钥
     */
    public static SecretKey generatePBEKey(String algorithm, char[] password) {
        // 如果不是PBE算法，则抛出异常
        if (algorithm == null || !algorithm.startsWith("PBE")) {
            throw new CryptoException("Algorithm [" + algorithm + "] is not a PBE algorithm!");
        }
        // 密码为空，生成随机密码
        if (password == null) {
            password = randomString(32).toCharArray();
        }
        PBEKeySpec keySpec = new PBEKeySpec(password);
        return generateKey(algorithm, keySpec);
    }

    /**
     * 生成 DES(数据加密标准Data Encryption Standard)密钥 ，使用 key中的前 8 个字节作为 DES密钥的密钥内容。
     * @param algorithm DES算法，包括DES、DESede等
     * @param key 具有DES密钥内容的缓冲区
     * @return 秘密（对称）密钥
     */
    public static SecretKey generateDESKey(String algorithm, byte[] key) {
        if (algorithm == null || !algorithm.startsWith("DES")) {
            throw new CryptoException("Algorithm [" + algorithm + "] is not a DES algorithm!");
        }
        if (key == null) {
            KeyGenerator keyGenerator = getKeyGenerator(algorithm);
            return keyGenerator.generateKey();
        }
        KeySpec keySpec;
        try {
            if (algorithm.startsWith("DESede")) {
                keySpec = new DESedeKeySpec(key);
            } else {
                keySpec = new DESKeySpec(key);
            }
        } catch (InvalidKeyException e) {
            throw new CryptoException(e);
        }
        return generateKey(algorithm, keySpec);
    }

    /**
     * 生成随机秘钥 {@link SecretKey}
     * @param algorithm 对称加密算法
     * @return 秘密（对称）密钥
     */
    public static SecretKey generateRandomKey(String algorithm) {
        return generateRandomKey(algorithm, -1);
    }

    /**
     * 生成随机秘钥 {@link SecretKey}
     * @param algorithm 对称加密算法
     * @param keySize 密钥长度，-1表示不指定
     * @return 秘密（对称）密钥
     */
    public static SecretKey generateRandomKey(String algorithm, int keySize) {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        if (keySize > 0) {
            keyGenerator.init(keySize);
        } else if (algorithm.startsWith("AES")) {
            // AES算法，默认使用128位
            keyGenerator.init(128);
        }
        return keyGenerator.generateKey();
    }

    /**
     * 生成对称加密的秘钥 {@link SecretKey}
     * @param algorithm 对称加密算法
     * @param key 密钥内容
     * @return 秘密（对称）密钥
     */
    public static SecretKey generateKey(String algorithm, byte[] key) {
        algorithm = getMainAlgorithm(algorithm);
        // PBE密钥
        if (algorithm.startsWith("PBE")) {
            char[] password = key == null ? null : new String(key, StandardCharsets.UTF_8).toCharArray();
            return generatePBEKey(algorithm, password);
        }

        // DES密钥
        if (algorithm.startsWith("DES")) {
            return generateDESKey(algorithm, key);
        }
        // 其它算法密钥，随机秘钥
        if (key == null) {
            return generateRandomKey(algorithm);
        }
        // 其它算法密钥，使用密钥内容生成密钥
        return new SecretKeySpec(key, algorithm);
    }

    /**
     * 获取主体算法名，例如AES/CBC/PKCS5Padding的主体算法是AES
     * @param algorithm 加密算法名称
     * @return 主体算法名
     */
    private static String getMainAlgorithm(String algorithm) {
        int slashIndex = algorithm.indexOf(CharConstants.SLASH);
        if (slashIndex > 0) {
            return algorithm.substring(0, slashIndex);
        }
        return algorithm;
    }

    /**
     * 获得一个随机的字符串
     * @param length 字符串的长度
     * @return 随机字符串
     */
    private static String randomString(int length) {
        if (length < 1) {
            length = 1;
        }
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(RANDOM_CHARS[(int) (Math.random() * RANDOM_CHARS.length)]);
        }
        return builder.toString();
    }
}
