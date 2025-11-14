package com.github.relucent.base.common.thread;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.concurrent.ParallelWorker;

public class ParallelWorkerTest {

    @Test
    public void testStart() {
        ParallelWorker worker = new ParallelWorker();
        AtomicInteger value = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(0);
        Set<String> threadIds = ConcurrentHashMap.newKeySet();
        int size = 1000;
        for (int i = 0; i < size; i++) {
            worker.add(() -> {
                count.incrementAndGet();
                value.incrementAndGet();
                threadIds.add(Thread.currentThread().getName());
            });
            worker.add(() -> {
                count.incrementAndGet();
                value.decrementAndGet();
                threadIds.add(Thread.currentThread().getName());
            });
        }
        worker.run(1000);
        Assert.assertEquals(0, value.get());
        Assert.assertEquals(size << 1, count.get());
        System.out.println(threadIds.size());
    }
}
