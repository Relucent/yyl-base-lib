package com.github.relucent.base.common.lock;

/**
 * 分布式锁工厂类
 */
public interface ConcurrentLockFactory {
    /**
     * 获得一个分布式锁
     * @param id 锁Id
     * @return 分布式锁
     */
    ConcurrentLock getLock(String id);
}
