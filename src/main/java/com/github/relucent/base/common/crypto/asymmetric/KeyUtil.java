package com.github.relucent.base.common.crypto.asymmetric;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.KeySpec;

import com.github.relucent.base.common.constants.CharConstants;
import com.github.relucent.base.common.crypto.CryptoException;
import com.github.relucent.base.common.crypto.ProviderFactory;

/**
 * 密钥(非对称加密的公钥和私钥)工具类
 */
public class KeyUtil {

    // =================================Fields================================================
    /** 主算法名称 */
    private static class PrefixNames {
        /** EC算法名称 */
        private static final String EC = "EC";
        /** ECDSA算法名称 */
        private static final String ECDSA = "ECDSA";
        /** SM2算法名称 */
        private static final String SM2 = "SM2";
    }

    /** 默认密钥字节数 */
    private static final int DEFAULT_KEY_SIZE = 1024;

    /** SM2默认曲线 */
    private static final String SM2_DEFAULT_CURVE = "sm2p256v1";

    // =================================Methods================================================
    /**
     * 获取密钥对生成器{@link KeyPairGenerator}
     * @param algorithm 非对称加密算法
     * @return 密钥对生成器
     */
    public static KeyPairGenerator getKeyPairGenerator(String algorithm) {
        KeyPairGenerator keyPairGenerator;
        try {
            Provider provider = ProviderFactory.getProvider();
            if (provider == null) {
                keyPairGenerator = KeyPairGenerator.getInstance(getMainAlgorithm(algorithm));
            } else {
                keyPairGenerator = KeyPairGenerator.getInstance(getMainAlgorithm(algorithm), provider);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return keyPairGenerator;
    }

    /**
     * 获取密钥(非对称加密密钥)工厂{@link KeyFactory}
     * @param algorithm 非对称加密算法
     * @return 密钥(非对称加密密钥)工厂
     */
    public static KeyFactory getKeyFactory(String algorithm) {
        KeyFactory keyFactory;
        try {
            Provider provider = ProviderFactory.getProvider();
            if (provider == null) {
                keyFactory = KeyFactory.getInstance(getMainAlgorithm(algorithm));
            } else {
                keyFactory = KeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        return keyFactory;
    }

    /**
     * 生成密钥对（非对称加密的公钥和私钥）<br>
     * 对于非对称加密算法，密钥长度有严格限制，具体如下：
     * 
     * <pre>
     * RSA：
     * RS256、PS256：2048 bits
     * RS384、PS384：3072 bits
     * RS512、RS512：4096 bits
     *
     * EC（Elliptic Curve）：
     * EC256：256 bits
     * EC384：384 bits
     * EC512：512 bits
     * </pre>
     *
     * @param algorithm 非对称加密算法
     * @param keySize 密钥模数长度(单位bit)
     * @param random 随机数生成器
     * @param params 加密参数的（透明）规范
     * @return 生成密钥对（非对称加密的公钥和私钥）
     */
    public static KeyPair generateKeyPair(String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec params) {
        algorithm = getMainAlgorithm(algorithm);
        KeyPairGenerator keyPairGenerator = getKeyPairGenerator(algorithm);
        // 密钥模长度初始化定义
        if (keySize > 0) {
            // EC算法，密钥长度有限制
            if (PrefixNames.EC.equalsIgnoreCase(algorithm)) {
                keySize = Math.min(keySize, 256);
            }
            // 随机数生成器
            if (random != null) {
                keyPairGenerator.initialize(keySize, random);
            } else {
                keyPairGenerator.initialize(keySize);
            }
        }
        if (params != null) {
            try {
                if (null != random) {
                    keyPairGenerator.initialize(params, random);
                } else {
                    keyPairGenerator.initialize(params);
                }
            } catch (InvalidAlgorithmParameterException e) {
                throw new CryptoException(e);
            }
        }
        return keyPairGenerator.generateKeyPair();
    }


    /**
     * 生成用于密钥对(非对称加密的公钥和私钥)<br>
     * @param algorithm 非对称加密算法
     * @return 密钥对
     */
    public static KeyPair generateKeyPair(String algorithm) {
        return generateKeyPair(algorithm, DEFAULT_KEY_SIZE);
    }

    /**
     * 生成用于密钥对(非对称加密的公钥和私钥)<br>
     * @param algorithm 非对称加密算法
     * @param keySize 密钥模长度
     * @return 密钥对
     */
    public static KeyPair generateKeyPair(String algorithm, int keySize) {
        return generateKeyPair(algorithm, keySize, null);
    }

    /**
     * 生成用于密钥对(非对称加密的公钥和私钥)<br>
     * @param algorithm 非对称加密算法
     * @param keySize 密钥模长度
     * @param seed 种子
     * @return 密钥对
     */
    public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed) {
        algorithm = getMainAlgorithm(algorithm);
        // SM2算法需要单独定义其曲线生成
        if (PrefixNames.SM2.equalsIgnoreCase(algorithm)) {
            return generateKeyPair(algorithm, keySize, seed, new ECGenParameterSpec(SM2_DEFAULT_CURVE));
        }
        return generateKeyPair(algorithm, keySize, seed, (AlgorithmParameterSpec) null);
    }

    /**
     * 生成密钥对（非对称加密的公钥和私钥）<br>
     * @param algorithm 非对称加密算法
     * @param keySize 密钥模数长度(单位bit)
     * @param seed 随机数种子
     * @param params 加密参数的（透明）规范
     * @return 生成密钥对（非对称加密的公钥和私钥）
     */
    public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed, AlgorithmParameterSpec params) {
        SecureRandom random = (null == seed) ? new SecureRandom() : new SecureRandom(seed);
        return generateKeyPair(algorithm, keySize, random, params);
    }

    /**
     * 生成私钥<br>
     * @param algorithm 算法
     * @param keySpec 组成加密密钥的密钥内容的（透明）规范
     * @return 私钥 {@link PrivateKey}
     */
    public static PrivateKey generatePrivateKey(String algorithm, KeySpec keySpec) {
        if (null == keySpec) {
            return null;
        }
        algorithm = getMainAlgorithm(algorithm);
        try {
            return getKeyFactory(algorithm).generatePrivate(keySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 生成公钥<br>
     * @param algorithm 算法
     * @param keySpec 组成加密密钥的密钥内容的（透明）规范
     * @return 公钥 {@link PublicKey}
     */
    public static PublicKey generatePublicKey(String algorithm, KeySpec keySpec) {
        if (null == keySpec) {
            return null;
        }
        algorithm = getMainAlgorithm(algorithm);
        try {
            return getKeyFactory(algorithm).generatePublic(keySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }


    /**
     * 获取主体算法名<br>
     * 例如：<br>
     * 加密算法：RSA/ECB/PKCS1Padding 的主体算法是RSA<br>
     * 签名算法：NONEwithECDSA 的主体算法是ECDSA<br>
     * @param algorithm 对称加密算法名称
     * @return 主体算法名
     */
    private static String getMainAlgorithm(String algorithm) {
        // 获取加密算法第主体算法名称
        int slashIndex = algorithm.indexOf(CharConstants.SLASH);
        if (slashIndex > 0) {
            algorithm = algorithm.substring(0, slashIndex);
        }
        // 获取签名算法XXXwithXXX算法的后半部分算法
        int indexOfWith = algorithm.indexOf("with");
        if (indexOfWith > 0) {
            algorithm = algorithm.substring(indexOfWith + "with".length());
        }
        // 如果ECDSA或SM2，返回算法为EC
        if (PrefixNames.ECDSA.equalsIgnoreCase(algorithm) || PrefixNames.SM2.equalsIgnoreCase(algorithm)) {
            algorithm = PrefixNames.EC;
        }
        return algorithm;
    }
}
