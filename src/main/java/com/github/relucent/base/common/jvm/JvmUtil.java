package com.github.relucent.base.common.jvm;

import java.lang.management.ManagementFactory;

/**
 * 运行时信息工作类
 */
public class JvmUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected JvmUtil() {
    }

    /**
     * 获得当前进程ID (Process Id)，如果获取失败返回 -1
     * @return 当前进程的ID
     */
    public static int getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        try {
            if (name != null) {
                return Integer.parseInt(name.split("@")[0]);
            }
        } catch (Throwable e) {
            // Ignore
        }
        return -1;
    }
}
