package com.github.relucent.base.common.logging.stdout;

import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerFactory;

public class StdoutLoggerFactory implements LoggerFactory {

	@Override
	public Logger getLogger(Class<?> clazz) {
		return new StdoutLogger();
	}

	@Override
	public Logger getLogger(String name) {
		return new StdoutLogger();
	}
}
