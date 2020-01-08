package com.github.relucent.base.common.crypto.symmetric;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Data Encryption Standard(数据加密标准)算法实现<br>
 * Java中的默认模式是：DES/CBC/PKCS5Padding<br>
 */
public class Des extends SymmetricCrypto {

    // =================================Fields================================================
    private static final String ALGORITHM_PREFIX = "DES";

    // =================================Constructors===========================================
    /**
     * 构造函数，使用默认的DES/CBC/PKCS5Padding算法
     * @param secretKey 秘密(对称)密钥，如果为null，表示使用随机密钥
     */
    protected Des(SecretKey secretKey) {
        super(SymmetricAlgorithm.DES, secretKey);
    }

    /**
     * 构造函数，使用默认的DES/CBC/PKCS5Padding算法
     * @param key 密钥数据，长度为8个字节；如果为null，表示使用随机密钥
     */
    protected Des(byte[] key) {
        super(SymmetricAlgorithm.DES, key);
    }

    /**
     * 构造函数
     * @param mode 模式{@link Mode}
     * @param padding {@link Padding}补码方式
     * @param secretKey 秘密(对称)密钥
     * @param parameterSpec 算法参数(偏移向量,加盐)
     */
    protected Des(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        super(ALGORITHM_PREFIX + "/" + mode.name() + "/" + padding.name(), key, iv);
    }

    // =================================CreateMethods==========================================
    /**
     * 创建DES实例，使用默认的DES/CBC/PKCS5Padding算法，随机秘钥
     * @return DES实例
     */
    public static Des create() {
        return new Des((SecretKey) null);
    }

    /**
     * 创建DES实例，使用默认的DES/CBC/PKCS5Padding算法
     * @param secretKey 秘密(对称)密钥
     * @return DES实例
     */
    public static Des create(SecretKey secretKey) {
        return new Des(secretKey);
    }

    /**
     * 创建DES实例，使用默认的DES/CBC/PKCS5Padding算法
     * @param 密钥数据，长度为8个字节；如果为null，表示使用随机密钥
     * @return DES实例
     */
    public static Des create(byte[] key) {
        return new Des(key);
    }

    /**
     * 创建DES实例
     * @param mode 模式 {@link Mode}
     * @param padding {@link Padding}补码方式
     * @param secretKey 秘密(对称)密钥
     * @return DES实例
     */
    public static Des create(Mode mode, Padding padding, SecretKey secretKey) {
        return create(mode, padding, secretKey, null);
    }

    /**
     * 创建DES实例
     * @param mode 模式 {@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key 密钥数据，长度为8个字节；如果为null，表示使用随机密钥
     * @return DES实例
     */
    public static Des create(Mode mode, Padding padding, byte[] key) {
        return create(mode, padding, key, null);
    }

    /**
     * 创建DES实例
     * @param mode 模式 {@link Mode}
     * @param padding {@link Padding}补码方式
     * @param key 密钥数据，长度为8个字节；如果为null，表示使用随机密钥
     * @param iv 偏移向量(加盐)
     * @return DES实例
     */
    public static Des create(Mode mode, Padding padding, byte[] key, byte[] iv) {
        SecretKey secretKey = key == null ? null : SecretKeyUtil.generateSecretKey(ALGORITHM_PREFIX, key);
        IvParameterSpec paramsSpec = iv == null ? null : new IvParameterSpec(iv);
        return create(mode, padding, secretKey, paramsSpec);
    }

    /**
     * 创建DES实例
     * @param mode 模式 {@link Mode}
     * @param padding {@link Padding}补码方式
     * @param secretKey 秘密(对称)密钥
     * @param paramsSpec 加密参数的(偏移向量,加盐)
     * @return DES实例
     */
    public static Des create(Mode mode, Padding padding, SecretKey secretKey, IvParameterSpec paramsSpec) {
        return new Des(mode, padding, secretKey, paramsSpec);
    }

    // =================================Methods================================================
    /**
     * 设置偏移向量
     * @param iv 偏移向量(加盐)
     * @return this
     */
    public Des setIvParameter(byte[] iv) {
        setParameter(new IvParameterSpec(iv));
        return this;
    }
}
