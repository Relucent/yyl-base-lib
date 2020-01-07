package com.github.relucent.base.common.lock;

/**
 * 分布式锁
 */
public interface ConcurrentLock {

    /** 获取锁 */
    void lock();

    /** 释放锁 */
    void unlock();
}
