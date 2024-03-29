package com.github.relucent.base.common.logging;

import java.lang.reflect.Constructor;

import com.github.relucent.base.common.exception.GeneralException;
import com.github.relucent.base.common.logging.jdk.JdkLoggerFactory;
import com.github.relucent.base.common.logging.log4j2.Log4j2LoggerFactory;
import com.github.relucent.base.common.logging.none.NoneLoggerFactory;
import com.github.relucent.base.common.logging.slf4j.Slf4jLoggerFactory;
import com.github.relucent.base.common.logging.stdout.StdoutLoggerFactory;

/**
 * 日志工具管理类<br>
 */
public class LoggerManager {

    private static LoggerFactory DEFAULT_LOGGER_FACTORY = null;
    static {
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useSlf4jLogging();
            }
        });
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useLog4J2Logging();
            }
        });
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useJdkLogging();
            }
        });
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useNoneLogging();
            }
        });
    }

    public static void setDefaultLogFactory(LoggerFactory defaultLogFactory) {
        if (defaultLogFactory == null) {
            throw new IllegalArgumentException("defaultLogFactory can not be null.");
        }
        DEFAULT_LOGGER_FACTORY = defaultLogFactory;
    }

    public static LoggerFactory getDefaultLogFactory() {
        return DEFAULT_LOGGER_FACTORY;
    }

    static Logger getLogger(Class<?> clazz) {
        return DEFAULT_LOGGER_FACTORY.getLogger(clazz);
    }

    static Logger getLogger(String name) {
        return DEFAULT_LOGGER_FACTORY.getLogger(name);
    }

    public static synchronized void useSlf4jLogging() {
        setImplementation(Slf4jLoggerFactory.class);
    }

    public static synchronized void useLog4J2Logging() {
        setImplementation(Log4j2LoggerFactory.class);
    }

    public static synchronized void useJdkLogging() {
        setImplementation(JdkLoggerFactory.class);
    }

    public static synchronized void useStdoutLogging() {
        setImplementation(StdoutLoggerFactory.class);
    }

    public static synchronized void useNoneLogging() {
        setImplementation(NoneLoggerFactory.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (DEFAULT_LOGGER_FACTORY == null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                /** ignore */
            }
        }
    }

    private static void setImplementation(Class<? extends LoggerFactory> implClass) {
        try {
            Constructor<? extends LoggerFactory> candidate = implClass.getConstructor();
            LoggerFactory factory = (LoggerFactory) candidate.newInstance();
            Logger logger = factory.getLogger(LoggerFactory.class);
            if (logger.isDebugEnabled()) {
                logger.debug("Logging initialized using '" + implClass + "' adapter.");
            }
            DEFAULT_LOGGER_FACTORY = factory;
        } catch (Throwable e) {
            throw new GeneralException("Error setting Log implementation.", e);
        }
    }
}
