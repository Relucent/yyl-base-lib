package com.github.relucent.base.common.crypto.symmetric;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.github.relucent.base.common.crypto.ModeEnum;
import com.github.relucent.base.common.crypto.PaddingEnum;

/**
 * Advanced Encryption Standard（高级加密标准）加密算法实现<br>
 * Java中AES的默认模式是：AES/ECB/PKCS5Padding<br>
 */
public class Aes extends SymmetricCrypto {

    // =================================Fields================================================
    private static final String ALGORITHM_PREFIX = "AES";

    // =================================Constructors===========================================
    /**
     * 构造函数，使用的AES/ECB/PKCS5Padding算法
     * @param secretKey 秘密(对称)密钥，如果为null，表示使用随机密钥
     */
    protected Aes(SecretKey secretKey) {
        super(SymmetricAlgorithmEnum.AES, secretKey);
    }

    /**
     * 构造函数，使用的AES/ECB/PKCS5Padding算法
     * @param key 密钥，支持三种密钥长度：128(16字节)、192(24字节)、256位(32字节)；如果为null，表示使用随机密钥
     */
    protected Aes(byte[] key) {
        super(SymmetricAlgorithmEnum.AES, key);
    }

    /**
     * 构造函数
     * @param mode 模式{@link ModeEnum}
     * @param padding {@link PaddingEnum}补码方式
     * @param secretKey 秘密(对称)密钥
     * @param parameterSpec 算法参数(初始化向量)
     */
    protected Aes(ModeEnum mode, PaddingEnum padding, SecretKey secretKey, IvParameterSpec parameterSpec) {
        super(ALGORITHM_PREFIX + "/" + mode.name() + "/" + padding.name(), secretKey, parameterSpec);
    }

    // =================================CreateMethods==========================================
    /**
     * 创建AES实例，使用AES/ECB/PKCS5Padding算法，随机秘钥
     * @return AES实例
     */
    public static Aes create() {
        return new Aes((SecretKey) null);
    }

    /**
     * 创建AES实例，使用AES/ECB/PKCS5Padding算法
     * @param secretKey 秘密(对称)密钥
     * @return AES实例
     */
    public static Aes create(SecretKey secretKey) {
        return new Aes(secretKey);
    }

    /**
     * 创建AES实例，使用AES/ECB/PKCS5Padding算法
     * @param key 密钥，支持三种密钥长度：128(16字节)、192(24字节)、256位(32字节)；如果为null，表示使用随机密钥
     * @return AES实例
     */
    public static Aes create(byte[] key) {
        return new Aes(key);
    }

    /**
     * 创建AES实例
     * @param mode 模式 {@link ModeEnum}
     * @param padding {@link PaddingEnum}补码方式
     * @param secretKey 秘密(对称)密钥
     * @return AES实例
     */
    public static Aes create(ModeEnum mode, PaddingEnum padding, SecretKey secretKey) {
        return create(mode, padding, secretKey, null);
    }

    /**
     * 创建AES实例
     * @param mode 模式 {@link ModeEnum}
     * @param padding {@link PaddingEnum}补码方式
     * @param key 密钥，支持三种密钥长度：128(16字节)、192(24字节)、256位(32字节)；如果为null，表示使用随机密钥
     * @return AES实例
     */
    public static Aes create(ModeEnum mode, PaddingEnum padding, byte[] key) {
        return create(mode, padding, key, null);
    }

    /**
     * 创建AES实例
     * @param mode 模式 {@link ModeEnum}
     * @param padding {@link PaddingEnum}补码方式
     * @param key 密钥，支持三种密钥长度：128(16字节)、192(24字节)、256位(32字节)；如果为null，表示使用随机密钥
     * @param iv 偏移向量(加盐)
     * @return AES实例
     */
    public static Aes create(ModeEnum mode, PaddingEnum padding, byte[] key, byte[] iv) {
        SecretKey secretKey = key == null ? null : SecretKeyUtil.generateSecretKey(ALGORITHM_PREFIX, key);
        IvParameterSpec paramsSpec = iv == null ? null : new IvParameterSpec(iv);
        return create(mode, padding, secretKey, paramsSpec);
    }

    /**
     * 创建AES实例
     * @param mode 模式 {@link ModeEnum}
     * @param padding {@link PaddingEnum}补码方式
     * @param secretKey 秘密(对称)密钥
     * @param paramsSpec 加密参数的(初始化向量)
     * @return AES实例
     */
    public static Aes create(ModeEnum mode, PaddingEnum padding, SecretKey secretKey, IvParameterSpec paramsSpec) {
        return new Aes(mode, padding, secretKey, paramsSpec);
    }

    // =================================Methods================================================
    /**
     * 设置偏移向量
     * @param iv 偏移向量(加盐)
     * @return this
     */
    public Aes setIvParameter(byte[] iv) {
        setParameter(new IvParameterSpec(iv));
        return this;
    }
}
