package com.github.relucent.base.common.jvm;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;

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
        String name = getRuntimeMXBean().getName();
        try {
            if (name != null) {
                return Integer.parseInt(name.split("@")[0]);
            }
        } catch (Throwable e) {
            // Ignore
        }
        return -1;
    }

    /**
     * 获得当前进程名称，格式通常为 {@code pid@host}，如果获取失败返回 {@code null}
     * @return 进程名称，例如 {@code 12345@localhost}
     */
    public static String getProcessName() {
        try {
            return getRuntimeMXBean().getName();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获得 JVM 实现名称，如果获取失败返回 {@code null}
     * @return JVM 名称，例如 {@code Java HotSpot(TM) 64-Bit Server VM}
     */
    public static String getJvmName() {
        try {
            return getRuntimeMXBean().getVmName();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获得 JVM 实现版本，如果获取失败返回 {@code null}
     * @return JVM 版本，例如 {@code 25.181-b13}
     */
    public static String getJvmVersion() {
        try {
            return getRuntimeMXBean().getVmVersion();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获得 JVM 供应商，如果获取失败返回 {@code null}
     * @return JVM 供应商，例如 {@code Oracle Corporation}
     */
    public static String getJvmVendor() {
        try {
            return getRuntimeMXBean().getVmVendor();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获得 JVM 名称与版本的组合信息，如果获取失败返回 {@code null}
     * @return JVM 信息，例如 {@code Java HotSpot(TM) 64-Bit Server VM (25.181-b13)}
     */
    public static String getJvmInfo() {
        String name = getJvmName();
        String version = getJvmVersion();
        if (name == null && version == null) {
            return null;
        }
        if (version == null) {
            return name;
        }
        if (name == null) {
            return version;
        }
        return name + " (" + version + ")";
    }

    /**
     * 获得 JVM 启动时间（自 1970-01-01 起的毫秒数），如果获取失败返回 -1
     * @return JVM 启动时间戳
     */
    public static long getJvmStartTime() {
        try {
            return getRuntimeMXBean().getStartTime();
        } catch (Throwable e) {
            return -1L;
        }
    }

    /**
     * 获得 JVM 已运行时长（毫秒），如果获取失败返回 -1
     * @return JVM 运行时长
     */
    public static long getJvmUptime() {
        try {
            return getRuntimeMXBean().getUptime();
        } catch (Throwable e) {
            return -1L;
        }
    }

    /**
     * 获得 JVM 最大可用内存（字节）
     * @return 最大内存
     */
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    /**
     * 获得 JVM 当前已申请内存（字节）
     * @return 当前总内存
     */
    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    /**
     * 获得 JVM 当前空闲内存（字节）
     * @return 空闲内存
     */
    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * 获得 JVM 当前已使用内存（字节），等于 {@link #getTotalMemory()} - {@link #getFreeMemory()}
     * @return 已使用内存
     */
    public static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * 获得 JVM 当前还可用于分配的内存（字节），约等于 {@link #getMaxMemory()} - 已使用内存
     * @return 可分配内存
     */
    public static long getUsableMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
    }

    /**
     * 获得堆内存使用情况，如果获取失败返回 {@code null}
     * @return 堆内存使用情况（包含 init/used/committed/max）
     */
    public static MemoryUsage getHeapMemoryUsage() {
        try {
            return getMemoryMXBean().getHeapMemoryUsage();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获得非堆内存使用情况，如果获取失败返回 {@code null}
     * @return 非堆内存使用情况（包含 init/used/committed/max）
     */
    public static MemoryUsage getNonHeapMemoryUsage() {
        try {
            return getMemoryMXBean().getNonHeapMemoryUsage();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 获得当前已加载的类数量，如果获取失败返回 -1
     * @return 已加载类数量
     */
    public static int getLoadedClassCount() {
        try {
            return getClassLoadingMXBean().getLoadedClassCount();
        } catch (Throwable e) {
            return -1;
        }
    }

    /**
     * 获得自 JVM 启动以来累计加载的类数量，如果获取失败返回 -1
     * @return 累计加载类数量
     */
    public static long getTotalLoadedClassCount() {
        try {
            return getClassLoadingMXBean().getTotalLoadedClassCount();
        } catch (Throwable e) {
            return -1L;
        }
    }

    /**
     * 获得自 JVM 启动以来累计卸载的类数量，如果获取失败返回 -1
     * @return 累计卸载类数量
     */
    public static long getUnloadedClassCount() {
        try {
            return getClassLoadingMXBean().getUnloadedClassCount();
        } catch (Throwable e) {
            return -1L;
        }
    }

    /**
     * 获得运行时管理 Bean
     * @return 运行时管理 Bean
     */
    private static RuntimeMXBean getRuntimeMXBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

    /**
     * 获得内存管理 Bean
     * @return 内存管理 Bean
     */
    private static MemoryMXBean getMemoryMXBean() {
        return ManagementFactory.getMemoryMXBean();
    }

    /**
     * 获得类加载管理 Bean
     * @return 类加载管理 Bean
     */
    private static ClassLoadingMXBean getClassLoadingMXBean() {
        return ManagementFactory.getClassLoadingMXBean();
    }
}
