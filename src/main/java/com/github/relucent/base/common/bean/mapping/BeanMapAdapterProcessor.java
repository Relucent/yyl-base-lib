package com.github.relucent.base.common.bean.mapping;

import com.github.relucent.base.common.bean.MapConfig;
import com.github.relucent.base.common.collection.Mapx;

/**
 * 适配器接口 <br>
 * 因为默认的JsonBuilder实现只支持 基本类型，字符串，集合，日期的转换。所以对于其他类型的对象需要先转换为支持类型才能进行JSON转换<br>
 * 该接口用于将任意类型对象转换为Map类型(默认的JsonBuilder提供对Map类型的JSON转换功能）<br>
 * @author YaoYiLang
 * @version 1.2 2009-12-11
 */
public interface BeanMapAdapterProcessor {
    /**
     * 将任意对象转换为Map 形式
     * @param object Map对象
     * @param config 配置
     * @return 转换后的 Map对象
     */
    public Mapx process(Object object, MapConfig config);

    /**
     * 适配器是否支持该类型的Map转换
     * @param clazz 对象的class类型
     * @return 如果支持返回true,否则返回false
     */
    public boolean supports(Class<?> clazz);
}
