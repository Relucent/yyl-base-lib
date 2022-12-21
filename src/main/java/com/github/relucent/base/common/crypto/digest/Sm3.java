package com.github.relucent.base.common.crypto.digest;

/**
 * 国密SM3（摘要算法）实现，该实现基于BC库<br>
 * SM3密码摘要算法是中国国家密码管理局2010年公布的中国商用密码杂凑算法标准。<br>
 * SM3算法适用于商用密码应用中的数字签名和验证，是在SHA-256基础上改进实现的一种算法。<br>
 * SM3算法采用Merkle-Damgard结构，消息分组的长度为512位，生成的摘要长度为256位（32字节，16进制字符串长度64），具有相对较高的安全性。<br>
 * 标准号：GB/T 32905-2016 <br>
 * 中文标准名称：信息安全技术 SM3密码杂凑算法<br>
 * 英文标准名称：Information security techniques—SM3 cryptographic hash algorithm <br>
 * @see <a href="https://openstd.samr.gov.cn/bzgk/gb/newGbInfo?hcno=45B1A67F20F3BF339211C391E9278F5E">GB/T 32905-2016</a>
 */
public class Sm3 extends Digester {

    // =================================Constructors===========================================
    /**
     * 构造函数
     * @param salt 盐值
     * @param saltPosition 加盐位置，既将盐值字符串放置在数据的index数，默认0
     * @param digestCount 摘要次数，当此值小于等于1,默认为1。
     */
    protected Sm3(byte[] salt, int saltPosition, int digestCount) {
        super(DigestAlgorithm.SM3, salt, saltPosition, digestCount, null);
    }

    // =================================Methods================================================
    /**
     * 创建MD5实例
     * @return MD5
     */
    public static Sm3 create() {
        return new Sm3(null, 0, 1);
    }

    /**
     * 创建MD5实例
     * @param salt 盐值
     * @return MD5实例
     */
    public static Sm3 create(byte[] salt) {
        return new Sm3(salt, 0, 1);
    }

    /**
     * 创建MD5实例
     * @param salt 盐值
     * @param saltPosition 加盐位置，既将盐值字符串放置在数据的index数，默认0
     * @param digestCount 摘要次数，当此值小于等于1,默认为1。
     * @return MD5实例
     */
    public static Sm3 create(byte[] salt, int saltPosition, int digestCount) {
        return new Sm3(salt, saltPosition, digestCount);
    }
}
