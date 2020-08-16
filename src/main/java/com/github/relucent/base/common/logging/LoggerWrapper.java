package com.github.relucent.base.common.logging;

public class LoggerWrapper implements Logger {

    private final Logger logger;

    public LoggerWrapper(Logger logger) {
        this.logger = logger;
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return logger.isFatalEnabled();
    }

    public void trace(String message) {
        logger.trace(message);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void fatal(String message) {
        logger.fatal(message);
    }

    public void trace(String message, Throwable throwable) {
        logger.trace(message, throwable);
    }

    public void debug(String message, Throwable throwable) {
        logger.debug(message, throwable);
    }

    public void info(String message, Throwable throwable) {
        logger.info(message, throwable);
    }

    public void warn(String message, Throwable throwable) {
        logger.warn(message, throwable);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public void fatal(String message, Throwable throwable) {
        logger.fatal(message, throwable);
    }

    public void trace(String format, Object... args) {
        logger.trace(format, args);
    }

    public void debug(String format, Object... args) {
        logger.debug(format, args);
    }

    public void info(String format, Object... args) {
        logger.info(format, args);
    }

    public void warn(String format, Object... args) {
        logger.warn(format, args);
    }

    public void error(String format, Object... args) {
        logger.error(format, args);
    }

    public void fatal(String format, Object... args) {
        logger.fatal(format, args);
    }
}
