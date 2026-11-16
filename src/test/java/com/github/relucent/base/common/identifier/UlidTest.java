package com.github.relucent.base.common.identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class UlidTest {

    /** ULID 规范中的示例值（时间 2016-07-30T22:36:16.385Z） */
    private static final String CANONICAL = "01ARYZ6S41TSV4RRFFQ69G5FAV";
    /** 示例值对应的时间戳 */
    private static final long CANONICAL_TIME = 1469918176385L;
    /** 示例值对应的最高有效位 */
    private static final long CANONICAL_MSB = 0x01563df36481d676L;
    /** 示例值对应的字节形态 */
    private static final String CANONICAL_BYTES = "01563df36481d6764c61efb99302bd5b";
    /** 示例值对应的随机部分 */
    private static final String CANONICAL_RANDOM = "d6764c61efb99302bd5b";
    /** 时间部分最大值 2^48-1 */
    private static final long MAX_TIME = (1L << 48) - 1;

    @Test
    public void uniqueTest() {
        int count = 10000;
        List<Ulid> idList = new ArrayList<>();
        Set<String> idSet = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            Ulid ulid = Ulid.create();
            idSet.add(ulid.toString());
            idList.add(ulid);
        }
        // create() 保证严格单调递增
        for (int i = 1; i < count; i++) {
            Assert.assertTrue("第 " + i + " 个 ULID 未严格递增", idList.get(i - 1).compareTo(idList.get(i)) < 0);
        }
        Assert.assertEquals(count, idSet.size());
    }

    @Test
    public void canonicalVectorTest() {
        Ulid ulid = Ulid.from(CANONICAL);
        Assert.assertEquals(CANONICAL_TIME, ulid.getTime());
        Assert.assertEquals(CANONICAL_TIME, ulid.getInstant().toEpochMilli());
        Assert.assertEquals(CANONICAL_MSB, ulid.getMostSignificantBits());
        Assert.assertEquals(CANONICAL_RANDOM, toHex(ulid.getRandom()));
        // 静态方法提取时间/随机部分
        Assert.assertEquals(CANONICAL_TIME, Ulid.getTime(CANONICAL));
        Assert.assertEquals(CANONICAL_TIME, Ulid.getInstant(CANONICAL).toEpochMilli());
        Assert.assertEquals(CANONICAL_RANDOM, toHex(Ulid.getRandom(CANONICAL)));
        // 编码回原字符串
        Assert.assertEquals(CANONICAL, ulid.toString());
        Assert.assertEquals(CANONICAL.toLowerCase(Locale.ENGLISH), ulid.toLowerCase());
        // 字节形态
        Assert.assertEquals(CANONICAL_BYTES, toHex(ulid.toBytes()));
        Assert.assertEquals(ulid, Ulid.from(ulid.toBytes()));
    }

    @Test
    public void randomBitRoundTripTest() {
        Random random = new Random(20261104L);
        for (int i = 0; i < 20000; i++) {
            long msb = random.nextLong();
            long lsb = random.nextLong();
            Ulid ulid = new Ulid(msb, lsb);
            // 字符串编解码往返
            String string = ulid.toString();
            Assert.assertEquals(Ulid.ULID_CHARS, string.length());
            Ulid parsed = Ulid.from(string);
            Assert.assertEquals(msb, parsed.getMostSignificantBits());
            Assert.assertEquals(lsb, parsed.getLeastSignificantBits());
            Assert.assertEquals(ulid, parsed);
            Assert.assertEquals(ulid.hashCode(), parsed.hashCode());
            // 字节数组往返
            Assert.assertEquals(ulid, Ulid.from(ulid.toBytes()));
            // 随机部分必须等于字节数组的后 10 字节
            Assert.assertEquals(toHex(ulid.toBytes()).substring(12), toHex(ulid.getRandom()));
            // 时间/随机部分可以被独立提取
            Assert.assertEquals(ulid.getTime(), Ulid.getTime(string));
            Assert.assertEquals(toHex(ulid.getRandom()), toHex(Ulid.getRandom(string)));
        }
    }

    @Test
    public void timeBoundaryTest() {
        Ulid zero = new Ulid(0L, new byte[Ulid.RANDOM_BYTES]);
        Assert.assertEquals(0L, zero.getTime());
        Assert.assertEquals("00000000000000000000000000", zero.toString());
        Assert.assertEquals(Ulid.MIN, zero);

        Ulid max = new Ulid(MAX_TIME, filled(0xff));
        Assert.assertEquals(MAX_TIME, max.getTime());
        Assert.assertEquals(MAX_TIME, Ulid.from(max.toString()).getTime());
        Assert.assertEquals("7ZZZZZZZZZ", max.toString().substring(0, Ulid.TIME_CHARS));
        Assert.assertEquals(ULID_MAX_STRING, max.toString());
        Assert.assertEquals(Ulid.MAX, max);
    }

    /** 最大 ULID 的规范字符串：首字符 7，其后 25 个 Z */
    private static final String ULID_MAX_STRING = "7ZZZZZZZZZZZZZZZZZZZZZZZZZ";

    @Test
    public void minMaxTest() {
        for (long time : new long[] { 0L, 1L, CANONICAL_TIME, MAX_TIME }) {
            Ulid min = Ulid.min(time);
            Ulid max = Ulid.max(time);
            // 时间部分保真
            Assert.assertEquals(time, min.getTime());
            Assert.assertEquals(time, max.getTime());
            // 随机部分：min 全 0，max 全 1
            Assert.assertEquals("00000000000000000000", toHex(min.getRandom()));
            Assert.assertEquals("ffffffffffffffffffff", toHex(max.getRandom()));
            // 同一时间下 min <= 任意 <= max
            Ulid middle = new Ulid(time, filled(0x5a));
            Assert.assertTrue(min.compareTo(middle) <= 0);
            Assert.assertTrue(middle.compareTo(max) <= 0);
        }
        // 越界时间必须拒绝，而不是静默回绕成一个看似合法的边界值
        assertIllegalArgument(() -> Ulid.min(-1L));
        assertIllegalArgument(() -> Ulid.min(MAX_TIME + 1));
        assertIllegalArgument(() -> Ulid.max(-1L));
        assertIllegalArgument(() -> Ulid.max(MAX_TIME + 1));
    }

    @Test
    public void invalidInputTest() {
        final String valid = CANONICAL;
        // 时间越界
        assertIllegalArgument(() -> new Ulid(-1L, new byte[Ulid.RANDOM_BYTES]));
        assertIllegalArgument(() -> new Ulid(MAX_TIME + 1, new byte[Ulid.RANDOM_BYTES]));
        // 随机字节非法
        assertIllegalArgument(() -> new Ulid(1L, (byte[]) null));
        assertIllegalArgument(() -> new Ulid(1L, new byte[Ulid.RANDOM_BYTES - 1]));
        // 字符串非法
        assertIllegalArgument(() -> Ulid.from((String) null));
        assertIllegalArgument(() -> Ulid.from(""));
        assertIllegalArgument(() -> Ulid.from(valid.substring(0, Ulid.ULID_CHARS - 1)));
        assertIllegalArgument(() -> Ulid.from(valid + "0"));
        assertIllegalArgument(() -> Ulid.from("U" + valid.substring(1))); // U 不在 Crockford 字母表
        assertIllegalArgument(() -> Ulid.from("81ARYZ6S41TSV4RRFFQ69G5FAV")); // 首字符 >7，时间溢出
        assertIllegalArgument(() -> Ulid.from("\u4e2d" + valid.substring(1))); // 多字节字符
        // 时间/随机提取同样要求完整字符串，而不是 10/16 位片段
        assertIllegalArgument(() -> Ulid.getTime(valid.substring(0, Ulid.TIME_CHARS)));
        assertIllegalArgument(() -> Ulid.getRandom(valid.substring(Ulid.TIME_CHARS)));
        // 字节数组非法
        assertIllegalArgument(() -> Ulid.from((byte[]) null));
        assertIllegalArgument(() -> Ulid.from(new byte[Ulid.ULID_BYTES - 1]));
        // isValid 不抛异常
        Assert.assertTrue(Ulid.isValid(valid));
        Assert.assertTrue(Ulid.isValid(valid.toLowerCase(Locale.ENGLISH)));
        Assert.assertFalse(Ulid.isValid((String) null));
        Assert.assertFalse(Ulid.isValid("81ARYZ6S41TSV4RRFFQ69G5FAV"));
        Assert.assertFalse(Ulid.isValid(valid.substring(1)));
    }

    @Test
    public void compareToTest() {
        // 跨越符号位的无符号比较
        Ulid low = new Ulid(0x0000000000000001L, 0L);
        Ulid high = new Ulid(0x8000000000000001L, 0L);
        Assert.assertTrue(low.compareTo(high) < 0);
        Assert.assertTrue(high.compareTo(low) > 0);

        // 低 64 位同样按无符号比较
        Ulid sameMsbSmall = new Ulid(0L, 1L);
        Ulid sameMsbBig = new Ulid(0L, 0x8000000000000000L);
        Assert.assertTrue(sameMsbSmall.compareTo(sameMsbBig) < 0);

        Assert.assertEquals(0, low.compareTo(new Ulid(0x0000000000000001L, 0L)));
        Assert.assertEquals(0, Ulid.MIN.compareTo(Ulid.MIN));
        Assert.assertTrue(Ulid.MAX.compareTo(Ulid.MIN) > 0);

        // 规范字符串的字典序必须与 Ulid 的无符号序一致
        Random random = new Random(20261105L);
        List<Ulid> ulids = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            ulids.add(new Ulid(random.nextLong(), random.nextLong()));
        }
        ulids.sort(null);
        List<String> byUlid = new ArrayList<>();
        for (Ulid ulid : ulids) {
            byUlid.add(ulid.toString());
        }
        List<String> byString = new ArrayList<>(byUlid);
        byString.sort(null);
        Assert.assertEquals(byString, byUlid);
    }

    @Test
    public void incrementTest() {
        Ulid ulid = new Ulid(CANONICAL_TIME, filled(0x00));
        Ulid next = ulid.increment();
        // 时间部分不变，随机部分加一
        Assert.assertEquals(ulid.getTime(), next.getTime());
        Assert.assertEquals(1, next.getRandom()[Ulid.RANDOM_BYTES - 1] & 0xff);
        Assert.assertTrue(ulid.compareTo(next) < 0);

        // 低 64 位溢出时进位到 msb
        Ulid overflow = new Ulid(0L, -1L).increment();
        Assert.assertEquals(1L, overflow.getMostSignificantBits());
        Assert.assertEquals(0L, overflow.getLeastSignificantBits());
    }

    @Test
    public void uuidInteropTest() {
        Ulid ulid = Ulid.from(CANONICAL);
        Assert.assertEquals(ulid, Ulid.from(ulid.toUuid()));
        Assert.assertEquals(ulid.getMostSignificantBits(), ulid.toUuid().getMostSignificantBits());
        Assert.assertEquals(ulid.getLeastSignificantBits(), ulid.toUuid().getLeastSignificantBits());

        // toRfc4122 只会改动版本位（msb 的 4 位）与变体位（lsb 的 2 位），其余位必须保持不变
        Ulid rfc = ulid.toRfc4122();
        Assert.assertEquals(4, rfc.toUuid().version());
        Assert.assertEquals(2, rfc.toUuid().variant());
        Assert.assertEquals(ulid.getMostSignificantBits() & 0xffffffffffff0fffL,
                rfc.getMostSignificantBits() & 0xffffffffffff0fffL);
        Assert.assertEquals(ulid.getLeastSignificantBits() & 0x3fffffffffffffffL,
                rfc.getLeastSignificantBits() & 0x3fffffffffffffffL);
        Assert.assertTrue(diffBits(ulid, rfc) <= 6);
    }

    @Test
    public void fastTest() {
        int count = 5000;
        Set<String> idSet = new HashSet<>(count);
        for (int i = 0; i < count; i++) {
            Ulid ulid = Ulid.fast();
            Assert.assertTrue(Ulid.isValid(ulid.toString()));
            Assert.assertTrue(Math.abs(ulid.getTime() - System.currentTimeMillis()) < 60_000L);
            idSet.add(ulid.toString());
        }
        Assert.assertEquals(count, idSet.size());
    }

    // ------------------------------------------------------------------

    private static byte[] filled(int value) {
        byte[] bytes = new byte[Ulid.RANDOM_BYTES];
        Arrays.fill(bytes, (byte) value);
        return bytes;
    }

    private static int diffBits(Ulid a, Ulid b) {
        return Long.bitCount(a.getMostSignificantBits() ^ b.getMostSignificantBits())
                + Long.bitCount(a.getLeastSignificantBits() ^ b.getLeastSignificantBits());
    }

    private static String toHex(byte[] bytes) {
        final char[] digits = "0123456789abcdef".toCharArray();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(digits[(b >> 4) & 0xf]).append(digits[b & 0xf]);
        }
        return sb.toString();
    }

    private static void assertIllegalArgument(Runnable runnable) {
        try {
            runnable.run();
            Assert.fail("预期抛出 IllegalArgumentException，但没有抛出");
        } catch (IllegalArgumentException expected) {
            // 符合预期
        }
    }
}
