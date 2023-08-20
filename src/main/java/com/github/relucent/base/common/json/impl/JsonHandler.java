package com.github.relucent.base.common.json.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;

import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.json.JsonConfig;
import com.github.relucent.base.common.logging.Logger;
import com.github.relucent.base.common.reflect.TypeReference;

public class JsonHandler implements com.github.relucent.base.common.json.JsonHandler {

    private static final JsonConfig CONFIG = new JsonConfig.Builder().build();
    private final Logger logger = Logger.getLogger(getClass());

    /**
     * 将Java对象转化为JSON字符串
     * @param object java对象
     * @return JSON字符串
     */
    public String encode(Object object) {
        StringWriter writer = new StringWriter();
        new JsonWriter(writer, CONFIG).writeObject(object);
        return writer.toString();
    }

    /**
     * 将JSON字符串转化为Java对象
     * @param json JSON字符串
     * @param type 转化的对象类型
     * @return Java对象
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T decode(String json, Class<T> type) {
        return (T) decode(json, (Type) type);
    }

    /**
     * 将JSON字符串，解码为JAVA对象
     * @param <T> 对象泛型
     * @param json JSON字符串
     * @param token JAVA对象类型标记
     * @return JSON对应的JAVA对象，如果无法解析将返回NULL.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T decode(String json, TypeReference<T> token) {
        return (T) decode(json, (Type) token.getType());
    }

    /**
     * 将JSON字符串，解码为JAVA对象
     * @param json JSON字符串
     * @param type JAVA对象类型
     * @return JSON对应的JAVA对象，如果无法解析将返回NULL.
     */
    public Object decode(String json, Type type) {
        throw new UnsupportedOperationException("#decode(String, TypeReference)");
    }

    /**
     * 将Java对象解析为JAVA对象
     * @param <T> JAVA对象泛型
     * @param json JSON字符串
     * @return JAVA对象
     */
    @SuppressWarnings("unchecked")
    protected <T> T decode(String json) {
        try {
            return (T) new JsonTokener(new StringReader(json)).nextValue();
        } catch (Exception e) {
            logger.warn("#", e);
            return null;
        }
    }

    /**
     * 将Java对象解析为MAP对象
     * @param json JSON字符串
     * @return MAP对象,如果解析失败返回null
     */
    public Mapx toMap(String json) {
        try {
            return (Mapx) decode(json);
        } catch (Exception e) {
            logger.warn("#", e);
            return null;
        }
    }

    /**
     * 将Java对象解析为LIST对象
     * @param json LIST字符串
     * @return LIST对象,如果解析失败返回null
     */
    public Listx toList(String json) {
        try {
            return (Listx) decode(json);
        } catch (Exception e) {
            logger.warn("#", e);
            return null;
        }
    }
}
