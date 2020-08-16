package com.github.relucent.base.common.logging.jdk;

import java.util.logging.Level;

import com.github.relucent.base.common.logging.AbstractLogger;

/**
 * JdkLogger
 */
public class JdkLogger extends AbstractLogger {

    private final java.util.logging.Logger logger;
    private final String sourceClass;

    public JdkLogger(Class<?> clazz) {
        this(clazz.getName());
    }

    public JdkLogger(String name) {
        logger = java.util.logging.Logger.getLogger(sourceClass = name);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINEST);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    @Override
    public void trace(String message) {
        logger.logp(Level.FINEST, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }

    @Override
    public void debug(String message) {
        logger.logp(Level.FINE, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }

    @Override
    public void info(String message) {
        logger.logp(Level.INFO, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }

    @Override
    public void warn(String message) {
        logger.logp(Level.WARNING, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }

    @Override
    public void error(String message) {
        logger.logp(Level.SEVERE, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }

    @Override
    public void fatal(String message) {
        logger.logp(Level.SEVERE, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message);
    }

    @Override
    public void trace(String message, Throwable throwable) {
        logger.logp(Level.FINEST, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message, throwable);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        logger.logp(Level.FINE, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message, throwable);
    }

    @Override
    public void info(String message, Throwable throwable) {
        logger.logp(Level.INFO, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message, throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        logger.logp(Level.WARNING, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message, throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.logp(Level.SEVERE, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message, throwable);
    }

    @Override
    public void fatal(String message, Throwable throwable) {
        logger.logp(Level.SEVERE, sourceClass, Thread.currentThread().getStackTrace()[1].getMethodName(), message, throwable);
    }
}
