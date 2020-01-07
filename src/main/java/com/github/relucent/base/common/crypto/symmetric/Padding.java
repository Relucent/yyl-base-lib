package com.github.relucent.base.common.crypto.symmetric;

/**
 * 填充方式 <br>
 * @see <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher">Cipher Algorithm Padding</a>
 */
public enum Padding {
    /** 无补码 */
    NoPadding,
    /** ISO10126 */
    ISO10126Padding,
    /** OAEP */
    OAEPPadding,
    /** PKCS1 */
    PKCS1Padding,
    /** PKCS5 */
    PKCS5Padding,
    /** SSL3 */
    SSL3Padding
}
