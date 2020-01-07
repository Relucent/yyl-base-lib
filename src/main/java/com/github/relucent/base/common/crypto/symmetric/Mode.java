package com.github.relucent.base.common.crypto.symmetric;

/**
 * 密码算法模式
 * @see <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher">Cipher Algorithm Modes</a>
 */
public enum Mode {

    /** 无模式 */
    NONE,

    /** 电子密码本模式 (Electronic Code Book) */
    ECB,

    /** 加密块链模式(Cipher Block Chaining) */
    CBC,

    /** 加密反馈模式 (Cipher Feedback) */
    CFB,

    /** 输出反馈模式 (Output Feedback) */
    OFB,

    /** 计数器模式 (Counter) */
    CTR,

    /** 密文窃取模式(Cipher Text Stealing) */
    CTS,

    /** 填充密码块链接模式(Propagating Cipher Block) */
    PCBC;
}
