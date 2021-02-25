package com.github.relucent.base.plugin.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.json.JsonHandler;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.reflect.TypeReference;

public class JacksonHandler implements JsonHandler {

    // ===================================Fields==============================================
    public static final JacksonHandler INSTANCE = new JacksonHandler();
    private final Logger logger = Logger.getLogger(getClass());
    private final ObjectMapper objectMapper;

    // ===================================Constructors========================================
    /**
     * 构造函数(使用指定 {@link ObjectMapper})
     * @param objectMapper 对象映射
     */
    public JacksonHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
    public String encode(Object src) {
        try {
            return objectMapper.writeValueAsString(src);
        } catch (Throwable e) {
            logger.warn("#", e);
            return null;
        }
    }

    /**
     * 将JSON字符串解码为JAVA对象
     * @param <T> 对象泛型
     * @param json 对象的JSON字符串
     * @param type JAVA对象类型
     * @return JSON对应的JAVA对象，如果无法解析将返回NULL.
     */
    public <T> T decode(String json, Class<T> type) {
        return decode(json, type, null);
    }

    /**
     * 将JSON字符串，解码为JAVA对象
     * @param <T> 对象泛型
     * @param json JSON字符串
     * @param type JAVA对象类型
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
     * @param <T> 对象泛型
     * @param json JSON字符串
     * @param token 类型标记
     * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
     */
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
}
