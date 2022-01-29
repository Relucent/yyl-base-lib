package com.github.relucent.base.plugin.jedis;

import java.util.concurrent.CountDownLatch;

import com.github.relucent.base.common.lock.DistributedLock;

public class JedisDistributedLockExample {

    public static void main(String[] args) throws InterruptedException {

        try (JedisDS ds = JedisDS.builder().setHost("127.0.0.1").setPort(6379).build()) {

            System.out.println("Start");
            ds.ping();

            DistributedLock lock = ds.getLock("test_lock");

            int count = 5;
            CountDownLatch latch = new CountDownLatch(count);

            for (int i = 0; i < count; i++) {
                new Thread(() -> {
                    try {
                        String threadName = Thread.currentThread().getName();
                        System.out.println(threadName + ":lock()");
                        lock.lock();
                        System.out.println(threadName + ":lock acquired");
                        try {
                            Thread.sleep(1000L);
                        } finally {
                            System.out.println(threadName + ":unlock()");
                            lock.unlock();
                            System.out.println(threadName + ":unlock success");
                            latch.countDown();
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                }).start();
            }

            latch.await();
            System.out.println("Complete");
        }
    }
}
