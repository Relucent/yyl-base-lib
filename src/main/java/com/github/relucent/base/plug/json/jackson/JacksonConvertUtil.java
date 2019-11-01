package com.github.relucent.base.plug.json.jackson;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.github.relucent.base.util.collection.Listx;
import com.github.relucent.base.util.collection.Mapx;

/**
 * 类型转换工具类
 */
class JacksonConvertUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonConvertUtil.class);
    public final static JsonDeserializer<Mapx> MAP_DESERIALIZER;
    public final static JsonDeserializer<Listx> LIST_DESERIALIZER;

    static {
        MAP_DESERIALIZER = new JsonDeserializer<Mapx>() {
            @Override
            public Mapx deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
                try {
                    return toMap(parser.readValueAsTree());
                } catch (Exception e) {
                    return null;
                }
            }
        };
        LIST_DESERIALIZER = new JsonDeserializer<Listx>() {
            @Override
            public Listx deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
                try {
                    return toList(parser.readValueAsTree());
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    /**
     * 将{@link TreeNode}转换为{@link Mapx}类型
     * @param node {@link TreeNode} 对象
     * @return {@link TreeNode}对象,如果类型不匹配或者转换出现异常则返回null.
     */
    public static Mapx toMap(TreeNode node) {
        try {
            if (node instanceof ObjectNode) {
                return toMap((ObjectNode) node);
            }
        } catch (Exception e) {
            LOGGER.warn(node.getClass() + "cannot be cast to Mapx", e);
        }
        return null;
    }

    /**
     * 将 {@link TreeNode} 转换为{@link Listx}类型
     * @param node {@link TreeNode} 对象
     * @return {@link Listx} 对象,如果类型不匹配或者转换出现异常则返回null.
     */
    public static Listx toList(TreeNode node) {
        try {
            if (node instanceof ArrayNode) {
                return toList((ArrayNode) node);
            }
        } catch (Exception e) {
            LOGGER.warn(node.getClass() + "cannot be cast to Listx", e);
        }
        return null;
    }

    /**
     * 将 {@link ObjectNode} 转换为{@link Mapx}类型
     * @param node {@link ObjectNode} 对象
     * @return {@link Mapx} 对象,如果类型不匹配或者转换出现异常则返回null.
     */
    private static Mapx toMap(ObjectNode node) {
        Mapx map = new Mapx();
        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> entry = it.next();
            String key = entry.getKey();
            Object value = toBasicValue(entry.getValue());
            map.put(key, value);
        }
        return map;
    }

    /**
     * 将 {@link ArrayNode} 转换为{@link Listx}类型
     * @param node {@link ArrayNode} 对象
     * @return {@link Listx} 对象,如果类型不匹配或者转换出现异常则返回null.
     */
    private static Listx toList(ArrayNode node) {
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
    private static Object toBasicValue(JsonNode node) {
        if (node == null || node instanceof NullNode || node instanceof MissingNode) {// NULL|MISSING
            return null;
        }
        if (node instanceof ArrayNode) {// ARRAY
            return toList((ArrayNode) node);
        }
        if (node instanceof ObjectNode) {// OBJECT
            return toMap((ObjectNode) node);
        }
        if (node.isLong()) {
            return node.asLong();
        }
        if (node.isInt()) {
            return node.asInt();
        }
        if (node.isDouble()) {
            return node.asDouble();
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node instanceof ValueNode) {// BINARY|BOOLEAN|NUMBER|POJO|STRING
            return node.asText();
        }
        return null;
    }

}
