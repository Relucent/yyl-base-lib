package com.github.relucent.base.common.logging.slf4j;

import org.slf4j.spi.LocationAwareLogger;

import com.github.relucent.base.common.logging.AbstractLogger;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.logging.LoggerWrapper;

/**
 * Slf4jLogger
 */
public class Slf4jLogger extends LoggerWrapper {

	private static final String FQCN = Slf4jLogger.class.getName();

	public Slf4jLogger(Class<?> clazz) {
		super(wrap(org.slf4j.LoggerFactory.getLogger(clazz)));
	}

	public Slf4jLogger(String name) {
		super(wrap(org.slf4j.LoggerFactory.getLogger(name)));
	}

	private static Logger wrap(org.slf4j.Logger logger) {
		return logger instanceof AbstractLogger ? new Slf4jLocationAwareLoggerImpl((LocationAwareLogger) logger, FQCN) : new Slf4jLoggerImpl(logger);
	}
}