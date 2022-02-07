package com.github.relucent.base.common.crypto;

/**
 * 加密模式
 * @see <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Cipher">Cipher Algorithm Modes</a>
 */
public enum ModeEnum {

    /** 无模式 */
    NONE("NONE"),

    /** 电子密码本模式 (Electronic Code Book) */
    ECB("ECB"),

    /** 加密块链模式(Cipher Block Chaining) */
    CBC("CBC"),

    /** 加密反馈模式 (Cipher Feedback) */
    CFB("CFB"),

    /** 输出反馈模式 (Output Feedback) */
    OFB("OFB"),

    /** 计数器模式 (Counter) */
    CTR("CTR"),

    /** 密文窃取模式(Cipher Text Stealing) */
    CTS("CTS"),

    /** 填充密码块链接模式(Propagating Cipher Block) */
    PCBC("PCBC");

    private final String string;

    private ModeEnum(String string) {
        this.string = string;
    }

    public String string() {
        return string;
    }
}
