package com.github.relucent.base.common.identifier;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.github.relucent.base.common.jvm.JvmUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.net.NetworkUtil;

/**
 * 日期时间序列ID生成器<br>
 * 格式为 日期(年月日时分秒)+秒内计数(5位)<br>
 */
public class TimeIdWorker {

    /** 默认实例 */
    public static final TimeIdWorker DEFAULT = new TimeIdWorker();

    /** 日期格式化 */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** 秒内序列限制 */
    private final long MAX_SEQUENCE = 99999;
    /** 秒内序列限制 */
    private final int MAX_SEQUENCE_SIZE = Long.toString(MAX_SEQUENCE).length();
    /** "0" 字符 */
    private final char ZERO_CHAR = '0';

    /** 序列ID前缀(为不同业务设置不同前缀，可以防止集群环境序列ID的冲突) */
    private final String prefix;

    /** 序列ID后缀(为不同服务器设置不同后缀，可以防止集群环境序列ID的冲突) */
    private final String suffix;

    /** 上次生成ID的时间截(秒) */
    private long lastSeconds = -1L;

    /** 日期时间字符串 */
    private String datetimeString = "00000000000000";

    /** 秒内序列 */
    private long sequence = 0L;

    /**
     * 构造函数
     */
    public TimeIdWorker() {
        this.prefix = "";
        this.suffix = "";
    }

    /**
     * 构造函数
     * @param prefix 序列ID前缀
     * @param suffix 序列ID后缀
     */
    public TimeIdWorker(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * 获得下一个序列ID
     * @return 序列ID
     */
    public synchronized String nextId() {

        long seconds = secondsGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (seconds < lastSeconds) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d seconds", lastSeconds - seconds));
        }

        // 如果是同一秒生成的，则递增秒内序列
        if (lastSeconds == seconds) {
            sequence++;
            // 秒内序列溢出，需要等待下一秒
            if (sequence > MAX_SEQUENCE) {
                seconds = tilNextSeconds(lastSeconds);
            }
        }

        // 时间戳改变，秒内序列重置
        if (lastSeconds != seconds) {
            lastSeconds = seconds;
            sequence = 0L;
            datetimeString = formatDatetimeOfSeconds(lastSeconds);
        }

        // 拼装 ID字符串
        StringBuffer buffer = new StringBuffer();
        buffer.append(prefix);
        buffer.append(datetimeString);
        String appendSequence = Long.toString(sequence);
        for (int i = MAX_SEQUENCE_SIZE - appendSequence.length(); i > 0; i--) {
            buffer.append(ZERO_CHAR);
        }
        buffer.append(appendSequence);
        buffer.append(suffix);
        return buffer.toString();
    }

    /**
     * 阻塞到下一个秒，直到获得新的时间戳(秒)
     * @param lastSecond 上次生成ID的时间戳(秒)
     * @return 当前时间戳(秒)
     */
    private long tilNextSeconds(long lastSeconds) {
        long seconds = secondsGen();
        while (seconds <= lastSeconds) {
            seconds = secondsGen();
        }
        return seconds;
    }

    /**
     * 返回以秒为单位的当前时间
     * @return 当前时间(秒)
     */
    private long secondsGen() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * 格式化时间秒为日期时间字符串
     * @param seconds 时间秒
     * @return 日期时间字符串
     */
    private String formatDatetimeOfSeconds(long seconds) {
        Instant instant = Instant.ofEpochMilli(seconds * 1000L);
        ZoneId zone = ZoneId.systemDefault();
        return DATETIME_FORMATTER.format(LocalDateTime.ofInstant(instant, zone));
    }

    /**
     * 通过环境获取ID后缀（通过IP地址与进程号获取）
     * @return ID后缀
     */
    public static String getIdSuffixFromEnvironment() {
        String macMod = new BigInteger(NetworkUtil.getHardwareAddress()).mod(BigInteger.valueOf(36L * 36L * 36L)).toString(36);
        String pidMod = BigInteger.valueOf(JvmUtil.getPid()).mod(BigInteger.valueOf(36L * 36L)).toString(36);
        return ("_" + StringUtil.leftPad(macMod, 3, '0') + StringUtil.leftPad(pidMod, 3, '0')).toUpperCase();
    }
}
