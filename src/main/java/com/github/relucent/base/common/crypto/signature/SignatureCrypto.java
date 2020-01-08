package com.github.relucent.base.common.crypto.signature;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Set;

import javax.crypto.Cipher;

import com.github.relucent.base.common.crypto.CryptoException;
import com.github.relucent.base.common.crypto.asymmetric.KeyUtil;

/**
 * 数字签名算法类，提供数据的签名与验证方法<br>
 * 注意：该类的实例不保证线程安全，应当避免多线程同时调用同一个实例(每个线程使用独立的实例，或者在调用时候增加同步锁)。<br>
 * @see java.security.Signature
 */
public class SignatureCrypto {
    // =================================Fields================================================
    /** KeyUsage扩展的对象标识符 (Object Identifier，OID) */
    private static final String KEY_USAGE_OID = "2.5.29.15";

    /** 公钥 */
    protected PublicKey publicKey;
    /** 私钥 */
    protected PrivateKey privateKey;
    /** 提供加密和解密功能 */
    protected Cipher cipher;
    /** 算法名称 */
    protected String algorithm;
    /** 签名，提供数字签名算法功能(签名和验证) */
    protected Signature signature;

    // =================================Constructors===========================================
    /**
     * 构造函数<br>
     * 使用随机的密钥对(私钥和公钥)<br>
     * @param algorithm 签名算法
     */
    public SignatureCrypto(SignatureAlgorithm algorithm) {
        this(algorithm, null, null);
    }

    /**
     * 构造函数<br>
     * 传入私钥，只能用于生成数字签名<br>
     * @param algorithm 签名算法
     * @param privateKey 私钥
     */
    public SignatureCrypto(SignatureAlgorithm algorithm, PrivateKey privateKey) {
        this(algorithm, privateKey, null);
    }

    /**
     * 构造函数<br>
     * 传入公钥，只能用于做数字签名的合法性验证<br>
     * @param algorithm 签名算法
     * @param publicKey 公钥
     */
    public SignatureCrypto(SignatureAlgorithm algorithm, PublicKey publicKey) {
        this(algorithm, null, publicKey);
    }

    /**
     * 构造函数<br>
     * 私钥和公钥同时为null时，使用随机的密钥对(私钥和公钥)。 <br>
     * 私钥和公钥可以只传入一个，只能用来做签名或者验证一种操作。 <br>
     * @param algorithm 签名算法
     * @param privateKey 私钥
     * @param publicKey 公钥
     */
    public SignatureCrypto(SignatureAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this(algorithm.getValue(), privateKey, publicKey);
    }

    /**
     * 构造函数<br>
     * 私钥和公钥同时为null时，使用随机的密钥对(私钥和公钥)。 <br>
     * 私钥和公钥可以只传入一个，只能用来做签名或者验证一种操作。 <br>
     * @param algorithm 签名算法
     * @param privateKey 私钥
     * @param publicKey 公钥
     */
    protected SignatureCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this.algorithm = algorithm;
        try {
            this.signature = Signature.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        if (null == privateKey && null == publicKey) {
            KeyPair keyPair = KeyUtil.generateKeyPair(algorithm);
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        }
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    // =================================Methods================================================
    /**
     * 生成密钥对(公钥和私钥)
     */
    protected void initializeKeys() {
        KeyPair keyPair = KeyUtil.generateKeyPair(algorithm);
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    /**
     * 用私钥对信息生成数字签名
     * @param data 加密数据
     * @return 签名
     */
    public byte[] sign(byte[] data) {
        try {
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * 用公钥检验数字签名的合法性
     * @param data 数据
     * @param sign 签名
     * @return 是否验证通过
     */
    public boolean verify(byte[] data, byte[] sign) {
        try {
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    // =================================SetMethods=============================================
    /**
     * 设置身份证书{@link Certificate} 为PublicKey<br>
     * @param certificate身份证{@link Certificate}
     * @return this
     */
    public SignatureCrypto setCertificate(Certificate certificate) {
        // 检查是否有密钥扩展
        if (certificate instanceof X509Certificate) {
            final X509Certificate x509 = (X509Certificate) certificate;
            final Set<String> oids = x509.getCriticalExtensionOIDs();
            if (oids != null && oids.contains(KEY_USAGE_OID)) {
                final boolean[] keyUsage = x509.getKeyUsage();
                // KeyUsage ::= BIT STRING
                // [0] digitalSignature
                // [1] nonRepudiation
                // [2] keyEncipherment
                // [3] dataEncipherment
                // [4] keyAgreement
                // [5] keyCertSign
                // [6] cRLSign
                // [7] encipherOnly
                // [8] decipherOnly
                if (keyUsage != null && !keyUsage[0]) {
                    throw new CryptoException("Certificate keyUsage Error");
                }
            }
        }
        this.publicKey = certificate.getPublicKey();
        return this;
    }

    /**
     * 使用指定的参数集初始化此签名引擎
     * @param params 算法参数
     * @return this
     */
    public SignatureCrypto setParameter(AlgorithmParameterSpec params) {
        try {
            signature.setParameter(params);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
        return this;
    }

    /**
     * 设置私钥
     * @param privateKey 私钥
     * @return this
     */
    public SignatureCrypto setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    /**
     * 设置公钥
     * @param publicKey 公钥
     * @return this
     */
    public SignatureCrypto setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    // =================================GetMethods=============================================
    /**
     * 获得签名对象 {@link Signature}
     * @return 签名对象
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * 获得私钥
     * @return 获得私钥
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * 获得公钥
     * @return 获得公钥
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}
