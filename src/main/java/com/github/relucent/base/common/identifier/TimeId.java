package com.github.relucent.base.common.identifier;

import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.net.NetworkUtil;

/**
 * 时间序列ID生成器(20位)<br>
 * 时间戳+循环计数+随机数+网络地址+进程号<br>
 * 优势：<br>
 * TimeId 因此是有序的（有序但不连贯）<br>
 * TimeId 使用了和 SnowflakeIdWorker 类似的时间戳算法，但是冲突的概率更低<br>
 * TimeId 与 UUID 相比，大小从36个符号减少到20个符号<br>
 * 劣势：<br>
 * TimeId 是字符串，相对 SnowflakeIdWorker（ 长整型） 占用更大的空间，但是比UUID紧凑
 * @author YYL
 */
public class TimeId {

    private static final int RADIX36 = 36;
    private static final int TIMESTAMP_LENGTH = 9;
    private static final int COUNTER_LENGTH = 5;
    private static final int MAC_LENGTH = 2;
    private static final int PID_LENGTH = 1;
    private static final int RANDOM_LENGTH = 3;
    private static final int COUNTER_MOD = computeRadix36Mod(COUNTER_LENGTH);
    private static final int MAC_MOD = computeRadix36Mod(MAC_LENGTH);
    private static final int PID_MOD = computeRadix36Mod(PID_LENGTH);
    private static final int RANDOM_MOD = computeRadix36Mod(RANDOM_LENGTH);
    private static final char ZERO_CHAR = '0';

    /** 循环序列号 */
    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    /** 此类用来创建随机数的随机数生成器 */
    private static class Holder {
        static final SecureRandom NUMBER_GENERATOR = new SecureRandom();
        static final long MAC_VALUE = getMac();
        static final long PID_VALUE = getPid();
    }

    /**
     * 构造函数
     */
    protected TimeId() {

    }

    /**
     * 生成 TimeId
     * @return TimeId 字符串
     */
    public static String nextId() {
        StringBuilder buffer = new StringBuilder();
        append(buffer, timeGen(), TIMESTAMP_LENGTH);
        append(buffer, Math.abs(NEXT_COUNTER.getAndIncrement() % COUNTER_MOD), COUNTER_LENGTH);
        append(buffer, Math.abs(Holder.MAC_VALUE % MAC_MOD), MAC_LENGTH);
        append(buffer, Math.abs(Holder.PID_VALUE % PID_MOD), PID_LENGTH);
        append(buffer, Math.abs(Holder.NUMBER_GENERATOR.nextLong() % RANDOM_MOD), RANDOM_LENGTH);
        return buffer.toString();
    }

    /**
     * 将数值添加到字符串缓冲中
     * @param buffer 字符串缓冲中
     * @param value 数值
     * @param length 添加的位数
     */
    private static void append(StringBuilder buffer, long value, int length) {
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

    /**
     * 计算模数
     * @param length 长度
     * @return 进制模(32)
     */
    private static int computeRadix36Mod(int length) {
        int value = 1;
        for (int i = 0; i < length; i++) {
            value *= RADIX36;
        }
        return value - 1;
    }
}
