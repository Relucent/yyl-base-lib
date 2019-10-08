package com.github.relucent.base.plug.jackson.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.relucent.base.plug.jackson.MyObjectMapper;
import com.github.relucent.base.plug.jackson.databind.TreeNodeConverts;
import com.github.relucent.base.util.collection.Listx;
import com.github.relucent.base.util.collection.Mapx;
import com.github.relucent.base.util.json.JsonHandler;

public class JacksonHandler implements JsonHandler {

    // ===================================Fields==============================================
    public static final JacksonHandler INSTANCE = new JacksonHandler();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // ===================================Methods=============================================
    protected ObjectMapper getObjectMapper() {
        return MyObjectMapper.INSTANCE;
    }

    /**
     * 将JAVA对象编码为JSON字符串
     * @param src JAVA对象
     * @return 对象的JSON字符串
     */
    public String encode(Object src) {
        try {
            return getObjectMapper().writeValueAsString(src);
        } catch (JsonProcessingException e) {
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
            return getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            logger.warn("#", e);
            return defaultValue;
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
            return TreeNodeConverts.toMapx(getObjectMapper().readTree(json));
        } catch (Exception e) {
            logger.error("#", e);
            return null;
        }
    }


    /**
     * 将JSON字符串，解码为List对象(该方法依赖于JACKSON类库)
     * @param json JSON字符串
     * @return JSON对应的List对象，如果无法解析将返回NULL.
     */
    @Override
    public Listx toList(String json) {
        try {
            return TreeNodeConverts.toListx(getObjectMapper().readTree(json));
        } catch (Exception e) {
            logger.error("#", e);
            return null;
        }
    }


    /**
     * 将JSON字符串，解码为JAVA对象(该方法依赖于JACKSON类库)
     * @param <T> 对象泛型
     * @param json JSON字符串
     * @param token 类型标记
     * @return JSON对应的JAVA对象，如果无法解析将返回NULL.
     */
    public <T> T decode(String json, TypeReference<T> token) {
        return decode(json, token, null);
    }

    /**
     * 将JSON字符串，解码为JAVA对象(该方法依赖于JACKSON类库)
     * @param <T> 对象泛型
     * @param json JSON字符串
     * @param token 类型标记
     * @param defaultValue 默认值
     * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
     */
    public <T> T decode(String json, TypeReference<T> token, T defaultValue) {
        try {
            return getObjectMapper().readValue(json, token);
        } catch (Exception e) {
            logger.error("#", e);
            return defaultValue;
        }
    }
}
