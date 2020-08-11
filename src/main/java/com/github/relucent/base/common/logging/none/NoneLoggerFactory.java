package com.github.relucent.base.common.logging.none;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerFactory;

public class NoneLoggerFactory implements LoggerFactory {

	@Override
	public Logger getLogger(Class<?> clazz) {
		return new NoneLogger();
	}

	@Override
	public Logger getLogger(String name) {
		return new NoneLogger();
	}
}
