package com.github.relucent.base.common.identifier;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ULID（Universally Unique Lexicographically Sortable Identifier）唯一标识符实现。<br>
 * ULID 兼顾了全局唯一性和按时间排序的特性，并且使用 Crockford Base32 编码（不含易混淆字符如 I, L, O, U），是一种比 UUID 更现代、更实用的唯一标识符方案<br>
 * <p>
 * ULID 是一个 128 位的值（26个字符），由两个部分组成：
 * <ul>
 * <li><b>时间部分</b>：自 1970-01-01（Unix 纪元）以来的毫秒数，10个字符（48位）。
 * <li><b>随机部分</b>：由安全随机生成器生成的 80 位随机序列，16个字符。
 * </ul>
 */
@SuppressWarnings("serial")
public final class Ulid implements Serializable, Comparable<Ulid> {

    /** 时钟漂移容限 */
    private static final int CLOCK_DRIFT_TOLERANCE = 10_000;

    /** 单调递增控制器 */
    private static class MonotonicHolder {
        /** 随机数生成器 */
        static final Random ENTROPY = new SecureRandom();
        /** 同步锁 */
        static final Lock LOCK = new ReentrantLock();
        /** 上一次的ULID（用来控制单调递） */
        static final AtomicReference<Ulid> LAST_ULID = new AtomicReference<>(Ulid.MIN);
    }

    /**
     * most significant bits. <br>
     * 最高有效位
     */
    private final long msb;
    /**
     * least significant bits. <br>
     * 最低有效位
     */
    private final long lsb;
    /**
     * Number of characters of a ULID. <br>
     * ULID字符串的长度
     */
    public static final int ULID_CHARS = 26;
    /**
     * Number of characters of the time component of a ULID. <br>
     * ULID中时间部分字符长度
     */
    public static final int TIME_CHARS = 10;
    /**
     * Number of characters of the random component of a ULID.<br>
     * ULID中随机部分字符长度
     */
    public static final int RANDOM_CHARS = 16;

    /**
     * Number of bytes of a ULID. <br>
     * ULID字节数组长度（16字节）
     */
    public static final int ULID_BYTES = 16;
    /**
     * Number of bytes of the time component of a ULID.<br>
     * 时间部分占用字节数（6字节）
     */
    public static final int TIME_BYTES = 6;
    /**
     * Number of bytes of the random component of a ULID.<br>
     * 随机部分占用字节数（10字节）
     */
    public static final int RANDOM_BYTES = 10;
    /**
     * A special ULID that has all 128 bits set to ZERO.<br>
     * 所有位均为0的最小ULID
     */
    public static final Ulid MIN = new Ulid(0x0000000000000000L, 0x0000000000000000L);
    /**
     * A special ULID that has all 128 bits set to ONE.<br>
     * 所有位均为1的最大ULID
     */
    public static final Ulid MAX = new Ulid(0xffffffffffffffffL, 0xffffffffffffffffL);

    private static final byte[] ALPHABET_VALUES = new byte[256];
    private static final char[] ALPHABET_UPPERCASE = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final char[] ALPHABET_LOWERCASE = "0123456789abcdefghjkmnpqrstvwxyz".toCharArray();

    static {

        // Initialize the alphabet map with -1
        // 初始化字母表映射数组，默认值为-1
        Arrays.fill(ALPHABET_VALUES, (byte) -1);

        // Map the alphabets chars to values
        // 映射字母表字符对应的数值索引
        for (int i = 0; i < ALPHABET_UPPERCASE.length; i++) {
            ALPHABET_VALUES[ALPHABET_UPPERCASE[i]] = (byte) i;
        }
        for (int i = 0; i < ALPHABET_LOWERCASE.length; i++) {
            ALPHABET_VALUES[ALPHABET_LOWERCASE[i]] = (byte) i;
        }

        // Upper case OIL
        // 大写字母 O I L 视为数字 0 和 1
        ALPHABET_VALUES['O'] = 0x00;
        ALPHABET_VALUES['I'] = 0x01;
        ALPHABET_VALUES['L'] = 0x01;

        // Lower case OIL
        // 小写字母 o i l 也视为数字 0 和 1
        ALPHABET_VALUES['o'] = 0x00;
        ALPHABET_VALUES['i'] = 0x01;
        ALPHABET_VALUES['l'] = 0x01;
    }

    // 0xffffffffffffffffL + 1 = 0x0000000000000000L
    private static final long INCREMENT_OVERFLOW = 0x0000000000000000L;

    /**
     * 创建一个新的ULID实例，该构造函数主要用于复制ULID
     * @param ulid a ULID
     */
    public Ulid(Ulid ulid) {
        this.msb = ulid.msb;
        this.lsb = ulid.lsb;
    }

    /**
     * 创建一个ULID。<br>
     * @param mostSignificantBits  高64位
     * @param leastSignificantBits 低64位
     */
    public Ulid(long mostSignificantBits, long leastSignificantBits) {
        this.msb = mostSignificantBits;
        this.lsb = leastSignificantBits;
    }

    /**
     * 根据时间戳和随机字节数组创建一个新的ULID实例。
     * <p>
     * 时间参数表示自1970-01-01以来的毫秒数，必须是非负且不大于2^48-1。
     * <p>
     * 随机参数是10字节的任意数组。
     * <p>
     * 注意：ULID不能表示1970-01-01之前的日期，因为时间戳作为无符号整数，只能表示0到2^48-1的数值。
     * @param time   自1970-01-01以来的毫秒数
     * @param random 10字节随机数组
     * @throws IllegalArgumentException 如果时间为负或超过2^48-1
     * @throws IllegalArgumentException 如果random为空或长度不为10
     */
    public Ulid(long time, byte[] random) {
        // 时间部分占48位
        if ((time & 0xffff000000000000L) != 0) {
            // ULID specification:
            // "Any attempt to decode or encode a ULID larger than this (time > 2^48-1)
            // should be rejected by all implementations, to prevent overflow bugs."
            throw new IllegalArgumentException("Invalid time value"); // overflow or negative time!
        }
        // 随机部分80位(10字节)
        if (random == null || random.length != RANDOM_BYTES) {
            throw new IllegalArgumentException("Invalid random bytes"); // null or wrong length!
        }

        long long0 = 0;
        long long1 = 0;

        long0 |= time << 16;
        long0 |= (long) (random[0x0] & 0xff) << 8;
        long0 |= (long) (random[0x1] & 0xff);

        long1 |= (long) (random[0x2] & 0xff) << 56;
        long1 |= (long) (random[0x3] & 0xff) << 48;
        long1 |= (long) (random[0x4] & 0xff) << 40;
        long1 |= (long) (random[0x5] & 0xff) << 32;
        long1 |= (long) (random[0x6] & 0xff) << 24;
        long1 |= (long) (random[0x7] & 0xff) << 16;
        long1 |= (long) (random[0x8] & 0xff) << 8;
        long1 |= (long) (random[0x9] & 0xff);

        this.msb = long0;
        this.lsb = long1;
    }

    /**
     * 快速生成一个新的ULID。<br>
     * 使用 {@link ThreadLocalRandom}，性能很好，但不具备密码学强度，适用于日志记录等场景。
     * @return ULID对象
     */
    public static Ulid fast() {
        final long time = System.currentTimeMillis();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new Ulid((time << 16) | (random.nextLong() & 0xffffL), random.nextLong());
    }

    /**
     * 生成一个ULID。<br>
     * 在生成多个 ULIDs 时，它可以确保即便在同一毫秒内生成多个 ULIDs，它们依然有序。<br>
     * 内部维护一个“上次生成时间 + 随机种子”组合，当同一毫秒内生成多个 ULID 时，它保证第二个比第一个大（通过递增随机部分）<br>
     * @return ULID对象
     */
    public static Ulid create() {
        final long time = System.currentTimeMillis();
        MonotonicHolder.LOCK.lock();
        try {
            Ulid lastUlid = MonotonicHolder.LAST_ULID.get();
            final long lastTime = lastUlid.getTime();
            if ((time > lastTime - CLOCK_DRIFT_TOLERANCE) && (time <= lastTime)) {
                lastUlid = lastUlid.increment();
            } else {
                final byte[] random = new byte[Ulid.RANDOM_BYTES];
                MonotonicHolder.ENTROPY.nextBytes(random);
                lastUlid = new Ulid(time, random);
            }
            MonotonicHolder.LAST_ULID.set(lastUlid);
            return new Ulid(lastUlid);
        } finally {
            MonotonicHolder.LOCK.unlock();
        }
    }

    /**
     * 回指定时间的最小ULID。<br>
     * 时间部分48位填充指定时间，随机部分80位全为0。<br>
     * 用于查询某个时间点之前或之后所有记录非常有用。<br>
     * @param time the number of milliseconds since 1970-01-01
     * @return ULID对象
     */
    public static Ulid min(long time) {
        return new Ulid((time << 16) | 0x0000L, 0x0000000000000000L);
    }

    /**
     * 回指定时间的最大ULID。<br>
     * 时间部分48位填充指定时间，随机部分80位全为1。<br>
     * 用于查询某个时间点之前或之后所有记录（无created_at字段）非常有用。<br>
     * @param time the number of milliseconds since 1970-01-01
     * @return ULID
     */
    public static Ulid max(long time) {
        return new Ulid((time << 16) | 0xffffL, 0xffffffffffffffffL);
    }

    /**
     * 将 UUID 转换为 ULID。
     * @param uuid UUID
     * @return ULID对象
     */
    public static Ulid from(UUID uuid) {
        return new Ulid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    /**
     * 将字节数组转换为 ULID。
     * @param bytes 字节数组
     * @return ULID对象
     * @throws IllegalArgumentException if bytes are null or its length is not 16
     */
    public static Ulid from(byte[] bytes) {

        if (bytes == null || bytes.length != ULID_BYTES) {
            throw new IllegalArgumentException("Invalid ULID bytes"); // null or wrong length!
        }

        long msb = 0;
        long lsb = 0;

        msb |= (bytes[0x0] & 0xffL) << 56;
        msb |= (bytes[0x1] & 0xffL) << 48;
        msb |= (bytes[0x2] & 0xffL) << 40;
        msb |= (bytes[0x3] & 0xffL) << 32;
        msb |= (bytes[0x4] & 0xffL) << 24;
        msb |= (bytes[0x5] & 0xffL) << 16;
        msb |= (bytes[0x6] & 0xffL) << 8;
        msb |= (bytes[0x7] & 0xffL);

        lsb |= (bytes[0x8] & 0xffL) << 56;
        lsb |= (bytes[0x9] & 0xffL) << 48;
        lsb |= (bytes[0xa] & 0xffL) << 40;
        lsb |= (bytes[0xb] & 0xffL) << 32;
        lsb |= (bytes[0xc] & 0xffL) << 24;
        lsb |= (bytes[0xd] & 0xffL) << 16;
        lsb |= (bytes[0xe] & 0xffL) << 8;
        lsb |= (bytes[0xf] & 0xffL);

        return new Ulid(msb, lsb);
    }

    /**
     * 将规范字符串转换为 ULID。<br>
     * 输入字符串必须为 26 个字符，并且只能包含 Crockford Base32 字母表中的字符。<br>
     * 输入字符串的第一个字符必须在 0 到 7 之间。<br>
     * @param string 一个规范字符串
     * @return ULID 实例
     * @throws IllegalArgumentException 如果输入字符串无效
     * @see <a href="https://www.crockford.com/base32.html">Crockford 的 Base 32</a>
     */
    public static Ulid from(String string) {

        final char[] chars = toCharArray(string);

        long time = 0;
        long random0 = 0;
        long random1 = 0;

        time |= (long) ALPHABET_VALUES[chars[0x00]] << 45;
        time |= (long) ALPHABET_VALUES[chars[0x01]] << 40;
        time |= (long) ALPHABET_VALUES[chars[0x02]] << 35;
        time |= (long) ALPHABET_VALUES[chars[0x03]] << 30;
        time |= (long) ALPHABET_VALUES[chars[0x04]] << 25;
        time |= (long) ALPHABET_VALUES[chars[0x05]] << 20;
        time |= (long) ALPHABET_VALUES[chars[0x06]] << 15;
        time |= (long) ALPHABET_VALUES[chars[0x07]] << 10;
        time |= (long) ALPHABET_VALUES[chars[0x08]] << 5;
        time |= (long) ALPHABET_VALUES[chars[0x09]];

        random0 |= (long) ALPHABET_VALUES[chars[0x0a]] << 35;
        random0 |= (long) ALPHABET_VALUES[chars[0x0b]] << 30;
        random0 |= (long) ALPHABET_VALUES[chars[0x0c]] << 25;
        random0 |= (long) ALPHABET_VALUES[chars[0x0d]] << 20;
        random0 |= (long) ALPHABET_VALUES[chars[0x0e]] << 15;
        random0 |= (long) ALPHABET_VALUES[chars[0x0f]] << 10;
        random0 |= (long) ALPHABET_VALUES[chars[0x10]] << 5;
        random0 |= (long) ALPHABET_VALUES[chars[0x11]];

        random1 |= (long) ALPHABET_VALUES[chars[0x12]] << 35;
        random1 |= (long) ALPHABET_VALUES[chars[0x13]] << 30;
        random1 |= (long) ALPHABET_VALUES[chars[0x14]] << 25;
        random1 |= (long) ALPHABET_VALUES[chars[0x15]] << 20;
        random1 |= (long) ALPHABET_VALUES[chars[0x16]] << 15;
        random1 |= (long) ALPHABET_VALUES[chars[0x17]] << 10;
        random1 |= (long) ALPHABET_VALUES[chars[0x18]] << 5;
        random1 |= (long) ALPHABET_VALUES[chars[0x19]];

        final long msb = (time << 16) | (random0 >>> 24);
        final long lsb = (random0 << 40) | (random1 & 0xffffffffffL);

        return new Ulid(msb, lsb);
    }

    /**
     * 将 ULID 转换为 UUID。<br>
     * ULID 与 {@link UUID} 兼容，具有 128 位长度。<br>
     * 若需要符合 RFC-4122 的 UUIDv4，可使用：{@code Ulid.toRfc4122().toUuid()}。
     * @return UUID 实例
     */
    public UUID toUuid() {
        return new UUID(this.msb, this.lsb);
    }

    /**
     * 将 ULID 转换为字节数组。
     * @return 包含 16 字节的数组
     */
    public byte[] toBytes() {

        final byte[] bytes = new byte[ULID_BYTES];

        bytes[0x0] = (byte) (msb >>> 56);
        bytes[0x1] = (byte) (msb >>> 48);
        bytes[0x2] = (byte) (msb >>> 40);
        bytes[0x3] = (byte) (msb >>> 32);
        bytes[0x4] = (byte) (msb >>> 24);
        bytes[0x5] = (byte) (msb >>> 16);
        bytes[0x6] = (byte) (msb >>> 8);
        bytes[0x7] = (byte) (msb);

        bytes[0x8] = (byte) (lsb >>> 56);
        bytes[0x9] = (byte) (lsb >>> 48);
        bytes[0xa] = (byte) (lsb >>> 40);
        bytes[0xb] = (byte) (lsb >>> 32);
        bytes[0xc] = (byte) (lsb >>> 24);
        bytes[0xd] = (byte) (lsb >>> 16);
        bytes[0xe] = (byte) (lsb >>> 8);
        bytes[0xf] = (byte) (lsb);

        return bytes;
    }

    /**
     * 将 ULID 转换为大写规范字符串。<br>
     * 输出字符串长度为 26 个字符，仅包含 Crockford Base32 字母表中的字符。<br>
     * 若需小写字符串，请使用快捷方法 {@code Ulid#toLowerCase()}， 而不是 {@code Ulid#toString().toLowerCase()}。<br>
     * @return ULID 字符串
     * @see <a href="https://www.crockford.com/base32.html">Crockford 的 Base 32</a>
     */
    @Override
    public String toString() {
        return toString(ALPHABET_UPPERCASE);
    }

    /**
     * 将 ULID 转换为小写规范字符串。<br>
     * 输出字符串长度为 26 个字符，仅包含 Crockford Base32 字母表中的字符。<br>
     * 它比 {@code Ulid.toString().toLowerCase()} 更高效。<br>
     * @return 字符串
     * @see <a href="https://www.crockford.com/base32.html">Crockford 的 Base 32</a>
     */
    public String toLowerCase() {
        return toString(ALPHABET_LOWERCASE);
    }

    /**
     * 将 ULID 转换为与 UUIDv4 兼容的另一 ULID。<br>
     * 返回的 ULID 字节符合 RFC-4122 第 4 版规范。<br>
     * 若需要获得符合 RFC-4122 的 UUIDv4，可使用：{@code Ulid.toRfc4122().toUuid()}。<br>
     * <b>注意：</b> 使用此方法会更改 ULID 的 6 位，因此无法还原原始 ULID。
     * @return 新的 ULID 实例
     * @see <a href="https://www.rfc-editor.org/rfc/rfc4122">RFC-4122</a>
     */
    public Ulid toRfc4122() {

        // set the 4 most significant bits of the 7th byte to 0, 1, 0 and 0
        final long msb4 = (this.msb & 0xffffffffffff0fffL) | 0x0000000000004000L; // RFC-4122 version 4
        // set the 2 most significant bits of the 9th byte to 1 and 0
        final long lsb4 = (this.lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // RFC-4122 variant 2

        return new Ulid(msb4, lsb4);
    }

    /**
     * 返回 ULID 的创建时间点。<br>
     * 创建时间从时间部分提取。<br>
     * @return {@link Instant} 实例
     */
    public Instant getInstant() {
        return Instant.ofEpochMilli(this.getTime());
    }

    /**
     * 获得时间部分字符串代表的时间点。<br>
     * @param string 时间部分字符串
     * @return {@link Instant} 实例
     * @throws IllegalArgumentException 如果输入字符串无效
     */
    public static Instant getInstant(String string) {
        return Instant.ofEpochMilli(getTime(string));
    }

    /**
     * 以数字形式返回时间部分。<br>
     * 时间部分为 0 到 2^48-1 之间的数字，等价于自 1970-01-01（Unix 纪元）以来的毫秒数。
     * @return 毫秒数
     */
    public long getTime() {
        return this.msb >>> 16;
    }

    /**
     * 获得时间部分字符串代表的字节数组。<br>
     * 时间部分为 0 到 2^48-1 之间的数字，等价于自 1970-01-01（Unix 纪元）以来的毫秒数。
     * @param string 时间部分字符串
     * @return 毫秒数
     * @throws IllegalArgumentException 如果输入字符串无效
     */
    public static long getTime(String string) {

        final char[] chars = toCharArray(string);

        long time = 0;

        time |= (long) ALPHABET_VALUES[chars[0x00]] << 45;
        time |= (long) ALPHABET_VALUES[chars[0x01]] << 40;
        time |= (long) ALPHABET_VALUES[chars[0x02]] << 35;
        time |= (long) ALPHABET_VALUES[chars[0x03]] << 30;
        time |= (long) ALPHABET_VALUES[chars[0x04]] << 25;
        time |= (long) ALPHABET_VALUES[chars[0x05]] << 20;
        time |= (long) ALPHABET_VALUES[chars[0x06]] << 15;
        time |= (long) ALPHABET_VALUES[chars[0x07]] << 10;
        time |= (long) ALPHABET_VALUES[chars[0x08]] << 5;
        time |= (long) ALPHABET_VALUES[chars[0x09]];

        return time;
    }

    /**
     * 获得随机部分的字节数组。<br>
     * 随机部分是 10 字节（80 位）数组。
     * @return 字节数组
     */
    public byte[] getRandom() {

        final byte[] bytes = new byte[RANDOM_BYTES];

        bytes[0x0] = (byte) (msb >>> 8);
        bytes[0x1] = (byte) (msb);

        bytes[0x2] = (byte) (lsb >>> 56);
        bytes[0x3] = (byte) (lsb >>> 48);
        bytes[0x4] = (byte) (lsb >>> 40);
        bytes[0x5] = (byte) (lsb >>> 32);
        bytes[0x6] = (byte) (lsb >>> 24);
        bytes[0x7] = (byte) (lsb >>> 16);
        bytes[0x8] = (byte) (lsb >>> 8);
        bytes[0x9] = (byte) (lsb);

        return bytes;
    }

    /**
     * 获得随机部分字符串代表的字节数组。<br>
     * 随机部分是 10 字节（80 位）数组。<br>
     * @param string 随机部分字符串
     * @return 字节数组
     * @throws IllegalArgumentException 如果输入字符串无效
     */
    public static byte[] getRandom(String string) {

        final char[] chars = toCharArray(string);

        long random0 = 0;
        long random1 = 0;

        random0 |= (long) ALPHABET_VALUES[chars[0x0a]] << 35;
        random0 |= (long) ALPHABET_VALUES[chars[0x0b]] << 30;
        random0 |= (long) ALPHABET_VALUES[chars[0x0c]] << 25;
        random0 |= (long) ALPHABET_VALUES[chars[0x0d]] << 20;
        random0 |= (long) ALPHABET_VALUES[chars[0x0e]] << 15;
        random0 |= (long) ALPHABET_VALUES[chars[0x0f]] << 10;
        random0 |= (long) ALPHABET_VALUES[chars[0x10]] << 5;
        random0 |= (long) ALPHABET_VALUES[chars[0x11]];

        random1 |= (long) ALPHABET_VALUES[chars[0x12]] << 35;
        random1 |= (long) ALPHABET_VALUES[chars[0x13]] << 30;
        random1 |= (long) ALPHABET_VALUES[chars[0x14]] << 25;
        random1 |= (long) ALPHABET_VALUES[chars[0x15]] << 20;
        random1 |= (long) ALPHABET_VALUES[chars[0x16]] << 15;
        random1 |= (long) ALPHABET_VALUES[chars[0x17]] << 10;
        random1 |= (long) ALPHABET_VALUES[chars[0x18]] << 5;
        random1 |= (long) ALPHABET_VALUES[chars[0x19]];

        final byte[] bytes = new byte[RANDOM_BYTES];

        bytes[0x0] = (byte) (random0 >>> 32);
        bytes[0x1] = (byte) (random0 >>> 24);
        bytes[0x2] = (byte) (random0 >>> 16);
        bytes[0x3] = (byte) (random0 >>> 8);
        bytes[0x4] = (byte) (random0);

        bytes[0x5] = (byte) (random1 >>> 32);
        bytes[0x6] = (byte) (random1 >>> 24);
        bytes[0x7] = (byte) (random1 >>> 16);
        bytes[0x8] = (byte) (random1 >>> 8);
        bytes[0x9] = (byte) (random1);

        return bytes;
    }

    /**
     * 返回最高有效位数字。
     * @return 最高有效位数字。
     */
    public long getMostSignificantBits() {
        return this.msb;
    }

    /**
     * 返回最低有效位数字。
     * @return 最低有效位数字
     */
    public long getLeastSignificantBits() {
        return this.lsb;
    }

    /**
     * 通过递增当前 ULID 的随机部分生成一个新 ULID。
     * <p>
     * 由于随机部分是 80 位：
     * <ul>
     * <li>(1) 每毫秒最多可生成 1208925819614629174706176（2^80）个 ULID；
     * <li>(2) 假设每毫秒生成 10 亿个 ULID，也能有 99.999999999999992% 的概率保证单调递增。
     * </ul>
     * <p>
     * 因此不会抛出规范建议的错误。当 80 位随机数溢出时，将简单地将时间部分加一以保持单调性。
     * @return 一个新的 ULID
     */
    public Ulid increment() {
        long newMsb = this.msb;
        long newLsb = this.lsb + 1; // increment the LEAST significant bits
        if (newLsb == INCREMENT_OVERFLOW) {
            newMsb += 1; // increment the MOST significant bits
        }
        return new Ulid(newMsb, newLsb);
    }

    /**
     * 检查输入字符串是否为有效 ULID。<br>
     * 输入字符串必须为 26 个字符，并且只能包含 Crockford Base32 字母表中的字符。<br>
     * 第一个字符必须在 0 到 7 之间。<br>
     * @param string 一个规范字符串
     * @return 如果输入有效，返回 true
     * @see <a href="https://www.crockford.com/base32.html">Crockford 的 Base 32</a>
     */
    public static boolean isValid(String string) {
        return string != null && isValidCharArray(string.toCharArray());
    }

    /**
     * 返回 ULID 的哈希值。
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        final long bits = msb ^ lsb;
        return (int) (bits ^ (bits >>> 32));
    }

    /**
     * 判断另一个 ULID 是否与当前对象相等。
     * @param other 比较的对象
     * @return 如果对象相等返回{@code true}，否则返回{@code false}
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass() != Ulid.class) {
            return false;
        }
        Ulid that = (Ulid) other;
        if (lsb != that.lsb) {
            return false;
        } else if (msb != that.msb) {
            return false;
        }
        return true;
    }

    /**
     * 比较两个 ULID 的大小，按无符号 128 位整数比较。<br>
     * 若两个 ULID 不同的第一个最高有效字节中，第一个 ULID 的字节更大，则视为第一个更大。<br>
     * @param that 要比较的 ULID
     * @return -1、0 或 1（分别表示小于、等于、大于）
     */
    @Override
    public int compareTo(Ulid that) {

        // used to compare as UNSIGNED longs
        final long min = 0x8000000000000000L;

        final long a = this.msb + min;
        final long b = that.msb + min;

        if (a > b)
            return 1;
        else if (a < b)
            return -1;

        final long c = this.lsb + min;
        final long d = that.lsb + min;

        if (c > d)
            return 1;
        else if (c < d)
            return -1;

        return 0;
    }

    String toString(char[] alphabet) {

        final char[] chars = new char[ULID_CHARS];

        long time = this.msb >>> 16;
        long random0 = ((this.msb & 0xffffL) << 24) | (this.lsb >>> 40);
        long random1 = (this.lsb & 0xffffffffffL);

        chars[0x00] = alphabet[(int) (time >>> 45 & 0b11111)];
        chars[0x01] = alphabet[(int) (time >>> 40 & 0b11111)];
        chars[0x02] = alphabet[(int) (time >>> 35 & 0b11111)];
        chars[0x03] = alphabet[(int) (time >>> 30 & 0b11111)];
        chars[0x04] = alphabet[(int) (time >>> 25 & 0b11111)];
        chars[0x05] = alphabet[(int) (time >>> 20 & 0b11111)];
        chars[0x06] = alphabet[(int) (time >>> 15 & 0b11111)];
        chars[0x07] = alphabet[(int) (time >>> 10 & 0b11111)];
        chars[0x08] = alphabet[(int) (time >>> 5 & 0b11111)];
        chars[0x09] = alphabet[(int) (time & 0b11111)];

        chars[0x0a] = alphabet[(int) (random0 >>> 35 & 0b11111)];
        chars[0x0b] = alphabet[(int) (random0 >>> 30 & 0b11111)];
        chars[0x0c] = alphabet[(int) (random0 >>> 25 & 0b11111)];
        chars[0x0d] = alphabet[(int) (random0 >>> 20 & 0b11111)];
        chars[0x0e] = alphabet[(int) (random0 >>> 15 & 0b11111)];
        chars[0x0f] = alphabet[(int) (random0 >>> 10 & 0b11111)];
        chars[0x10] = alphabet[(int) (random0 >>> 5 & 0b11111)];
        chars[0x11] = alphabet[(int) (random0 & 0b11111)];

        chars[0x12] = alphabet[(int) (random1 >>> 35 & 0b11111)];
        chars[0x13] = alphabet[(int) (random1 >>> 30 & 0b11111)];
        chars[0x14] = alphabet[(int) (random1 >>> 25 & 0b11111)];
        chars[0x15] = alphabet[(int) (random1 >>> 20 & 0b11111)];
        chars[0x16] = alphabet[(int) (random1 >>> 15 & 0b11111)];
        chars[0x17] = alphabet[(int) (random1 >>> 10 & 0b11111)];
        chars[0x18] = alphabet[(int) (random1 >>> 5 & 0b11111)];
        chars[0x19] = alphabet[(int) (random1 & 0b11111)];

        return new String(chars);
    }

    static char[] toCharArray(String string) {
        char[] chars = string == null ? null : string.toCharArray();
        if (!isValidCharArray(chars)) {
            throw new IllegalArgumentException(String.format("Invalid ULID: \"%s\"", string));
        }
        return chars;
    }

    /**
     * 检查字符数组是否为有效 ULID。<br>
     * 有效的 ULID 字符串是 26 个字符，来自 Crockford Base32 字符集。<br>
     * 输入字符串的第一个字符必须在 0 到 7 之间。<br>
     * @param 字符数组
     * @return 字符数组是否为有效
     */
    static boolean isValidCharArray(final char[] chars) {

        if (chars == null || chars.length != ULID_CHARS) {
            return false; // null or wrong size!
        }

        for (int i = 0; i < chars.length; i++) {
            try {
                if (ALPHABET_VALUES[chars[i]] == -1) {
                    return false; // invalid character!
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return false; // Multibyte character!
            }
        }

        // The time component has 48 bits.
        // The base32 encoded time component has 50 bits.
        // The time component cannot be greater than than 2^48-1.
        // So the 2 first bits of the base32 decoded time component must be ZERO.
        // As a consequence, the 1st char of the input string must be between 0 and 7.
        if ((ALPHABET_VALUES[chars[0]] & 0b11000) != 0) {
            // ULID specification:
            // "Any attempt to decode or encode a ULID larger than this (time > 2^48-1)
            // should be rejected by all implementations, to prevent overflow bugs."
            return false; // time overflow!
        }

        return true; // It seems to be OK.
    }
}