package com.github.relucent.base.common.crypto.asymmetric;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import com.github.relucent.base.common.crypto.CipherUtil;
import com.github.relucent.base.common.crypto.CryptoException;

/**
 * 非对称加密算法(Asymmetric Cryptographic Algorithm)<br>
 * 非对称加密算法是一种密钥的保密方法，加密和解密使用不同密钥的加密算法，也称为公私钥加密。<br>
 * 非对称加密算法需要两个密钥：公开密钥（PublicKey，简称公钥）和私有密钥（PrivateKey，简称私钥）。<br>
 * 公钥与私钥是一对，如果用公钥对数据进行加密，只有用对应的私钥才能解密。<br>
 * 因为加密和解密使用的是两个不同的密钥，所以这种算法叫作非对称加密算法。 <br>
 * 注意：该类的实例不保证线程安全，应当避免多线程同时调用同一个实例(每个线程使用独立的实例，或者在调用时候增加同步锁)。<br>
 */
public class AsymmetricCrypto extends AbstractAsymmetricCrypto<AsymmetricCrypto> {

    // =================================Fields================================================
    /** 提供加密和解密功能 */
    protected Cipher cipher;
    /** 加密的块大小 */
    protected int encryptBlockSize = -1;
    /** 解密的块大小 */
    protected int decryptBlockSize = -1;

    // =================================Constructors===========================================
    /**
     * 构造函数， 使用随机的密钥对(私钥和公钥)<br>
     * @param algorithm 签名算法
     */
    public AsymmetricCrypto(AsymmetricAlgorithm algorithm) {
        this(algorithm, null, null);
    }

    /**
     * 构造函数<br>
     * 传入私钥，只用来做加密或者解密其中一种操作<br>
     * @param algorithm 签名算法
     * @param privateKey 私钥
     */
    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, PrivateKey privateKey) {
        this(algorithm, privateKey, null);
    }

    /**
     * 构造函数<br>
     * 传入公钥，只用来做加密或者解密其中一种操作<br>
     * @param algorithm 签名算法
     * @param publicKey 公钥
     */
    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, PublicKey publicKey) {
        this(algorithm, null, publicKey);
    }

    /**
     * 构造函数<br>
     * 私钥和公钥同时为null时，使用随机的密钥对(私钥和公钥)。 私钥和公钥可以只传入一个，只用来做加密或者解密其中一种操作.<br>
     * @param algorithm 签名算法
     * @param privateKey 私钥
     * @param publicKey 公钥
     */
    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this(algorithm.getValue(), privateKey, publicKey);
    }

    /**
     * 构造函数<br>
     * 构造 私钥和公钥同时为null时，使用随机的密钥对(私钥和公钥)。 私钥和公钥可以只传入一个，只用来做加密或者解密其中一种操作.<br>
     * @param algorithm 算法名称
     * @param privateKey 私钥
     * @param publicKey 公钥
     */
    protected AsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        super(algorithm, privateKey, publicKey);
    }

    // =================================InitializeMethods======================================
    /**
     * 初始化，如果私钥和公钥同时为null，则使用随机生成的密钥对(私钥和公钥)。
     * @param algorithm 算法
     * @param privateKey 私钥
     * @param publicKey 公钥
     */
    protected void initialize(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        super.initialize(algorithm, privateKey, publicKey);
        this.cipher = createCipher(algorithm);
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
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密后的数据
     */
    public byte[] encrypt(byte[] input, KeyType keyType) {
        try {
            return doFinal(input, Cipher.ENCRYPT_MODE, keyType);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 解密数据
     * @param input 被解密的数据
     * @param keyType 密钥类型(私钥或公钥)
     * @return 解密后的数据
     */
    public byte[] decrypt(byte[] input, KeyType keyType) {
        try {
            return doFinal(input, Cipher.DECRYPT_MODE, keyType);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 加密或解密数据
     * @param input 需要加密或解密的数据
     * @param opmode 模式(Cipher.ENCRYPT_MODE或者Cipher.DECRYPT_MODE)
     * @param keyType 密钥类型(私钥或公钥)
     * @return 加密或解密后的数据
     * @throws Exception 出现异常时候抛出
     */
    private byte[] doFinal(byte[] input, int opmode, KeyType keyType) throws Exception {

        // 根据密钥类型获得相应密钥
        Key key = getKey(keyType);

        // 用密钥初始化此 CIPHER
        cipher.init(opmode, key);

        // 分段大小
        int blockSize = getBlockSize(opmode, key);

        // 分段大小0，无法分段
        if (blockSize == 0) {
            try {
                return this.cipher.doFinal(input, 0, input.length);
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        }

        // 不足分段，直接进行加密或解密
        if (input.length <= blockSize) {
            try {
                return this.cipher.doFinal(input, 0, input.length);
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        }

        // 分段加密或解密数据
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (int offset = 0, length = input.length; offset < length; offset += blockSize) {
                output.write(cipher.doFinal(input, offset, Math.min(length - offset, blockSize)));
            }
            return output.toByteArray();
        }
    }

    /**
     * 返回块的大小（以字节为单位）。
     * @param opmode 此 Cipher 的操作模式（ENCRYPT_MODE、DECRYPT_MODE、WRAP_MODE 或 UNWRAP_MODE）
     * @param key 密钥
     * @return 块的大小（以字节为单位
     */
    protected int getBlockSize(int opmode, Key key) {
        int blockSize = cipher.getBlockSize();
        if (Cipher.ENCRYPT_MODE == opmode && encryptBlockSize != -1) {
            return encryptBlockSize;
        }
        if (Cipher.DECRYPT_MODE == opmode && decryptBlockSize != -1) {
            return decryptBlockSize;
        }
        return blockSize;
    }

    // =================================SetMethods=============================================
    /**
     * 设置加密块大小
     * @param encryptBlockSize 加密块大小
     */
    public void setEncryptBlockSize(int encryptBlockSize) {
        this.encryptBlockSize = encryptBlockSize;
    }

    /**
     * 设置解密块大小
     * @param decryptBlockSize 解密块大小
     */
    public void setCustomDecryptBlockSize(int decryptBlockSize) {
        this.decryptBlockSize = decryptBlockSize;
    }
}
