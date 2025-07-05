package com.github.relucent.base.common.collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.github.relucent.base.common.lang.ObjectUtil;

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
     * 根据条件进行过滤，返回新的MAP对象
     * @param <K>    Key类型
     * @param <V>    Value类型
     * @param map    Map
     * @param filter 过滤器接口，{@code null}返回原Map
     * @return 过滤后的Map
     */
    public static <K, V> Map<K, V> filter(Map<K, V> map, Predicate<Entry<K, V>> filter) {

        if (isEmpty(map)) {
            return map;
        }

        Map<K, V> result = MapUtil.newMapIfPossible(map.getClass());

        if (!result.isEmpty()) {
            result.clear();
        }

        // 没有过滤器的情况，直接复制
        if (filter == null) {
            result.putAll(map);
            return result;
        }

        // 过滤
        for (Entry<K, V> entry : map.entrySet()) {
            if (filter.test(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 根据指定的 Map 类型创建实例。<br>
     * 如果无法创建指定类型的 Map（例如传入 null、类型非法或实例化失败），方法将返回一个 {@link HashMap} 作为默认实现，保证返回值非 null。<br>
     * @param mapType Map 的实现类或接口
     * @param <K>     key 类型
     * @param <V>     value 类型
     * @return 可用的 Map 实例，不会为 null
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <K, V> Map<K, V> newMapIfPossible(Class<? extends Map> mapType) {

        // 类型非法时返回默认实现
        if (mapType == null || !Map.class.isAssignableFrom(mapType)) {
            return new HashMap<>();
        }

        Map<K, V> map = ObjectUtil.newInstanceIfPossible(mapType);

        // 实例化失败时返回默认实现
        if (map == null) {
            return new HashMap<>();
        }

        return map;
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
