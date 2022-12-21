package com.github.relucent.base.common.crypto.asymmetric;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.github.relucent.base.common.codec.Base64;
import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.io.IoUtil;

/**
 * 非对称加密算法实现抽象类
 */
public abstract class AbstractAsymmetricCrypto<T extends AbstractAsymmetricCrypto<T>> {
    // =================================Fields================================================
    /** 算法名称 */
    protected String algorithm;
    /** 公钥 */
    protected PublicKey publicKey;
    /** 私钥 */
    protected PrivateKey privateKey;

    // =================================Constructors===========================================
    /**
     * 构造函数<br>
     * 私钥和公钥同时为null时，使用随机的密钥对(私钥和公钥)。 私钥和公钥可以只传入一个，只用来做加密或者解密其中一种操作.<br>
     * @param algorithm 算法名称
     * @param privateKey 私钥
     * @param publicKey 公钥
     */
    protected AbstractAsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        initialize(algorithm, privateKey, publicKey);
    }

    // =================================InitializeMethods======================================
    /**
     * 初始化，如果私钥和公钥同时为null，则使用随机生成的密钥对(私钥和公钥)。
     * @param algorithm 算法
     * @param privateKey 私钥
     * @param publicKey 公钥
     */
    protected void initialize(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this.algorithm = algorithm;
        if (privateKey == null && publicKey == null) {
            initializeKeys();
        } else {
            if (privateKey != null) {
                this.privateKey = privateKey;
            }
            if (publicKey != null) {
                this.publicKey = publicKey;
            }
        }
    }

    /**
     * 初始化密钥对(非对称加密的公钥和私钥)<br>
     */
    protected void initializeKeys() {
        KeyPair keyPair = KeyUtil.generateKeyPair(algorithm);
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    // =================================EncryptMethods=========================================
    /**
     * 加密数据
     * @param input 被加密的数据
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据
     */
    public abstract byte[] encrypt(byte[] input, KeyType keyType);

    /**
     * 加密数据，返回16进制字符串
     * @param input 被加密的数据
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据(16进制字符串)
     */
    public String encryptHex(byte[] input, KeyType keyType) {
        return Hex.encodeHexString(encrypt(input, keyType));
    }

    /**
     * 加密数据，返回Base64字符串
     * @param input 被加密的数据
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据(Base64字符串)
     */
    public String encryptBase64(byte[] input, KeyType keyType) {
        return Base64.encode(encrypt(input, keyType));
    }

    /**
     * 加密字符串
     * @param input 被加密的字符串
     * @param charset 字符串编码
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据
     */
    public byte[] encrypt(String input, Charset charset, KeyType keyType) {
        return encrypt(input.getBytes(charset), keyType);
    }

    /**
     * 加密字符串
     * @param input 被加密的字符串
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据
     */
    public byte[] encrypt(String input, KeyType keyType) {
        return encrypt(input.getBytes(StandardCharsets.UTF_8), keyType);
    }

    /**
     * 加密字符串，返回16进制字符串
     * @param input 被加密的字符串
     * @param charset 字符串编码
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据(16进制字符串)
     */
    public String encryptHex(String input, Charset charset, KeyType keyType) {
        return Hex.encodeHexString(encrypt(input, charset, keyType));
    }

    /**
     * 加密字符串，返回16进制字符串
     * @param input 被加密的字符串
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据(16进制字符串)
     */
    public String encryptHex(String input, KeyType keyType) {
        return Hex.encodeHexString(encrypt(input, keyType));
    }

    /**
     * 加密字符串，返回Base64字符串
     * @param input 被加密的字符串
     * @param charset 字符串编码
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据(16进制字符串)
     */
    public String encryptBase64(String input, Charset charset, KeyType keyType) {
        return Base64.encode(encrypt(input, charset, keyType));
    }

    /**
     * 加密字符串，返回Base64字符串
     * @param input 被加密的字符串
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据(16进制字符串)
     */
    public String encryptBase64(String input, KeyType keyType) {
        return Base64.encode(encrypt(input, keyType));
    }

    /**
     * 加密数据，不会关闭流
     * @param input 被加密的数据流
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据
     * @throws IOException 出现IO异常
     */
    public byte[] encrypt(InputStream input, KeyType keyType) throws IOException {
        return encrypt(IoUtil.toByteArray(input), keyType);
    }

    // =================================DecryptMethods=========================================
    /**
     * 解密数据
     * @param input 被解密的数据
     * @param keyType 密钥类型(私钥或公钥)
     * @return 解密后的数据
     */
    public abstract byte[] decrypt(byte[] input, KeyType keyType);

    /**
     * 解密数据，密文为16进制字符串
     * @param input 被解密的数据(16进制字符串)
     * @param keyType 密钥类型(私钥或公钥)
     * @return 解密后的数据
     */
    public byte[] decryptHex(String input, KeyType keyType) {
        return decrypt(Hex.decodeHex(input), keyType);
    }

    /**
     * 解密数据为字符串，密文为16进制字符串
     * @param input 被解密的数据(16进制字符串)
     * @param keyType 密钥类型
     * @param charset 加密前的字符串编码
     * @return 解密后的数据
     */
    public String decryptHexString(String input, KeyType keyType, Charset charset) {
        return new String(decryptHex(input, keyType), charset);
    }

    /**
     * 解密数据为字符串，密文为16进制字符串
     * @param input 被解密的数据(16进制字符串)
     * @param keyType 密钥类型
     * @return 解密后的数据
     */
    public String decryptHexString(String input, KeyType keyType) {
        return decryptHexString(input, keyType, StandardCharsets.UTF_8);
    }

    /**
     * 解密数据，密文为Base64字符串
     * @param input 被解密的数据(Base64字符串)
     * @param keyType 密钥类型(私钥或公钥)
     * @return 解密后的数据
     */
    public byte[] decryptBase64(String input, KeyType keyType) {
        return decrypt(Base64.decode(input), keyType);
    }

    /**
     * 解密数据为字符串，密文为Base64字符串
     * @param input 被解密的数据(Base64字符串)
     * @param keyType 密钥类型(私钥或公钥)
     * @param charset 加密前的字符串编码
     * @return 解密后的数据
     */
    public String decryptBase64String(String input, KeyType keyType, Charset charset) {
        return new String(decryptBase64(input, keyType), charset);
    }

    /**
     * 解密数据为字符串，密文为Base64字符串
     * @param input 被解密的数据(Base64字符串)
     * @param keyType 密钥类型(私钥或公钥)
     * @return 解密后的数据
     */
    public String decryptBase64String(String input, KeyType keyType) {
        return decryptBase64String(input, keyType, StandardCharsets.UTF_8);
    }

    /**
     * 解密数据，不会关闭流
     * @param input 被解密的数据流
     * @param keyType 密钥类型(私钥或公钥)
     * @return 解密后的数据
     * @throws IOException 出现IO异常
     */
    public byte[] decrypt(InputStream input, KeyType keyType) throws IOException {
        return decrypt(IoUtil.toByteArray(input), keyType);
    }

    // =================================SetMethods=============================================
    /**
     * 设置私钥
     * @param privateKey 私钥
     * @return this
     */
    @SuppressWarnings("unchecked")
    public T setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
        return (T) this;
    }

    /**
     * 设置公钥
     * @param publicKey 公钥
     * @return this
     */
    @SuppressWarnings("unchecked")
    public T setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return (T) this;
    }

    // =================================GetMethods=============================================
    /**
     * 返回算法名称(字符串表示)
     * @return 算法名称
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * 获得公钥
     * @return 获得公钥
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * 获得私钥
     * @return 获得私钥
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * 根据密钥类型获得相应密钥
     * @param keyType 密钥类型(私钥或公钥)
     * @return 相应类型的密钥
     */
    public Key getKey(KeyType keyType) {
        return KeyType.PRIVATE.equals(keyType) ? privateKey : publicKey;
    }
}
