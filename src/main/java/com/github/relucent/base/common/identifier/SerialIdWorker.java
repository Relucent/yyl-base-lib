package com.github.relucent.base.common.identifier;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 序列ID生成器<br>
 * 格式为 日期(年月日时分秒)+毫秒+毫秒内计数 <br>
 */
public class SerialIdWorker {

    /** 默认实例 */
    public static final SerialIdWorker DEFAULT = new SerialIdWorker();

    /** 日期格式化类线程变量 */
    private static final ThreadLocal<DateFormat> DATEFORMAT_HOLDER = new ThreadLocal<DateFormat>() {
        private static final String DATETIME_FORMAT = "yyyyMMddHHmmssSSS";

        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_FORMAT);
        };
    };

    /** 毫秒内序列限制 */
    private final long MAX_SEQUENCE = 999;
    /** 毫秒内序列限制 */
    private final int MAX_SEQUENCE_SIZE = Long.toString(MAX_SEQUENCE).length();
    /** "0" 字符 */
    private final char ZERO_CHAR = '0';

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;
    /** 毫秒内序列 */
    private long sequence = 0L;
    /** 序列ID后缀(为不同服务器设置不同后缀，可以防止集群环境序列ID的冲突) */
    private volatile String suffix = "";

    /**
     * 构造函数
     */
    public SerialIdWorker() {
        this("");
    }

    /**
     * 构造函数
     * @param suffix ID后缀
     */
    public SerialIdWorker(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 获得下一个序列ID
     * @return 序列ID
     */
    public synchronized String nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence++;
            if (sequence > MAX_SEQUENCE) {
                sequence = 0L;
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 拼装 ID字符串
        StringBuffer buffer = new StringBuffer();
        DATEFORMAT_HOLDER.get().format(new Date(timestamp), buffer, new FieldPosition(0));
        String appendSequence = Long.toString(sequence);
        for (int i = MAX_SEQUENCE_SIZE - appendSequence.length(); i > 0; i--) {
            buffer.append(ZERO_CHAR);
        }
        buffer.append(appendSequence);
        buffer.append(suffix);
        return buffer.toString();
    }

    /**
     * 设置序列ID的后缀，为不同服务器的应用使用不同后缀，可以防止集群环境序列ID的冲突
     * @param suffix 序列ID后缀
     */
    public synchronized void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
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
    private long timeGen() {
        return System.currentTimeMillis();
    }
}
