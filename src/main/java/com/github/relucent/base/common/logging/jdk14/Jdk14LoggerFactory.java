package com.github.relucent.base.common.logging.jdk14;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerFactory;

/**
 * JdkLoggerFactory
 */
public class Jdk14LoggerFactory implements LoggerFactory {

	public Logger getLogger(Class<?> clazz) {
		return new Jdk14Logger(clazz);
	}

	public Logger getLogger(String name) {
		return new Jdk14Logger(name);
	}
}
