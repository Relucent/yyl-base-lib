package com.github.relucent.base.common.logging.log4j2;

import org.apache.logging.log4j.Marker;

import com.github.relucent.base.common.logging.AbstractLogger;

/**
 * Log4j2LoggerImpl
 */
public class Log4j2LoggerImpl extends AbstractLogger {

    private final org.apache.logging.log4j.Logger logger;
    private final Marker marker;

    protected Log4j2LoggerImpl(org.apache.logging.log4j.Logger logger, Marker marker) {
        this.logger = logger;
        this.marker = marker;
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isFatalEnabled();
    }

    @Override
    public void trace(String message) {
        logger.trace(marker, message);
    }

    @Override
    public void debug(String message) {
        logger.debug(marker, message);
    }

    @Override
    public void info(String message) {
        logger.info(marker, message);
    }

    @Override
    public void warn(String message) {
        logger.warn(marker, message);
    }

    @Override
    public void error(String message) {
        logger.error(marker, message);
    }

    @Override
    public void fatal(String message) {
        logger.fatal(marker, message);
    }

    @Override
    public void trace(String message, Throwable throwable) {
        logger.trace(marker, message, throwable);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        logger.debug(marker, message, throwable);
    }

    @Override
    public void info(String message, Throwable throwable) {
        logger.info(marker, message, throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        logger.warn(marker, message, throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(marker, message, throwable);
    }

    @Override
    public void fatal(String message, Throwable throwable) {
        logger.fatal(marker, message, throwable);
    }
}
