package com.github.relucent.base.common.crypto.symmetric;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEParameterSpec;

import com.github.relucent.base.common.codec.Base64;
import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.crypto.CipherUtil;
import com.github.relucent.base.common.crypto.CryptoException;
import com.github.relucent.base.common.io.IoUtil;

/**
 * 对称加密算法<br>
 * 对称加密(也叫私钥加密)指加密和解密使用相同密钥的加密算法。<br>
 * 在对称加密算法中，数据发信方将明文（原始数据）和加密密钥一起经过特殊加密算法处理后，使其变成复杂的加密密文发送出去。<br>
 * 收信方收到密文后，若想解读原文，则需要使用加密用过的密钥及相同算法的逆算法对密文进行解密，才能使其恢复成可读明文。<br>
 * 在对称加密算法中，使用的密钥只有一个，发收信双方都使用这个密钥对数据进行加密和解密，这就要求解密方事先必须知道加密密钥。<br>
 * 注意：该类的实例不保证线程安全，应当避免多线程同时调用同一个实例(每个线程使用独立的实例，或者在调用时候增加同步锁)。<br>
 */
public class SymmetricCrypto {

    // =================================Fields================================================
    /** 秘密(对称)密钥 */
    private SecretKey secretKey;
    /** 提供加密和解密功能 */
    private Cipher cipher;
    /** 算法参数 */
    private AlgorithmParameterSpec params;

    // =================================Constructors===========================================
    /**
     * 构造函数，使用随机密钥
     * @param algorithm 算法
     */
    public SymmetricCrypto(SymmetricAlgorithm algorithm) {
        this(algorithm, null, null);
    }

    /**
     * 构造函数
     * @param algorithm 算法
     * @param key 密钥数据
     */
    public SymmetricCrypto(SymmetricAlgorithm algorithm, byte[] key) {
        this(algorithm, SecretKeyUtil.generateSecretKey(algorithm.getValue(), key), null);
    }

    /**
     * 构造函数
     * @param algorithm 算法
     * @param secretKey 密钥数据
     */
    public SymmetricCrypto(SymmetricAlgorithm algorithm, SecretKey secretKey) {
        this(algorithm, secretKey, null);
    }

    /**
     * 构造
     * @param algorithm 算法
     * @param secretKey 秘密(对称)密钥
     * @param params 算法参数
     */
    public SymmetricCrypto(SymmetricAlgorithm algorithm, SecretKey secretKey, AlgorithmParameterSpec params) {
        this(algorithm.getValue(), secretKey, params);
    }

    /**
     * 构造函数
     * @param algorithm 算法
     * @param secretKey 秘密(对称)密钥
     * @param params 算法参数
     */
    protected SymmetricCrypto(String algorithm, SecretKey secretKey, AlgorithmParameterSpec params) {
        initialize(algorithm, secretKey, params);
    }

    // =================================InitializeMethods======================================
    /**
     * 初始化
     * @param algorithm 算法
     * @param secretKey 秘密(对称)密钥
     * @param params 算法参数
     */
    protected void initialize(String algorithm, SecretKey secretKey, AlgorithmParameterSpec params) {
        // 如果密钥为null，那么生成一个随机密钥
        if (secretKey == null) {
            secretKey = generateKey(algorithm);
        }
        this.secretKey = secretKey;
        // 对于PBE算法使用随机数加盐
        if (params == null && algorithm.startsWith("PBE")) {
            byte[] bytes = new byte[8];
            ThreadLocalRandom.current().nextBytes(bytes);
            params = new PBEParameterSpec(bytes, 100);
        }
        this.params = params;
        this.cipher = createCipher(algorithm);
    }

    /**
     * 生成用于秘密(对称)秘钥<br>
     * @param algorithm 算法名称
     * @return 秘密(对称)秘钥
     */
    protected SecretKey generateKey(String algorithm) {
        return SecretKeyUtil.generateSecretKey(algorithm);
    }

    /**
     * 初始化加密解密器 {@link Cipher}
     * @param algorithm 算法名称
     * @return 加密解密器
     */
    protected Cipher createCipher(String algorithm) {
        return CipherUtil.createCipher(algorithm);
    }

    // =================================Methods================================================
    /**
     * 加密数据
     * @param input 被加密的数据
     * @return 加密后的数据
     */
    public byte[] encrypt(byte[] input) {
        try {
            initCipher(Cipher.ENCRYPT_MODE);
            return cipher.doFinal(input);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 加密数据
     * @param input 被加密的字符串
     * @param charset 字符串编码
     * @return 加密后的数据
     */
    public byte[] encrypt(String input, Charset charset) {
        return encrypt(input.getBytes(charset));
    }

    /**
     * 加密数据(使用UTF-8编码)
     * @param input 被加密的字符串
     * @return 加密后的数据
     */
    public byte[] encrypt(String input) {
        return encrypt(input, StandardCharsets.UTF_8);
    }



    /**
     * 加密数据，返回十六进制字符串
     * @param input 被加密的数据
     * @return 加密后的十六进制字符串
     */
    public String encryptHex(byte[] input) {
        return Hex.encodeHexString(encrypt(input));
    }

    /**
     * 加密数据，返回十六进制字符串
     * @param input 被加密的字符串
     * @param charset 字符串编码
     * @return 加密后的十六进制字符串
     */
    public String encryptHex(String input, Charset charset) {
        return encryptHex(input.getBytes(charset));
    }

    /**
     * 加密数据(使用UTF-8编码)，返回十六进制字符串
     * @param data 被加密的字符串
     * @return 加密后的Hex
     */
    public String encryptHex(String input) {
        return encryptHex(input, StandardCharsets.UTF_8);
    }

    /**
     * 加密数据，返回Base64字符串
     * @param input 被加密的数据
     * @return 加密后的Base64字符串
     */
    public String encryptBase64(byte[] input) {
        return Base64.encode(encrypt(input));
    }

    /**
     * 加密数据，返回Base64字符串
     * @param input 被加密的字符串
     * @param charset 字符串编码
     * @return 加密后的Base64字符串
     */
    public String encryptBase64(String input, Charset charset) {
        return encryptBase64(input.getBytes(charset));
    }

    /**
     * 加密数据(使用UTF-8编码)，返回的Base64字符串
     * @param input 被加密的字符串
     * @return 加密后的Base64
     */
    public String encryptBase64(String input) {
        return encryptBase64(input, StandardCharsets.UTF_8);
    }

    /**
     * 加密数据
     * @param input 被解密的数据(输入流)
     * @return 加密后的数据
     * @throws IOException 出现IO异常
     */
    public byte[] encrypt(InputStream input) throws IOException {
        return encrypt(IoUtil.toByteArray(input));
    }

    /**
     * 解密数据
     * @param input 被解密的数据
     * @return 解密后的数据
     */
    public byte[] decrypt(byte[] input) {
        try {
            initCipher(Cipher.DECRYPT_MODE);
            return cipher.doFinal(input);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 解密数据
     * @param input 被解密的数据(16进制字符串格式)
     * @return 解密后的数据
     */
    public byte[] decryptHex(String input) {
        return decrypt(Hex.decodeHex(input.toCharArray()));
    }

    /**
     * 解密数据
     * @param input 被解密的数据(Base64格式)
     * @return 解密后的数据
     */
    public byte[] decryptBase64(String input) {
        return decrypt(Base64.decode(input));
    }

    /**
     * 解密数据为字符串
     * @param input 被解密的数据
     * @param charset 解密后的字符串编码
     * @return 解密后的字符串
     */
    public String decryptString(byte[] input, Charset charset) {
        return new String(decrypt(input), charset);
    }

    /**
     * 解密数据为字符串（字符串编码为UTF-8）
     * @param input 被解密的数据
     * @return 解密后的字符串
     */
    public String decryptString(byte[] input) {
        return new String(decrypt(input), StandardCharsets.UTF_8);
    }

    /**
     * 解密数据为字符串
     * @param input 被解密的数据，格式为16进制字符串
     * @param charset 加密前的字符串编码
     * @return 解密后的数据
     */
    public String decryptHexString(String input, Charset charset) {
        return new String(decryptHex(input), charset);
    }

    /**
     * 解密数据为字符串（字符串编码为UTF-8）
     * @param input 被解密的数据，格式为16进制字符串
     * @return 解密后的数据
     */
    public String decryptHexString(String input) {
        return decryptHexString(input, StandardCharsets.UTF_8);
    }

    /**
     * 解密数据为字符串
     * @param input 被解密的数据，格式为Base64字符串
     * @param charset 加密前的字符串编码
     * @return 解密后的数据
     */
    public String decryptBase64String(String input, Charset charset) {
        return new String(decryptBase64(input), charset);
    }

    /**
     * 解密数据为字符串（字符串编码为UTF-8）
     * @param input 被解密的数据，格式为Base64字符串
     * @return 解密后的数据
     */
    public String decryptBase64String(String input) {
        return decryptBase64String(input, StandardCharsets.UTF_8);
    }

    /**
     * 解密数据，不会关闭流
     * @param input 被解密的数据流
     * @return 解密后的数据
     * @throws IOException 出现IO异常
     */
    public byte[] decrypt(InputStream input) throws IOException {
        return decrypt(IoUtil.toByteArray(input));
    }

    /**
     * 初始化加密和解密器
     * @param opmode 模式(Cipher.ENCRYPT_MODE或者Cipher.DECRYPT_MODE)
     * @throws GeneralSecurityException
     */
    private void initCipher(int opmode) throws GeneralSecurityException {
        if (params == null) {
            cipher.init(opmode, secretKey);
        } else {
            cipher.init(opmode, secretKey, params);
        }
    }

    // =================================SetMethods=============================================
    /**
     * 设置算法参数
     * @param params 算法参数
     * @return this
     */
    public SymmetricCrypto setParameter(AlgorithmParameterSpec params) {
        this.params = params;
        return this;
    }

    /**
     * 设置秘密(对称)密钥
     * @param secretKey 秘密(对称)密钥
     * @return this
     */
    public SymmetricCrypto setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    // =================================GetMethods=============================================
    /**
     * 获得秘密(对称)密钥
     * @return 秘密(对称)密钥
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }
}
