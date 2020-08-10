package com.github.relucent.base.common.logging.commons;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerFactory;

/**
 * JdkLoggerFactory
 */
public class CommonsLoggerFactory implements LoggerFactory {

	public Logger getLogger(Class<?> clazz) {
		return new CommonsLogger(clazz);
	}

	public Logger getLogger(String name) {
		return new CommonsLogger(name);
	}
}
