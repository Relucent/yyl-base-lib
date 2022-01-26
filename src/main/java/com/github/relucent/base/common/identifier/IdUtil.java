package com.github.relucent.base.common.identifier;

public class IdUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected IdUtil() {
    }

    /**
     * 生成默认的雪花ID
     * @return 雪花ID
     */
    public static Long snowflakeId() {
        return SnowflakeIdWorker.DEFAULT.nextId();
    }

    /**
     * 获得20位时序ID
     * @return 时序ID
     */
    public static String timeId() {
        return TimeId.nextId();
    }

    /**
     * 生成21位 NanoId
     * @return NanoId
     */
    public static String nanoId() {
        return NanoId.randomNanoId();
    }

    /**
     * 生成32位UUID
     * @return UUID
     */
    public static String uuid32() {
        return UUID32.randomUUID().toString();
    }
}
