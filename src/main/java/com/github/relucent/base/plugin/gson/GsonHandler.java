package com.github.relucent.base.plugin.gson;

import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.common.json.JsonHandler;
import com.github.relucent.base.common.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

public class GsonHandler implements JsonHandler {

    // ===================================Fields==============================================
    public static final GsonHandler INSTANCE = new GsonHandler();
    private final Logger logger = Logger.getLogger(getClass());
    private final Gson gson;

    // ===================================Constructors========================================

    /**
     * 构造函数
     */
    public GsonHandler() {
        this(new GsonBuilder().create());
    }

    /**
     * 构造函数使用指定 {@link Gson}
     * @param gson {@link Gson}
     */
    public GsonHandler(Gson gson) {
        this.gson = gson;
    }

    // ===================================Methods=============================================
    /**
     * 将JAVA对象编码为JSON字符串
     * @param src JAVA对象
     * @return 对象的JSON字符串
     */
    public String encode(Object src) {
        return gson.toJson(src);
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
            return gson.fromJson(json, type);
        } catch (Exception e) {
            logger.warn("#", e);
            return defaultValue;
        }
    }

    /**
     * 将JSON字符串，解码为Map对象
     * @param json JSON字符串
     * @return JSON对应的Map对象，如果无法解析将返回NULL.
     */
    @Override
    public Mapx toMap(String json) {
        try {
            JsonElement node = new JsonParser().parse(json);
            return GsonConvertUtil.toMap(node);
        } catch (Exception e) {
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
            JsonElement node = new JsonParser().parse(json);
            return GsonConvertUtil.toList(node);
        } catch (Exception e) {
            logger.error("#", e);
            return null;
        }
    }

    /**
     * 将JSON字符串，解码为JAVA对象
     * @param <T> 对象泛型
     * @param json JSON字符串
     * @param token 类型标记
     * @return JSON对应的JAVA对象，如果无法解析将返回NULL.
     */
    public <T> T decode(String json, TypeToken<T> token) {
        return decode(json, token, null);
    }

    /**
     * 将JSON字符串，解码为JAVA对象
     * @param <T> 对象泛型
     * @param json JSON字符串
     * @param token 类型标记
     * @param defaultValue 默认值
     * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
     */
    public <T> T decode(String json, TypeToken<T> token, T defaultValue) {
        try {
            TypeAdapter<T> typeAdapter = gson.getAdapter(token);
            return typeAdapter.fromJson(json);
        } catch (Exception e) {
            logger.error("#", e);
            return defaultValue;
        }
    }
}
