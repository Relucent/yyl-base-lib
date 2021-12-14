package com.github.relucent.base.common.crypto.symmetric;

/**
 * 填充方式 <br>
 * @see <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher">Cipher Algorithm Padding</a>
 */
public enum PaddingEnum {
    /** 无补码 */
    NoPadding("NoPadding"),
    /** ISO10126 */
    ISO10126Padding("ISO10126Padding"),
    /** OAEP */
    OAEPPadding("OAEPPadding"),
    /** PKCS1 */
    PKCS1Padding("PKCS1Padding"),
    /** PKCS5 */
    PKCS5Padding("PKCS5Padding"),
    /** PKCS7Padding */
    PKCS7Padding("PKCS7Padding"),
    /** SSL3 */
    SSL3Padding("SSL3Padding");

    private final String string;

    private PaddingEnum(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
