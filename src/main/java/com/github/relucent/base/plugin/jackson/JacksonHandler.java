package com.github.relucent.base.plugin.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.json.JsonHandler;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.reflect.TypeReference;

/**
 * JSON处理器 （基于JACKSON实现）
 */
public class JacksonHandler implements JsonHandler {

    // ===================================Fields==============================================
    public static final JacksonHandler INSTANCE = new JacksonHandler();
    private final Logger logger = Logger.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private final ObjectWriter prettyWriter;

    // ===================================Constructors========================================
    /**
     * 构造函数(使用指定 {@link ObjectMapper})
     * @param objectMapper 对象映射
     */
    public JacksonHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    /**
     * 构造函数(默认)
     */
    public JacksonHandler() {
        this(MyObjectMapper.INSTANCE);
    }

    // ===================================Methods=============================================

    /**
     * 将JAVA对象编码为JSON字符串
     * @param src JAVA对象
     * @return 对象的JSON字符串
     */
    @Override
    public String encode(Object src) {
        try {
            return objectMapper.writeValueAsString(src);
        } catch (Throwable e) {
            logger.warn("#", e);
            return null;
        }
    }

    /**
     * 将JAVA对象编码为带缩进打印格式的JSON字符串对象
     * @param value JAVA对象
     * @return 对象的JSON字符串
     */
    public String encodePretty(Object value) {
        try {
            return prettyWriter.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 将JSON字符串解码为JSON对象
     * @param json 对象的JSON字符串
     * @return JSON对象，如果无法解析将返回NULL
     */
    public JsonNode decode(String json) {
        try {
            return objectMapper.readValue(json, JsonNode.class);
        } catch (Exception e) {
            logger.warn("#", e);
            return null;
        }
    }

    /**
     * 将JSON字符串解码为JAVA对象
     * @param <T>  对象泛型
     * @param json 对象的JSON字符串
     * @param type JAVA对象类型
     * @return JSON对应的JAVA对象，如果无法解析将返回NULL
     */
    @Override
    public <T> T decode(String json, Class<T> type) {
        return decode(json, type, null);
    }

    /**
     * 将JSON字符串，解码为JAVA对象
     * @param <T>          对象泛型
     * @param json         JSON字符串
     * @param type         JAVA对象类型
     * @param defaultValue 默认值
     * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
     */
    public <T> T decode(String json, Class<T> type, T defaultValue) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Throwable e) {
            logger.warn("#", e);
            return defaultValue;
        }
    }

    /**
     * 将JSON字符串，解码为JAVA对象
     * @param <T>   对象泛型
     * @param json  JSON字符串
     * @param token 类型标记
     * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
     */
    @Override
    public <T> T decode(String json, TypeReference<T> token) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType valueType = typeFactory.constructType(token.getType());
            return objectMapper.readValue(json, valueType);
        } catch (Throwable e) {
            logger.error("#", e);
            return null;
        }
    }

    /**
     * 将JSON字符串，解码为Map对象(该方法依赖于JACKSON类库)
     * @param json JSON字符串
     * @return JSON对应的Map对象，如果无法解析将返回NULL.
     */
    @Override
    public Mapx toMap(String json) {
        try {
            return JacksonConvertUtil.toMap(objectMapper.readTree(json));
        } catch (Throwable e) {
            logger.error("#", e);
            return null;
        }
    }

    /**
     * 将JSON字符串，解码为List对象
     * @param json JSON字符串
     * @return JSON对应的List对象，如果无法解析将返回NULL.
     */
    @Override
    public Listx toList(String json) {
        try {
            return JacksonConvertUtil.toList(objectMapper.readTree(json));
        } catch (Throwable e) {
            logger.error("#", e);
            return null;
        }
    }

    /**
     * 将JSON对象转化为指定类型的JAVA对象
     * @param <T>  JAVA对象泛型
     * @param node JSON对象
     * @param type JAVA对象类型
     * @return 转换后的JAVA对象
     */
    public <T> T convertValue(JsonNode node, Class<T> type) {
        if (node == null) {
            return null;
        }
        return objectMapper.convertValue(node, type);
    }

    /**
     * 将JSON对象转化为指定类型的JAVA对象
     * @param <T>   JAVA对象泛型
     * @param node  JSON对象
     * @param token 类型标记
     * @return 转换后的JAVA对象
     */
    public <T> T convertValue(JsonNode node, TypeReference<T> token) {
        if (node == null) {
            return null;
        }
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType valueType = typeFactory.constructType(token.getType());
            return objectMapper.convertValue(node, valueType);
        } catch (Throwable e) {
            logger.error("#", e);
            return null;
        }
    }

    /**
     * 创建一个 JSON对象
     * @return JSON对象
     */
    public ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    /**
     * 创建一个 JSON数组对象
     * @return JSON数组对象
     */
    public ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }
}
