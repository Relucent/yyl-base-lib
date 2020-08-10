
package com.github.relucent.base.common.logging.log4j;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerFactory;

/**
 * Log4jLoggerFactory
 */
public class Log4jLoggerFactory implements LoggerFactory {

	@Override
	public Logger getLogger(Class<?> clazz) {
		return new Log4jLogger(clazz);
	}

	@Override
	public Logger getLogger(String name) {
		return new Log4jLogger(name);
	}
}
