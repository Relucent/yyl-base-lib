package com.github.relucent.base.common.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final boolean daemon;
    private final UncaughtExceptionHandler handler;

    public NamedThreadFactory(String namePrefix) {
        this(namePrefix, false, null);
    }

    public NamedThreadFactory(String namePrefix, boolean daemon) {
        this(namePrefix, daemon, null);
    }

    public NamedThreadFactory(String namePrefix, boolean daemon, UncaughtExceptionHandler handler) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.handler = handler;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
        t.setDaemon(daemon);
        if (handler != null) {
            t.setUncaughtExceptionHandler(handler);
        }
        return t;
    }
}