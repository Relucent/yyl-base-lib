package com.github.relucent.base.common.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 映射表工具类
 * @author _yyl
 */
public class MapUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected MapUtil() {
    }

    /**
     * 判断映射表是否为空
     * @param map 映射表
     * @return 映射表是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断映射表是否不为空
     * @param map 映射表
     * @return 映射表是否不为空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 获取映射表的大小
     * @param map 映射表
     * @return 映射表的大小
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    /**
     * 将 Map&lt;String, String[]&gt; 转换为 Map&lt;String, String&gt;，支持多种取值策略。
     * @param parameters 原始参数
     * @param strategy   值的提取策略：FIRST、LAST、JOIN_ALL、INDEX
     * @param delimiter  拼接用的分隔符（仅在 JOIN_ALL 生效）
     * @param index      仅在 INDEX 模式下有效，指定取第几个值（从 0 开始）
     * @return 转换后的 Map&lt;String, String&gt;
     */
    public static Map<String, String> toSingleValueMap(Map<String, String[]> parameters, ValuePickStrategy strategy,
            String delimiter, int index) {
        if (parameters == null || parameters.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new HashMap<>(parameters.size());

        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            if (values == null || values.length == 0) {
                continue;
            }

            switch (strategy) {
            case FIRST:
                result.put(key, values[0]);
                break;
            case LAST:
                result.put(key, values[values.length - 1]);
                break;
            case JOIN_ALL:
                result.put(key, String.join(delimiter, values));
                break;
            case INDEX:
                if (index >= 0 && index < values.length) {
                    result.put(key, values[index]);
                }
                break;
            }
        }

        return result;
    }

    /** 值选取策略 */
    public static enum ValuePickStrategy {
        /** 只取第一个 */
        FIRST, //
        /** 只取最后一个 */
        LAST,
        /** 拼接全部 */
        JOIN_ALL,
        /** 指定索引 */
        INDEX
    }
}
