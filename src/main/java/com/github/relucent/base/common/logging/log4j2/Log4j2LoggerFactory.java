package com.github.relucent.base.common.logging.log4j2;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerFactory;

/**
 * Log4j2LoggerFactory
 */
public class Log4j2LoggerFactory implements LoggerFactory {

    @Override
    public Logger getLogger(Class<?> clazz) {
        return new Log4j2Logger(clazz);
    }

    @Override
    public Logger getLogger(String name) {
        return new Log4j2Logger(name);
    }
}
