package com.github.relucent.base.common.crypto.digest;

/**
 * 摘要算法类型<br>
 * see: https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest
 */
public enum DigestAlgorithm {

    /** MD2 */
    MD2("MD2"),
    /** MD5 */
    MD5("MD5"),
    /** SHA-1 */
    SHA1("SHA-1"),
    /** SHA-256 */
    SHA256("SHA-256"),
    /** SHA-384 */
    SHA384("SHA-384"),
    /** SHA-512 */
    SHA512("SHA-512"),
    /** 信息安全技术 SM3密码杂凑算法 */
    SM3("SM3");

    /** 算法字符串表示 */
    private final String value;

    /**
     * 构造函数
     * @param value 算法字符串表示
     */
    private DigestAlgorithm(String value) {
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
