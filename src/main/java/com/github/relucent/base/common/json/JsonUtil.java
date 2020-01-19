package com.github.relucent.base.common.json;


import com.github.relucent.base.common.collection.Listx;
import com.github.relucent.base.common.collection.Mapx;
import com.github.relucent.base.plugin.gson.GsonHandler;
import com.github.relucent.base.plugin.jackson.JacksonHandler;

/**
 * JSON解析工具类<br>
 */
public class JsonUtil {

    // ===================================Fields==============================================
    private static volatile JsonHandler HANDLER = getDefaultHandler();

    // ===================================Methods=============================================
    /**
     * 将JAVA对象编码为JSON字符串
     * @param <T> JAVA对象泛型
     * @param src JAVA对象
     * @return 对象的JSON字符串
     */
    public static <T> String encode(T src) {
        return HANDLER.encode(src);
    }

    /**
     * 将JSON字符串解码为JAVA对象
     * @param <T> JAVA对象泛型
     * @param json 对象的JSON字符串
     * @param type JAVA对象类型
     * @return JSON对应的JAVA对象，如果无法解析将返回NULL.
     */
    public static <T> T decode(String json, Class<T> type) {
        return decode(json, type, null);
    }

    /**
     * 将JSON字符串，解码为JAVA对象
     * @param <T> JAVA对象泛型
     * @param json JSON字符串
     * @param type JAVA对象类型
     * @param defaultValue 默认值
     * @return JSON对应的JAVA对象，如果无法解析将返回默认值.
     */
    public static <T> T decode(String json, Class<T> type, T defaultValue) {
        T object = HANDLER.decode(json, type);
        return object != null ? object : defaultValue;
    }


    /**
     * 将JSON转换为MAP对象
     * @param json JSON字符串
     * @return MAP对象,如果类型不匹配或者转换出现异常则返回null.
     */
    public static Mapx toMap(String json) {
        return HANDLER.toMap(json);
    }

    /**
     * 将JSON转换为LIST对象
     * @param json JSON字符串
     * @return LIST对象,如果类型不匹配或者转换出现异常则返回null.
     */
    public static Listx toList(String json) {
        return HANDLER.toList(json);
    }

    /**
     * 设置JSON处理类
     * @param handler JSON处理类
     */
    public static void setHandler(JsonHandler handler) {
        HANDLER = handler;
    }

    /**
     * 获得JSON处理类
     * @return JSON处理类
     */
    public static JsonHandler getHandler() {
        return HANDLER;
    }

    /**
     * 获得可用的 {@link JsonHandler}
     * @return 可用的 {@link JsonHandler}
     */
    private static JsonHandler getDefaultHandler() {
        JsonHandler handler = null;
        if (handler == null) {
            try {
                handler = JacksonHandler.INSTANCE;
            } catch (Throwable e) {
                /* Ignore */
            }
        }
        if (handler == null) {
            try {
                handler = GsonHandler.INSTANCE;
            } catch (Throwable e) {
                /* Ignore */
            }
        }
        if (handler == null) {
            handler = new com.github.relucent.base.common.json.impl.JsonHandler();
        }
        return handler;
    }
}