package com.github.relucent.base.common.logging.slf4j;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerFactory;

public class Slf4jLoggerFactory implements LoggerFactory {

	@Override
	public Logger getLogger(Class<?> clazz) {
		return new Slf4jLogger(clazz);
	}

	@Override
	public Logger getLogger(String name) {
		return new Slf4jLogger(name);
	}

}
