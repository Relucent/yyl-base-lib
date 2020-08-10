package com.github.relucent.base.plugin.gson;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.logging.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * 类型转换工具类
 */
class GsonConvertUtil {

	private static final Logger LOGGER = Logger.getLogger(GsonConvertUtil.class);

	/**
	 * 将 {@link JsonElement} 转换为{@link Mapx}类型
	 * @param node {@link JsonElement} 对象
	 * @return {@link Mapx} 对象,如果类型不匹配或者转换出现异常则返回null.
	 */
	public static Mapx toMap(JsonElement node) {
		try {
			if (node instanceof JsonObject) {
				return toMap((JsonObject) node);
			}
		} catch (Exception e) {
			LOGGER.warn(node.getClass() + "cannot be cast to Mapx", e);
		}
		return null;
	}

	/**
	 * 将 {@link JsonElement} 转换为{@link Listx}类型
	 * @param node {@link JsonElement} 对象
	 * @return {@link Listx} 对象,如果类型不匹配或者转换出现异常则返回null.
	 */
	public static Listx toList(JsonElement node) {
		try {
			if (node instanceof JsonArray) {
				return toList((JsonArray) node);
			}
		} catch (Exception e) {
			LOGGER.warn(node.getClass() + "cannot be cast to Listx", e);
		}
		return null;
	}

	/**
	 * 将 {@link JsonObject} 转换为{@link Mapx}类型
	 * @param node {@link JsonObject} 对象
	 * @return {@link Mapx} 对象,如果类型不匹配或者转换出现异常则返回null.
	 */
	private static Mapx toMap(JsonObject node) {
		Mapx map = new Mapx();
		for (Map.Entry<String, JsonElement> entry : node.entrySet()) {
			String key = entry.getKey();
			Object value = toBasicValue(entry.getValue());
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 将 {@link JsonArray} 转换为{@link Listx}类型
	 * @param node {@link JsonArray} 对象
	 * @return {@link Listx} 对象,如果类型不匹配或者转换出现异常则返回null.
	 */
	private static Listx toList(JsonArray node) {
		Listx list = new Listx();
		for (int i = 0, len = node.size(); i < len; i++) {
			list.add(toBasicValue(node.get(i)));
		}
		return list;
	}

	/**
	 * 将 {@link JsonNode} 转化为基础对象或者扩展集合对象
	 * @param node {@link JsonNode}
	 * @return 基础对象或者扩展集合对象
	 */
	private static Object toBasicValue(JsonElement node) {
		if (node == null || node.isJsonNull()) {
			return null;
		}
		if (node instanceof JsonObject) {
			return toMap((JsonObject) node);
		}
		if (node instanceof JsonArray) {
			return toList((JsonArray) node);
		}
		if (node instanceof JsonPrimitive) {
			return node.getAsString();
		}
		return null;
	}
}
