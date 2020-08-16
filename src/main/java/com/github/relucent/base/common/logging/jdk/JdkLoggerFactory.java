package com.github.relucent.base.common.logging.jdk;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerFactory;

/**
 * JdkLoggerFactory
 */
public class JdkLoggerFactory implements LoggerFactory {

    public Logger getLogger(Class<?> clazz) {
        return new JdkLogger(clazz);
    }

    public Logger getLogger(String name) {
        return new JdkLogger(name);
    }
}
