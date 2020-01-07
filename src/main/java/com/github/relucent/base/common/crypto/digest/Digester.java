package com.github.relucent.base.common.crypto.digest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.constants.IoConstants;
import com.github.relucent.base.common.crypto.CryptoException;

/**
 * 消息摘要算法 (Message-Digest Algorithm)工具类<br>
 * 此类为应用程序提供信息摘要算法的功能，如 MD5或 SHA算法。<br>
 * 注意：该类的实例不保证线程安全，应当避免多线程同时调用同一个实例(每个线程使用独立的实例，或者在调用时候增加同步锁)。<br>
 */
public class Digester {

    // =================================Fields================================================
    /** 摘要算法的功能类(该对象非线程安全) */
    protected MessageDigest messageDigest;
    /** 盐值 */
    protected byte[] salt;
    /** 加盐位置，默认0 */
    protected int saltPosition;
    /** 散列次数 */
    protected int digestCount;

    // =================================Constructors===========================================
    /**
     * 构造函数
     * @param algorithm 算法
     */
    public Digester(DigestAlgorithm algorithm) {
        this(algorithm.getValue(), null, 0, 0, null);
    }

    /**
     * 构造函数
     * @param algorithm 算法
     * @param provider 算法提供者，null表示JDK默认，可以引入第三方包(例如BouncyCastle)提供更多算法支持
     */
    public Digester(DigestAlgorithm algorithm, Provider provider) {
        this(algorithm.getValue(), null, 0, 0, provider);
    }

    /**
     * 构造函数
     * @param algorithm 算法
     * @param salt 盐值 (默认加盐位置在头部)
     * @param digestCount 摘要次数，当此值小于等于1,默认为1。
     */
    public Digester(DigestAlgorithm algorithm, byte[] salt) {
        this(algorithm.getValue(), salt, 0, 1, null);
    }

    /**
     * 构造函数
     * @param algorithm 算法
     * @param salt 盐值
     * @param saltPosition 加盐位置，既将盐值字符串放置在数据的index数，默认0
     * @param digestCount 摘要次数，当此值小于等于1,默认为1。
     * @param provider 算法提供者，null表示JDK默认，可以引入第三方包(例如BouncyCastle)提供更多算法支持
     */
    protected Digester(DigestAlgorithm algorithm, byte[] salt, int saltPosition, int digestCount, Provider provider) {
        this(algorithm.getValue(), salt, saltPosition, digestCount, provider);
    }

    /**
     * 构造函数
     * @param algorithm 算法
     * @param salt 盐值
     * @param saltPosition 加盐位置，既将盐值字符串放置在数据的index数，默认0
     * @param digestCount 摘要次数，当此值小于等于1,默认为1。
     * @param provider 算法提供者，null表示JDK默认，可以引入第三方包(例如BouncyCastle)提供更多算法支持
     */
    protected Digester(String algorithm, byte[] salt, int saltPosition, int digestCount, Provider provider) {
        try {
            if (null == provider) {
                messageDigest = MessageDigest.getInstance(algorithm);
            } else {
                messageDigest = MessageDigest.getInstance(algorithm, provider);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        this.salt = salt;
        this.saltPosition = saltPosition;
        this.digestCount = digestCount;
    }

    // =================================Methods================================================
    /**
     * 获得 {@link MessageDigest}
     * @return {@link MessageDigest}
     */
    public MessageDigest getMessageDigest() {
        return messageDigest;
    }

    /**
     * 获取散列长度，0表示不支持此方法
     * @return 散列长度，0表示不支持此方法
     */
    public int getDigestLength() {
        return this.messageDigest.getDigestLength();
    }

    /**
     * 生成数据的摘要
     * @param input 被摘要数据
     * @return 摘要字节数组
     */
    public byte[] digest(String input) {
        return digest(input, StandardCharsets.UTF_8);
    }

    /**
     * 生成摘要
     * @param input 被摘要数据
     * @param charsetName 编码
     * @return 摘要字节数组
     */
    public byte[] digest(String input, String charsetName) {
        return digest(input, Charset.forName(charsetName));
    }

    /**
     * 生成数据的摘要
     * @param input 被摘要数据
     * @param charset 摘要数据
     * @return 摘要字节数组
     */
    public byte[] digest(String input, Charset charset) {
        return digest(input.getBytes(charset));
    }

    /**
     * 生成数据的摘要，并转为16进制字符串
     * @param input 被摘要数据
     * @return 摘要16进制字符串
     */
    public String digestHex(String input) {
        return digestHex(input, StandardCharsets.UTF_8);
    }

    /**
     * 生成数据的摘要，并转为16进制字符串
     * @param input 被摘要数据
     * @param charsetName 编码
     * @return 摘要16进制字符串
     */
    public String digestHex(String input, String charsetName) {
        return digestHex(input, Charset.forName(charsetName));
    }

    /**
     * 生成数据的摘要，并转为16进制字符串
     * @param input 被摘要数据
     * @param charset 编码
     * @return 摘要
     */
    public String digestHex(String input, Charset charset) {
        byte[] data = input.getBytes(charset);
        return digestHex(data);
    }

    /**
     * 生成摘要，并转为16进制字符串<br>
     * @param input 被摘要数据
     * @return 摘要
     */
    public String digestHex(byte[] input) {
        byte[] hash = digest(input);
        return Hex.encodeHexString(hash);
    }

    /**
     * 生成摘要
     * @param input 输入字节数组
     * @return 摘要字节数组
     */
    public byte[] digest(byte[] input) {
        // 使用指定的字节数组更新摘要
        doUpdate(input);
        // 来完成哈希计算。
        byte[] hash = digestAndReset();
        // 重复计算
        return doRepeatDigest(hash);
    }

    /**
     * 生成摘要
     * @param input {@link InputStream} 输入数据流
     * @return 摘要字节数组
     * @throws IOException 出现IO异常时抛出
     */
    public byte[] digest(InputStream input) throws IOException {
        byte[] buffer = new byte[IoConstants.DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        // 加盐在开头
        if (salt != null && saltPosition <= 0) {
            messageDigest.update(salt);
        }
        while (IoConstants.EOF != (n = input.read(buffer))) {
            // 加盐在中间
            if (salt != null && count <= saltPosition && saltPosition < count + n) {
                int len = (int) (saltPosition - count);
                if (len != 0) {
                    messageDigest.update(buffer, 0, len);
                }
                messageDigest.update(salt);
                messageDigest.update(buffer, len, n);
            } else {
                messageDigest.update(buffer, 0, n);
            }
            count += n;
        }
        // 加盐在末尾
        if (salt != null && count < saltPosition) {
            messageDigest.update(salt);
        }
        return messageDigest.digest();
    }

    /**
     * 使用指定的字节数组更新摘要
     * @param input 字节数组
     */
    private void doUpdate(byte[] input) {
        // 无加盐
        if (salt == null) {
            messageDigest.update(input);
        }
        // 加盐在开头
        else if (saltPosition <= 0) {
            messageDigest.update(salt);
            messageDigest.update(input);
        }
        // 加盐在末尾
        else if (saltPosition >= input.length) {
            messageDigest.update(input);
            messageDigest.update(salt);
        }
        // 加盐在中间
        else {
            messageDigest.update(input, 0, saltPosition);
            messageDigest.update(salt);
            messageDigest.update(input, saltPosition, input.length - saltPosition);
        }
    }

    /**
     * 重复计算摘要，取决于{@link #digestCount} 值<br>
     * 每次计算摘要前都会重置{@link #messageDigest}
     * @param input 第一次的摘要数据
     * @return 摘要字节数组
     */
    private byte[] doRepeatDigest(byte[] input) {
        int count = Math.max(1, digestCount);
        for (int i = 1; i < count; i++) {
            messageDigest.update(input);
            input = digestAndReset();
        }
        return input;
    }

    /**
     * 来完成哈希计算，并重置摘要。
     * @return
     */
    private byte[] digestAndReset() {
        try {
            return messageDigest.digest();
        } finally {
            messageDigest.reset();
        }
    }

    // =================================SetMethods=============================================
    /**
     * 设置加盐内容
     * @param salt 盐值
     * @return this
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    /**
     * 设置加盐的位置，只有盐值存在时有效<br>
     * @param saltPosition 盐的位置
     * @return this
     */
    public void setSaltPosition(int saltPosition) {
        this.saltPosition = saltPosition;
    }

    /**
     * 设置重复计算摘要值次数
     * @param digestCount 摘要值次数
     * @return this
     */
    public void setDigestCount(int digestCount) {
        this.digestCount = digestCount;
    }
}
