package com.github.relucent.base.common.crypto.digest;

/**
 * 摘要算法类型<br>
 * see: https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest
 */
public enum DigestAlgorithm {

    /**
     * RFC 1319中定义的MD2消息摘要算法
     */
    MD2("MD2"),
    /**
     * RFC 1321中定义的MD2消息摘要算法
     */
    MD5("MD5"),

    /**
     * FIPS PUB 180-2中定义的SHA-1哈希算法
     */
    SHA_1("SHA-1"),
    /**
     * FIPS PUB 180-3中定义的SHA-224哈希算法 （存在于JAVA8中）
     */
    SHA_224("SHA-224"),

    /**
     * FIPS PUB 180-2中定义的SHA-256哈希算法
     */
    SHA_256("SHA-256"),

    /**
     * FIPS PUB 180-2中定义的SHA-384散列算法
     */
    SHA_384("SHA-384"),

    /**
     * FIPS PUB 180-2中定义的SHA-512哈希算法
     */
    SHA_512("SHA-512"),

    /**
     * FIPS PUB 180-4中定义的SHA-512哈希算法 （包含在Oracle Java 9）
     */
    SHA_512_224("SHA-512/224"),

    /**
     * FIPS PUB 180-4中定义的SHA-512哈希算法 （包含在Oracle Java 9）
     */
    SHA_512_256("SHA-512/256"),

    /**
     * FIPS PUB 202中定义的SHA3-224散列算法 （包含在Oracle Java 9）
     */
    SHA3_224("SHA3-224"),

    /**
     * FIPS PUB 202中定义的SHA3-256散列算法 （包含在Oracle Java 9）
     */
    SHA3_256("SHA3-256"),

    /**
     * FIPS PUB 202中定义的SHA3-384散列算法 （包含在Oracle Java 9）
     */
    SHA3_384("SHA3-384"),

    /**
     * FIPS PUB 202中定义的SHA3-512散列算法 （包含在Oracle Java 9）
     */
    SHA3_512("SHA3-512"),

    /**
     * 信息安全技术 SM3密码杂凑算法
     */
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
