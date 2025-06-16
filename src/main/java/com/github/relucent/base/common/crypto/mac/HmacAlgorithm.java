package com.github.relucent.base.common.crypto.mac;

/**
 * 枚举定义了支持的 HMAC 算法。 每种算法都是基于哈希函数实现的对称消息认证码（MAC）。
 */
public enum HmacAlgorithm {
    /**
     * 基于 MD5 的 HMAC，速度快，但已不推荐用于高安全场景<br>
     */
    HmacMD5("HmacMD5"),

    /**
     * 基于 SHA-1 的 HMAC，强度比 MD5 高，但有已知弱点，适用于兼容性需求<br>
     */
    HmacSHA1("HmacSHA1"),

    /**
     * 推荐算法，安全性和性能兼顾，是 HMAC 的主流选择<br>
     */
    HmacSHA256("HmacSHA256"),

    /**
     * 安全性更高的 HMAC，比 SHA256 更长但计算稍慢<br>
     */
    HmacSHA384("HmacSHA384"),

    /**
     * 提供最高级别的 SHA 系列安全性，适合高安全要求的场景<br>
     */
    HmacSHA512("HmacSHA512"),

    /**
     * 国密算法 SM3 的 HMAC 实现，符合中国国家商用密码标准<br>
     * 需要依赖 BouncyCastle 加密库支持<br>
     */
    HmacSM3("HmacSM3");

    private final String algorithm;

    HmacAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 获取对应的算法名称字符串
     * @return 算法名称字符串
     */
    public String getAlgorithm() {
        return algorithm;
    }
}