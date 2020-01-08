package com.github.relucent.base.common.crypto.symmetric;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * DESede是由DES对称加密算法改进后的一种对称加密算法，又名3DES、TripleDES。<br>
 * 它使用 168 位的密钥对资料进行三次加密，提供极更强的安全性。<br>
 * 如果三个 56 位的子元素都相同，则三重 DES 向后兼容 DES。<br>
 * Java中默认实现为：DESede/ECB/PKCS5Padding
 */
public class Desede extends SymmetricCrypto {

    // =================================Fields================================================
    private static final String ALGORITHM_PREFIX = "DESede";

    // =================================Constructors===========================================
    /**
     * 构造函数，使用默认的DESede/ECB/PKCS5Padding算法
     * @param secretKey 秘密(对称)密钥，如果为null，表示使用随机密钥
     */
    protected Desede(SecretKey secretKey) {
        super(SymmetricAlgorithm.DESede, secretKey);
    }

    /**
     * 构造函数，使用默认的DESede/ECB/PKCS5Padding算法
     * @param key 密钥数据，长度24个字节；如果为null，表示使用随机密钥
     */
    protected Desede(byte[] key) {
        super(SymmetricAlgorithm.DESede, key);
    }

    /**
     * 构造函数
     * @param mode 模式{@link Mode}
     * @param padding {@link Padding}补码方式
     * @param secretKey 秘密(对称)密钥
     * @param parameterSpec 算法参数(偏移向量,加盐)
     */
    protected Desede(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        super(ALGORITHM_PREFIX + "/" + mode.name() + "/" + padding.name(), key, iv);
    }

    // =================================CreateMethods==========================================
    /**
     * 创建DESede实例，使用默认的DESede/ECB/PKCS5Padding算法，随机秘钥
     * @return DESede实例
     */
    public static Desede create() {
        return new Desede((SecretKey) null);
    }

    /**
     * 创建DESede实例，使用默认的DESede/ECB/PKCS5Padding算法
     * @param secretKey 秘密(对称)密钥
     * @return DESede实例
     */
    public static Desede create(SecretKey secretKey) {
        return new Desede(secretKey);
    }

    /**
     * 创建DESede实例，使用默认的DESede/ECB/PKCS5Padding算法
     * @param 密钥数据，长度24个字节；如果为null，表示使用随机密钥
     * @return DESede实例
     */
    public static Desede create(byte[] key) {
        return new Desede(key);
    }

    /**
     * 创建DESede实例
     * @param mode 模式 {@link Mode}
     * @param padding {@link Padding}补码方式
     * @param secretKey 秘密(对称)密钥
     * @return DESede实例
     */
    public static Desede create(Mode mode, Padding padding, SecretKey secretKey) {
        return create(mode, padding, secretKey, null);
    }

    /**
     * 创建DESede实例
     * @param mode 模式 {@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key 密钥数据，长度24个字节；如果为null，表示使用随机密钥
     * @return DESede实例
     */
    public static Desede create(Mode mode, Padding padding, byte[] key) {
        return create(mode, padding, key, null);
    }

    /**
     * 创建DESede实例
     * @param mode 模式 {@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key 密钥数据，长度24个字节；如果为null，表示使用随机密钥
     * @param iv 偏移向量(加盐)
     * @return DESede实例
     */
    public static Desede create(Mode mode, Padding padding, byte[] key, byte[] iv) {
        SecretKey secretKey = key == null ? null : SecretKeyUtil.generateKey(ALGORITHM_PREFIX, key);
        IvParameterSpec paramsSpec = iv == null ? null : new IvParameterSpec(iv);
        return create(mode, padding, secretKey, paramsSpec);
    }

    /**
     * 创建DESede实例
     * @param mode 模式 {@link Mode}
     * @param padding {@link Padding}补码方式
     * @param secretKey 秘密(对称)密钥
     * @param paramsSpec 加密参数的(偏移向量,加盐)
     * @return DESede实例
     */
    public static Desede create(Mode mode, Padding padding, SecretKey secretKey, IvParameterSpec paramsSpec) {
        return new Desede(mode, padding, secretKey, paramsSpec);
    }

    // =================================Methods================================================
    /**
     * 设置偏移向量
     * @param iv 偏移向量(加盐)
     * @return this
     */
    public Desede setIvParameter(byte[] iv) {
        setParameter(new IvParameterSpec(iv));
        return this;
    }
}
