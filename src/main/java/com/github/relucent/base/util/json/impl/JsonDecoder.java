package com.github.relucent.base.util.json.impl;

import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.relucent.base.util.collection.Listx;
import com.github.relucent.base.util.collection.Mapx;

/**
 * JSON编码工具类，将JAVA对象编码为JSON字符串。
 * @author _yyl
 * @version 1.0
 */
public class JsonDecoder {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 将Java对象解析为JAVA对象
     * @param <T> JAVA对象泛型
     * @param json JSON字符串
     * @return JAVA对象
     */
    @SuppressWarnings("unchecked")
    protected <T> T decode(String json) {
        try {
            return (T) new JsonTokener(new StringReader("[" + json + "]")).nextList().get(0);
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
