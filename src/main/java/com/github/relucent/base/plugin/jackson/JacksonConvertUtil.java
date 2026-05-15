package com.github.relucent.base.plugin.jackson;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.logging.Logger;

/**
 * 类型转换工具类
 */
public class JacksonConvertUtil {

	private static final Logger LOGGER = Logger.getLogger(JacksonConvertUtil.class);

	private JacksonConvertUtil() {
	}

	/**
	 * 将 {@link ObjectNode} 转换为{@link Mapx}类型
	 * @param node {@link ObjectNode} 对象
	 * @return {@link Mapx} 对象
	 */
	public static Mapx convertObject(ObjectNode node) {
		if (node == null || node.isNull() || node.isMissingNode()) {
			return new Mapx();
		}
		if (!node.isObject()) {
			throw new IllegalArgumentException("Expected ObjectNode but got: " + node.getNodeType());
		}
		Mapx map = new Mapx();
		for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
			Map.Entry<String, JsonNode> entry = it.next();
			map.put(entry.getKey(), convertValue(entry.getValue()));
		}
		return map;
	}

	/**
	 * 将 {@link TreeNode} 转换为{@link Listx}类型
	 * @param node {@link TreeNode} 对象
	 * @return {@link Listx} 对象
	 */
	public static Listx convertArray(ArrayNode node) {
		if (node == null || node.isNull() || node.isMissingNode()) {
			return new Listx();
		}
		if (!node.isArray()) {
			throw new IllegalArgumentException("Expected ArrayNode but got: " + node.getNodeType());
		}
		Listx list = new Listx();
		for (JsonNode item : node) {
			list.add(convertValue(item));
		}
		return list;
	}

	/**
	 * 将 {@link JsonNode} 转化为基础对象或者扩展集合对象
	 * @param node {@link JsonNode}
	 * @return 基础对象或者扩展集合对象
	 */
	private static Object convertValue(JsonNode node) {

		// null
		if (node == null || node.isNull() || node.isMissingNode()) {
			return null;
		}

		// array
		if (node.isArray()) {
			return convertArray((ArrayNode) node);
		}

		// object
		if (node.isObject()) {
			return convertObject((ObjectNode) node);
		}

		// number
		if (node.isInt()) {
			return node.intValue();
		}
		if (node.isLong()) {
			return node.longValue();
		}
		if (node.isDouble()) {
			return node.doubleValue();
		}
		if (node.isFloat()) {
			return node.floatValue();
		}
		if (node.isBigInteger()) {
			return node.bigIntegerValue();
		}
		if (node.isNumber()) {
			return node.decimalValue();
		}

		// boolean
		if (node.isBoolean()) {
			return node.booleanValue();
		}

		// string
		if (node.isTextual()) {
			return node.textValue();
		}

		// binary
		if (node.isBinary()) {
			try {
				return node.binaryValue();
			} catch (Exception e) {
				LOGGER.warn("Failed to read binary value", e);
				return null;
			}
		}

		LOGGER.warn("Unsupported JsonNode type: {}, value: {}", node.getClass(), node);
		return null;
	}

}
