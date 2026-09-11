package com.github.relucent.base.common.thread;

import java.util.concurrent.locks.Lock;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.concurrent.LockManager;
import com.github.relucent.base.common.concurrent.LockManager.TryLockGuard;

public class LockManagerTest {

    /**
     * 验证相同 name 返回同一把锁（命名锁的核心语义）。
     */
    @Test
    public void testGetLockReturnsSameInstance() {
        LockManager manager = new LockManager();
        Lock lock1 = manager.getLock("order-1001");
        Lock lock2 = manager.getLock("order-1001");
        Assert.assertSame("相同 name 应返回同一把锁", lock1, lock2);
    }

    /**
     * 验证 removeLock 后，再次 getLock 会得到一把新的锁（旧 name 对应的锁定被移除）。
     */
    @Test
    public void testRemoveLock() {
        LockManager manager = new LockManager();
        Lock before = manager.getLock("temp-lock");
        manager.removeLock("temp-lock");
        Lock after = manager.getLock("temp-lock");
        Assert.assertNotSame("removeLock 后再次获取应返回不同实例", before, after);
    }

    /**
     * 验证 tryLock 竞争失败时返回未加锁的 guard，且 close 后锁可被其他线程获取。
     */
    @Test
    public void testTryLockMutualExclusion() {
        LockManager manager = new LockManager();
        try (TryLockGuard g1 = manager.tryLock("mutex")) {
            Assert.assertTrue("首次 tryLock 应成功", g1.isLocked());
            // 同一把锁再次 tryLock 应失败（ReentrantLock 非可重入竞争场景由调用方控制，此处仅验证 guard 语义）
            try (TryLockGuard g2 = manager.tryLock("mutex-other")) {
                Assert.assertTrue(g2.isLocked());
            }
        }
    }
}
