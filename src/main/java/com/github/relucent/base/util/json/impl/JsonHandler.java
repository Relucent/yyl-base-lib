package com.github.relucent.base.util.json.impl;

import java.util.Map;

import com.github.relucent.base.util.bean.BeanUtil;
import com.github.relucent.base.util.collection.Listx;
import com.github.relucent.base.util.collection.Mapx;

public class JsonHandler implements com.github.relucent.base.util.json.JsonHandler {

    private static JsonEncoder encoder = new JsonEncoder();
    private static JsonDecoder decoder = new JsonDecoder();

    /**
     * 将Java对象转化为JSON字符串
     * @param object java对象
     * @return JSON字符串
     */
    public String encode(Object object) {
        return encoder.encode(object);
    }

    /**
     * 将JSON字符串转化为Java对象
     * @param json JSON字符串
     * @param type 转化的对象类型
     * @return Java对象
     */
    @Override
    public <T> T decode(String json, Class<T> type) {
        try {
            Map<String, Object> properties = toMap(json);
            return BeanUtil.newBean(type, properties);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将Java对象解析为MAP对象
     * @param json JSON字符串
     * @return MAP对象,如果解析失败返回null
     */
    public Mapx toMap(String json) {
        return decoder.toMap(json);
    }

    /**
     * 将Java对象解析为LIST对象
     * @param json LIST字符串
     * @return LIST对象,如果解析失败返回null
     */
    public Listx toList(String json) {
        return decoder.toList(json);
    }
}
