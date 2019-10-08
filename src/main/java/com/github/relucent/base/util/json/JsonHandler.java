package com.github.relucent.base.util.json;

import com.github.relucent.base.util.collection.Listx;
import com.github.relucent.base.util.collection.Mapx;

/**
 * JSON 处理器接口类
 * @author YYL
 */
public interface JsonHandler {

    /**
     * 将Java对象转化为JSON字符串
     * @param object java对象
     * @return JSON字符串
     */
    String encode(Object object);

    /**
     * 将JSON字符串解码为JAVA对象
     * @param <T> JAVA对象泛型
     * @param json 对象的JSON字符串
     * @param type JAVA对象类型
     * @return JSON对应的JAVA对象，如果无法解析将返回NULL.
     */
    <T> T decode(String json, Class<T> type);

    /**
     * 将Java对象解析为MAP对象
     * @param json JSON字符串
     * @return MAP对象,如果解析失败返回null
     */
    Mapx toMap(String json);

    /**
     * 将Java对象解析为LIST对象
     * @param json LIST字符串
     * @return LIST对象,如果解析失败返回null
     */
    Listx toList(String json);
}
