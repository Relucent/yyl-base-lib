package com.github.relucent.base.common.crypto.symmetric;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * AES-GCM 加密算法
 */
public class AesGcm {

    // ==============================Constants========================================
    public static final int DEFAULT_GCM_IV_LENGTH = 12;
    public static final int DEFAULT_GCM_TAG_LENGTH = 128;

    // ==============================Fields===========================================
    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();
    private final int ivLength;
    private final int tagLengthBit;

    // ==============================Constructors=====================================
    /**
     * 构造函数
     * @param key 秘钥
     */
    public AesGcm(SecretKey key) {
        this(key, DEFAULT_GCM_IV_LENGTH, DEFAULT_GCM_TAG_LENGTH);
    }

    /**
     * 构造函数
     * @param key          秘钥
     * @param ivLength     IV长度
     * @param tagLengthBit AEAD tag长度
     */
    public AesGcm(SecretKey key, int ivLength, int tagLengthBit) {
        this.key = key;
        this.ivLength = ivLength;
        this.tagLengthBit = tagLengthBit;
    }

    // ==============================Methods==========================================
    /**
     * 加密数据
     * @param plainText 要加密的数据
     * @return 加密后的数据
     * @throws Exception 加密失败
     */
    public AesGcmPayload encrypt(byte[] plainText) throws Exception {
        byte[] iv = new byte[ivLength];
        random.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(tagLengthBit, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] cipherText = cipher.doFinal(plainText);
        return new AesGcmPayload(iv, cipherText);
    }

    /**
     * 解密数据
     * @param cipherPayload 被解密的数据(包含IV)
     * @return 解密后的数据
     */
    public byte[] decrypt(AesGcmPayload cipherPayload) throws Exception {
        byte[] iv = cipherPayload.getIv();
        byte[] cipherText = cipherPayload.getCipherText();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(tagLengthBit, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(cipherText);
    }

    // ==============================InnerClass=======================================
    /**
     * AES-GCM 加密结果封装类
     */
    public static class AesGcmPayload {

        /** 初始化向量（IV），AES-GCM 解密时必须使用 */
        private final byte[] iv;

        /** 加密后的密文（CipherText） */
        private final byte[] cipherText;

        /**
         * 构造函数
         * @param iv         初始化向量
         * @param cipherText 密文
         */
        public AesGcmPayload(byte[] iv, byte[] cipherText) {
            this.iv = iv;
            this.cipherText = cipherText;
        }

        /**
         * 获取 IV
         * @return IV
         */
        public byte[] getIv() {
            return iv;
        }

        /**
         * 获取密文
         * @return 密文
         */
        public byte[] getCipherText() {
            return cipherText;
        }

        /**
         * 合并 IV 和 CipherText 为一个 byte[]，方便传输或存储<br>
         * 格式：[IV | CipherText]
         * @return IV + CipherText 合并后的字节数组
         */
        public byte[] toBytes() {
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
            return combined;
        }

        /**
         * 解析合并的 byte[] 为 AesGcmPayload<br>
         * 注意：假设默认 AES-GCM IV 长度为 12 字节<br>
         * @param combined 加密输出 [IV + CipherText]
         * @return AesGcmPayload 对象
         */
        public static AesGcmPayload parse(byte[] combined) {
            return parse(combined, DEFAULT_GCM_IV_LENGTH);
        }

        /**
         * 解析合并的 byte[] 为 AesGcmPayload<br>
         * @param combined 加密输出 [IV + CipherText]
         * @param ivLength IV长度
         * @return AesGcmPayload 对象
         */
        public static AesGcmPayload parse(byte[] combined, int ivLength) {
            if (combined == null || combined.length < ivLength + 1) {
                throw new IllegalArgumentException("Combined array too short");
            }
            byte[] iv = Arrays.copyOfRange(combined, 0, ivLength);
            byte[] cipherText = Arrays.copyOfRange(combined, ivLength, combined.length);
            return new AesGcmPayload(iv, cipherText);
        }
    }
}
