package com.github.relucent.base.common.logging;

/**
 * 日志工具接口类，支持6个级别日志<br>
 * 1. TRACE<br>
 * 2. DEBUG<br>
 * 3. INFO<br>
 * 4. WARN<br>
 * 5. ERROR<br>
 * 6. FATAL<br>
 */
public interface Logger {

    static Logger getLogger(String name) {
        return LoggerManager.getLogger(name);
    }

    static Logger getLogger(Class<?> clazz) {
        return LoggerManager.getLogger(clazz);
    }

    boolean isTraceEnabled();

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();

    boolean isFatalEnabled();

    void trace(String message);

    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    void fatal(String message);

    void trace(String message, Throwable throwable);

    void debug(String message, Throwable throwable);

    void info(String message, Throwable throwable);

    void warn(String message, Throwable throwable);

    void error(String message, Throwable throwable);

    void fatal(String message, Throwable throwable);

    void trace(String format, Object... args);

    void debug(String format, Object... args);

    void info(String format, Object... args);

    void warn(String format, Object... args);

    void error(String format, Object... args);

    void fatal(String format, Object... args);
}
