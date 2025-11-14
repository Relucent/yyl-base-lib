package com.github.relucent.base.common.concurrent;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.relucent.base.common.exception.ExceptionUtil;

/**
 * LockHelper 提供基于字符串 name 的锁管理与 TryLockGuard 工具方法。<br>
 * 1. 使用 ConcurrentHashMap 管理不同 name 的锁，实现全局可重用的命名锁。<br>
 * 2. getLock(name) 可以获取相同 name 的同一把锁。<br>
 * 3. tryLock(name) 返回 TryLockGuard，用于安全执行 tryLock 并自动释放。<br>
 * 
 * <pre>
 * 使用示例：
 * try (TryLockGuard guard = LockHelper.tryLock("task-1")) {
 *     if (!guard.isLocked()) {
 *         // 没抢到锁，直接返回
 *         return;
 *     }
 *     // 执行业务
 *     doTask();
 * }
 * </pre>
 */
public class LockManager {

    // ==============================Fields===========================================
    /** 全局锁容器，线程安全 */
    private final ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();

    /** 当前锁生成器 */
    private final LockProvider lockProvider;

    // ==============================Constructors=====================================
    /**
     * 构造函数使用默认锁生成器
     */
    public LockManager() {
        this(name -> new ReentrantLock());
    }

    /**
     * 构造函数使用
     * @param lockProvider 锁生成器
     */
    public LockManager(LockProvider lockProvider) {
        this.lockProvider = lockProvider;
    }

    // ==============================Methods==========================================
    /**
     * 根据 name 获取对应的锁。
     * @param name 锁名称
     * @return 同一 name 始终返回同一把锁。
     */
    public Lock getLock(String name) {
        return locks.computeIfAbsent(name, lockProvider::getLock);
    }

    /**
     * 阻塞锁，保证加锁成功
     * @param name 锁名称
     * @return LockGuard
     */
    public LockGuard lock(String name) {
        return new LockGuard(getLock(name));
    }

    /**
     * 尝试获取锁，并返回 TryLockGuard，用于自动管理锁释放。<br>
     * 
     * <pre>
     * 用法：
     * try (TryLockGuard guard = LockHelper.tryLock("sync-job")) {
     *     if(!guard.isLocked()) {
     *         return;
     *     }
     *     // 业务逻辑 
     * }
     * </pre>
     * 
     * @param name 锁名称
     * @return TryLockGuard（作用域结束自动 unlock）
     */
    public TryLockGuard tryLock(String name) {
        return new TryLockGuard(getLock(name));
    }

    /**
     * 尝试获取锁，并返回 TryLockGuard，用于自动管理锁释放。<br>
     * 
     * <pre>
     * 用法：
     * try (TryLockGuard guard = LockHelper.tryLock("sync-job", 3, TimeUnit.SECONDS)) {
     *     if(!guard.isLocked()) {
     *         return;
     *     }
     *     // 业务逻辑 
     * }
     * </pre>
     * 
     * @param name    锁名称
     * @param timeout 待锁的最长时间
     * @param unit    时间单位
     * @return TryLockGuard（作用域结束自动 unlock）
     */
    public TryLockGuard tryLock(String name, long timeout, TimeUnit unit) {
        return new TryLockGuard(getLock(name), timeout, unit);
    }

    /**
     * 尝试获取锁，并返回 TryLockGuard ，用于自动管理锁释放。
     *
     * <pre>
     * 用法：
     * try (TryLockGuard guard = lockManager.tryLock("sync-job", Duration.ofSeconds(3))) {
     *     if (!guard.isLocked()) return;
     *     // 业务逻辑
     * }
     * </pre>
     *
     * @param name    锁名称
     * @param timeout 超时时间
     * @return TryLockGuard
     */
    public TryLockGuard tryLock(String name, Duration timeout) {
        return tryLock(name, timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 封装 tryLock 执行 Runnable，返回是否成功获取锁
     * @param name 锁名称
     * @param task 业务逻辑
     * @return 是否成功执行
     */
    public boolean tryLockExecute(String name, Runnable task) {
        try (TryLockGuard guard = tryLock(name)) {
            if (!guard.isLocked()) {
                return false;
            }
            task.run();
            return true;
        }
    }

    /**
     * 封装带超时 tryLock 执行 Runnable，返回是否成功获取锁
     * @param name    锁名称
     * @param timeout 超时时间
     * @param task    业务逻辑
     * @return 是否成功执行
     */
    public boolean tryLockExecute(String name, Duration timeout, Runnable task) {
        try (TryLockGuard guard = tryLock(name, timeout)) {
            if (!guard.isLocked()) {
                return false;
            }
            task.run();
            return true;
        }
    }

    /**
     * 封装 tryLock 执行 Callable，失败返回默认值
     * @param name         锁名称
     * @param task         业务逻辑
     * @param defaultValue 加锁失败返回的默认值
     * @param <T>          返回类型
     * @return 业务逻辑结果或默认值
     */
    public <T> T tryLockExecute(String name, Callable<T> task, T defaultValue) {
        try (TryLockGuard guard = tryLock(name)) {
            if (!guard.isLocked()) {
                return defaultValue;
            }
            return task.call();
        } catch (Exception e) {
            throw ExceptionUtil.propagate(e);
        }
    }

    /**
     * 封装带超时 tryLock 执行 Callable，失败返回默认值
     * @param name         锁名称
     * @param timeout      超时时间
     * @param task         业务逻辑
     * @param defaultValue 加锁失败返回的默认值
     * @param <T>          返回类型
     * @return 业务逻辑结果或默认值
     */
    public <T> T tryLockExecute(String name, Duration timeout, Callable<T> task, T defaultValue) {
        try (TryLockGuard guard = tryLock(name, timeout)) {
            if (!guard.isLocked()) {
                return defaultValue;
            }
            return task.call();
        } catch (Exception e) {
            throw ExceptionUtil.propagate(e);
        }
    }

    // ==============================InnerClass=======================================
    /**
     * LockProvider 负责根据 name 创建或返回一个可用的 Lock 实例。<br>
     * 可替换为不同的分布式锁实现，例如：<br>
     * - ReentrantLock<br>
     * - RedisLock<br>
     * - ZookeeperLock<br>
     * - RedissonLock<br>
     */
    public static interface LockProvider {

        /**
         * 获取指定名称的锁对象。
         * @param name 锁名称
         * @return Lock实例（可能是本地锁、分布式锁、代理锁）
         */
        Lock getLock(String name);
    }

    /**
     * LockGuard 阻塞锁，保证一定加锁成功。<br>
     * 支持 try-with-resources，自动释放锁。
     */
    public static class LockGuard implements AutoCloseable {

        /** 实际的锁对象 */
        private final Lock lock;

        /**
         * 阻塞 lock
         * @param 锁
         */
        public LockGuard(Lock lock) {
            this.lock = lock;
            this.lock.lock(); // 阻塞获取锁
        }

        /**
         * 作用域结束时自动释放锁。
         */
        @Override
        public void close() {
            lock.unlock();
        }
    }

    /**
     * TryLockGuard 尝试加锁，可能失败。 支持非阻塞 tryLock 和带超时 tryLock。 自动释放锁。
     */
    public static class TryLockGuard implements AutoCloseable {

        /** 实际的锁对象 */
        private final Lock lock;
        /** 标记是否成功获取了锁 */
        private final boolean locked;

        /**
         * 非阻塞 tryLock
         * @param 锁
         */
        public TryLockGuard(Lock lock) {
            this.lock = lock;
            this.locked = lock.tryLock();
        }

        /**
         * 带超时 tryLock
         * @param name    锁名称
         * @param timeout 待锁的最长时间
         * @param unit    时间单位
         */

        public TryLockGuard(Lock lock, long timeout, TimeUnit unit) {
            this.lock = lock;
            boolean success = false;
            try {
                success = lock.tryLock(timeout, unit);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.locked = success;
        }

        /**
         * @return 是否成功加锁
         */
        public boolean isLocked() {
            return locked;
        }

        /**
         * 作用域结束时自动释放锁。
         */
        @Override
        public void close() {
            if (locked) {
                lock.unlock();
            }
        }
    }
}