package com.github.relucent.base.common.crypto.digest;

/**
 * MD5算法 <br>
 */
public class Md5 extends Digester {

    // =================================Constructors===========================================
    /**
     * 构造函数
     * @param salt 盐值
     * @param saltPosition 加盐位置，既将盐值字符串放置在数据的index数，默认0
     * @param digestCount 摘要次数，当此值小于等于1,默认为1。
     */
    protected Md5(byte[] salt, int saltPosition, int digestCount) {
        super(DigestAlgorithm.MD5, salt, saltPosition, digestCount, null);
    }

    // =================================Methods================================================
    /**
     * 创建MD5实例
     * @return MD5
     */
    public static Md5 create() {
        return new Md5(null, 0, 1);
    }

    /**
     * 创建MD5实例
     * @param salt 盐值
     * @return MD5实例
     */
    public static Md5 create(byte[] salt) {
        return new Md5(salt, 0, 1);
    }

    /**
     * 创建MD5实例
     * @param salt 盐值
     * @param saltPosition 加盐位置，既将盐值字符串放置在数据的index数，默认0
     * @param digestCount 摘要次数，当此值小于等于1,默认为1。
     * @return MD5实例
     */
    public static Md5 create(byte[] salt, int saltPosition, int digestCount) {
        return new Md5(salt, saltPosition, digestCount);
    }
}
