package com.github.relucent.base.common.collection;

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
}
