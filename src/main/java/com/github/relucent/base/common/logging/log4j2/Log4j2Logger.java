package com.github.relucent.base.common.logging.log4j2;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.spi.AbstractLogger;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerWrapper;

/**
 * Log4j2Logger
 */
public class Log4j2Logger extends LoggerWrapper {

    private static final String FQCN = Log4j2Logger.class.getName();
    private static final Marker MARKER = MarkerManager.getMarker("#y1");

    public Log4j2Logger(Class<?> clazz) {
        super(wrap(org.apache.logging.log4j.LogManager.getLogger(clazz)));
    }

    public Log4j2Logger(String name) {
        super(wrap(org.apache.logging.log4j.LogManager.getLogger(name)));
    }

    private static Logger wrap(org.apache.logging.log4j.Logger logger) {
        return logger instanceof AbstractLogger ? new Log4j2AbstractLoggerImpl((AbstractLogger) logger, FQCN, MARKER)
                : new Log4j2LoggerImpl(logger, MARKER);
    }
}
