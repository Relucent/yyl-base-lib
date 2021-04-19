package com.github.relucent.base.common.identifier;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.net.NetworkUtil;

/**
 * 时间序列ID生成器<br>
 * 时间戳+循环计数+随机数+网络地址+进程号<br>
 */
public class TimeId {

    private static final int RADIX36 = 36;
    private static final long RADIX36_MOD1 = RADIX36 - 1;
    private static final long RADIX36_MOD2 = RADIX36 * RADIX36 - 1;
    private static final long RADIX36_MOD3 = RADIX36 * RADIX36 * RADIX36 - 1;
    private static final int TIMESTAMP_LENGTH = 9;
    private static final int COUNTER_LENGTH = 3;
    private static final int MAC_LENGTH = 2;
    private static final int PID_LENGTH = 1;
    private static final int RANDOM_LENGTH = 3;
    private static final char ZERO_CHAR = '0';

    /** 循环序列号 */
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    /** 此类用来创建随机数的随机数生成器 */
    private static class Holder {
        static final SecureRandom NUMBER_GENERATOR = new SecureRandom();
        static final long MAC_VALUE = getMac();
        static final long PID_VALUE = getPid();
    }

    private final long timestamp;
    private final long counter;
    private final long mac;
    private final long pid;
    private final long random;

    /**
     * 构造函数
     * @param timestamp 时间戳
     * @param counter 毫秒内计数
     * @param mac 网卡ID
     * @param pid 进程ID
     * @param random 随机数
     */
    private TimeId(long timestamp, long counter, long mac, long pid, long random) {
        this.timestamp = timestamp;
        this.counter = Math.abs(counter % RADIX36_MOD3);
        this.mac = Math.abs(mac % RADIX36_MOD1);
        this.pid = Math.abs(pid % RADIX36_MOD1);
        this.random = Math.abs(random % RADIX36_MOD2);
    }

    /**
     * 生成序列ID
     * @return 序列ID
     */
    public static TimeId generate() {
        long timestamp = timeGen();
        long counter = NEXT_COUNTER.getAndIncrement();
        long mac = Holder.MAC_VALUE;
        long pid = Holder.PID_VALUE;
        long random = Holder.NUMBER_GENERATOR.nextLong();
        return new TimeId(timestamp, counter, mac, pid, random);
    }

    /**
     * 输出对象的字符串形式
     * @return 对象的字符串形式
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        append(buffer, timestamp, TIMESTAMP_LENGTH);
        append(buffer, counter, COUNTER_LENGTH);
        append(buffer, mac, MAC_LENGTH);
        append(buffer, pid, PID_LENGTH);
        append(buffer, random, RANDOM_LENGTH);
        return buffer.toString();
    }

    /**
     * 将数值添加到字符串缓冲中
     * @param buffer 字符串缓冲中
     * @param value 数值
     * @param length 添加的位数
     */
    private void append(StringBuilder buffer, long value, int length) {
        buffer.append(StringUtil.leftPad(Long.toString(value, RADIX36), length, ZERO_CHAR));
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获得MAC值
     * @return MAC值
     */
    private static long getMac() {
        long mac = 0;
        try {
            byte[] address = NetworkUtil.getHardwareAddress();
            mac = new BigInteger(address).longValue();
        } catch (Exception | Error e) {
            mac = Holder.NUMBER_GENERATOR.nextLong();
        }
        return mac;
    }

    /**
     * 获得当前进程ID
     * @return 进程ID
     */
    private static long getPid() {
        long pid = 0;
        String name = ManagementFactory.getRuntimeMXBean().getName();
        try {
            pid = Long.parseLong(name.split("@")[0]);
        } catch (Exception | Error e) {
            pid = Holder.NUMBER_GENERATOR.nextLong();
        }
        return pid;
    }
}
