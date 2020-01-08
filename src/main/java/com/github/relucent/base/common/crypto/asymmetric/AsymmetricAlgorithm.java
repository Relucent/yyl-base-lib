package com.github.relucent.base.common.crypto.asymmetric;

/**
 * 非对称算法类型<br>
 * see: https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
 */
public enum AsymmetricAlgorithm {

    /** RSA算法 */
    RSA("RSA"),
    /** RSA算法，此算法用了默认补位方式为RSA/ECB/PKCS1Padding */
    RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"),
    /** RSA算法，此算法用了RSA/None/NoPadding */
    RSA_NONE("RSA/None/NoPadding"),
    /** EC（Elliptic Curve）椭圆曲线密码算法 */
    EC("EC");

    /** 算法字符串表示 */
    private final String value;

    /**
     * 构造函数
     * @param value 算法字符串表示(区分大小写)
     */
    private AsymmetricAlgorithm(String value) {
        this.value = value;
    }

    /**
     * 获取算法字符串表示
     * @return 算法字符串表示
     */
    public String getValue() {
        return this.value;
    }
}
