package com.github.relucent.base.common.crypto.signature;

/**
 * 签名算法类型<br>
 * @see https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Signature
 */
public enum SignatureAlgorithm {

    /** RSA签名算法(不使用摘要算法) */
    NONEwithRSA("NONEwithRSA"),
    /** RSA签名算法(使用MD2摘要算法) */
    MD2withRSA("MD2withRSA"),
    /** RSA签名算法(使用MD5摘要算法) */
    MD5withRSA("MD5withRSA"),

    /** RSA签名算法(使用 SHA-1摘要算法 ) */
    SHA1withRSA("SHA1withRSA"),
    /** RSA签名算法(使用 SHA-256摘要算法 ) */
    SHA256withRSA("SHA256withRSA"),
    /** RSA签名算法(使用 SHA-384摘要算法 ) */
    SHA384withRSA("SHA384withRSA"),

    SHA512withRSA("SHA512withRSA"),

    /** DSA签名算法(不使用摘要算法) */
    NONEwithDSA("NONEwithDSA"),
    /** DSA签名算法(使用 SHA-1摘要算法 ) */
    SHA1withDSA("SHA1withDSA"),

    /** 椭圆曲线数字签名算法(不使用摘要算法) */
    NONEwithECDSA("NONEwithECDSA"),
    /** 椭圆曲线数字签名算法(使用 SHA-1摘要算法 ) */
    SHA1withECDSA("SHA1withECDSA"),
    /** 椭圆曲线数字签名算法(使用 SHA-256摘要算法 ) */
    SHA256withECDSA("SHA256withECDSA"),
    /** 椭圆曲线数字签名算法(使用 SHA-384摘要算法 ) */
    SHA384withECDSA("SHA384withECDSA"),
    /** 椭圆曲线数字签名算法(使用 SHA-512摘要算法 ) */
    SHA512withECDSA("SHA512withECDSA");

    private String value;

    /**
     * 构造函数
     * @param value 算法名称(区分大小写)
     */
    private SignatureAlgorithm(String value) {
        this.value = value;
    }

    /**
     * 获取算法名称(区分大小写)
     * @return 算法名称
     */
    public String getValue() {
        return this.value;
    }
}
