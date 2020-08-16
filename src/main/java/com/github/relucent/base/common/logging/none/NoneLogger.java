package com.github.relucent.base.common.logging.none;

import com.github.relucent.base.common.logging.Logger;

/**
 * NoneLogger
 */
public class NoneLogger implements Logger {

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public boolean isFatalEnabled() {
        return false;
    }

    @Override
    public void trace(String message) {
    }

    @Override
    public void debug(String message) {
    }

    @Override
    public void info(String message) {
    }

    @Override
    public void warn(String message) {
    }

    @Override
    public void error(String message) {
    }

    @Override
    public void fatal(String message) {
    }

    @Override
    public void trace(String message, Throwable throwable) {
    }

    @Override
    public void debug(String message, Throwable throwable) {
    }

    @Override
    public void info(String message, Throwable throwable) {
    }

    @Override
    public void warn(String message, Throwable throwable) {
    }

    @Override
    public void error(String message, Throwable throwable) {
    }

    @Override
    public void fatal(String message, Throwable throwable) {
    }

    @Override
    public void trace(String format, Object... args) {
    }

    @Override
    public void debug(String format, Object... args) {
    }

    @Override
    public void info(String format, Object... args) {
    }

    @Override
    public void warn(String format, Object... args) {
    }

    @Override
    public void error(String format, Object... args) {
    }

    @Override
    public void fatal(String format, Object... args) {
    }
}
