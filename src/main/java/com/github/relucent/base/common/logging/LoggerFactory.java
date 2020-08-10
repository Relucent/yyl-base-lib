package com.github.relucent.base.common.logging;

/**
 * 日志工具工厂类<br>
 */
public interface LoggerFactory {

	Logger getLogger(Class<?> clazz);

	Logger getLogger(String name);
}
