package com.github.relucent.base.common.identifier;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import com.github.relucent.base.common.jvm.JvmUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.net.NetworkUtil;

/**
 * 日期时间序列ID生成器<br>
 * 格式为日期(年月日时分秒)+毫秒(3位)+毫秒内计数(2位)+{后缀(可选,默认2位环境位+4位随机位)}<br>
 */
public class DateIdWorker {

    private static final Logger LOGGER = Logger.getLogger(DateIdWorker.class);

    /** 默认ID后缀生成器 */
    private static final Supplier<String> DEFAULT_SUFFIXER = defaultSuffixer();
    /** 日期格式 */
    private static final String DATETIME_PATTERN = "yyyyMMddHHmmssSSS";
    /** 日期格式化 */
    private static final ThreadLocal<DateFormat> DATETIME_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat(DATETIME_PATTERN));

    /** 默认实例 */
    public static final DateIdWorker DEFAULT = new DateIdWorker();

    /** 秒内序列限制 */
    private static final long MAX_SEQUENCE = 99;
    /** 秒内序列限制 */
    private static final int MAX_SEQUENCE_SIZE = Long.toString(MAX_SEQUENCE).length();
    /** "0" 字符 */
    private static final char ZERO_CHAR = '0';

    /** 序列ID后缀(为不同服务器设置不同后缀，可以防止集群环境序列ID的冲突) */
    private final Supplier<String> suffixer;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    /** 日期时间字符串 */
    private String datetimeString = "00000000000000";

    /** 秒内序列 */
    private long sequence = 0L;

    /**
     * 构造函数
     */
    public DateIdWorker() {
        this(false);
    }

    /**
     * 构造函数
     * @param suffix 是否追加后缀
     */
    public DateIdWorker(boolean suffix) {
        this.suffixer = suffix ? DEFAULT_SUFFIXER : null;
    }

    /**
     * 构造函数
     * @param suffix 序列ID后缀
     */
    public DateIdWorker(String suffix) {
        this.suffixer = () -> suffix;
    }

    /**
     * 构造函数
     * @param suffixer 序列ID后缀构建器
     */
    public DateIdWorker(Supplier<String> suffixer) {
        this.suffixer = suffixer;
    }

    /**
     * 获得下一个序列ID
     * @return 序列ID
     */
    public synchronized String nextId() {

        long timestamp = timeGen();

        // 当前时间戳小于上一次ID生成的时间戳，可能是闰秒
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            // 时间偏差大小小于5ms，则等待两倍时间
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Clock moved backwards. Waiting is interrupted.", e);
                }
                timestamp = timeGen();
                if (timestamp < lastTimestamp) {
                    throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", offset));
                }
            } else {
                throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", offset));
            }
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence++;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }

        // 时间戳改变，毫秒内序列重置
        if (lastTimestamp != timestamp) {
            lastTimestamp = timestamp;
            sequence = 0L;
            datetimeString = formatDatetimeMillis(lastTimestamp);
        }

        // 拼装 ID字符串
        StringBuilder buffer = new StringBuilder();
        buffer.append(datetimeString);
        String appendSequence = Long.toString(sequence);
        for (int i = MAX_SEQUENCE_SIZE - appendSequence.length(); i > 0; i--) {
            buffer.append(ZERO_CHAR);
        }
        buffer.append(appendSequence);
        if (suffixer != null) {
            buffer.append(suffixer.get());
        }
        return buffer.toString();
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 格式化时间为日期时间字符串
     * @param millis 毫秒
     * @return 日期时间字符串
     */
    private String formatDatetimeMillis(long millis) {
        return DATETIME_FORMAT.get().format(millis);
    }

    /**
     * 默认的后缀生成器
     * @return 后缀生成器
     */
    private static Supplier<String> defaultSuffixer() {
        // 随机数
        final Random random = ThreadLocalRandom.current();

        // 当前进程ID (Process Id)
        BigInteger pid = null;
        try {
            pid = BigInteger.valueOf(Math.abs(JvmUtil.getPid()));
        } catch (Throwable e) {
            LOGGER.error("!", e);
        }
        // 本机 MAC地址
        BigInteger mac = null;
        try {
            mac = new BigInteger(NetworkUtil.getHardwareAddress());
        } catch (Throwable e) {
            LOGGER.error("!", e);
        }

        if (pid == null) {
            pid = BigInteger.valueOf(random.nextLong());
        }
        if (mac == null) {
            mac = BigInteger.valueOf(random.nextLong());
        }

        final int radix36 = 36;
        final BigInteger z1 = new BigInteger("Z", radix36);
        final BigInteger z4 = new BigInteger("ZZZZ", radix36);

        // 3位环境后缀
        StringBuilder envModBuilder = new StringBuilder(2);
        envModBuilder.append(StringUtil.leftPad(pid.mod(z1).abs().toString(radix36), 1, '0'));
        envModBuilder.append(StringUtil.leftPad(mac.mod(z1).abs().toString(radix36), 1, '0'));
        final String envMod = envModBuilder.toString().toUpperCase();
        final int mod = z4.intValue();

        // 3位环境后缀 + 3位随机后缀
        return () -> envMod + StringUtil.leftPad(Integer.toString(Math.abs(random.nextInt() % mod), radix36), 4, '0').toUpperCase();
    }
}
