package com.github.relucent.base.plugin.jackson;

import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class JacksonUtil {

	// ===================================Fields==============================================
	private static final AtomicReference<JacksonHandler> HANDLER = new AtomicReference<>(JacksonHandler.DEFAULT);

	// ===================================Constructors========================================
	private JacksonUtil() {
	}

	// ===================================Methods=============================================
	public static void setHandler(JacksonHandler jacksonHandler) {
		HANDLER.set(jacksonHandler);
	}

	public static JacksonHandler getHandler() {
		return HANDLER.get();
	}

	public static String encode(Object value) {
		return getHandler().encode(value);
	}

	public static String encodePretty(Object value) {
		return getHandler().encodePretty(value);
	}

	public static String encodeIgnoreNull(Object value) {
		return getHandler().encodeIgnoreNull(value);
	}

	public static JsonNode decode(String json) {
		return getHandler().decode(json);
	}

	public static <T> T decode(String json, Class<T> type) {
		return getHandler().decode(json, type);
	}

	public static <T> T decode(String json, TypeReference<T> type) {
		return getHandler().decode(json, type);
	}

	public static <T> T decode(String json, com.github.relucent.base.common.reflect.TypeReference<T> type) {
		return getHandler().decode(json, type);
	}

	public static <T> T convertValue(Object node, Class<T> type) {
		return getHandler().convertValue(node, type);
	}

	public static <T> T convertValue(Object node, TypeReference<T> type) {
		return getHandler().convertValue(node, type);
	}

	public static <T> T convertValue(Object node, com.github.relucent.base.common.reflect.TypeReference<T> type) {
		return getHandler().convertValue(node, type);
	}
}
