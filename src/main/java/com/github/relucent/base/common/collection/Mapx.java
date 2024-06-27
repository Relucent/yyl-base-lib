package com.github.relucent.base.common.collection;

import java.util.Date;
import java.util.LinkedHashMap;

import com.github.relucent.base.common.convert.ConvertUtil;

/**
 * JSON对象<br>
 */
@SuppressWarnings("serial")
public class Mapx extends MapWrapper<String, Object> implements Cloneable {

	// ==============================Fields==============================================
	// ...

	// ==============================Constructors========================================
	public Mapx() {
		super(new LinkedHashMap<>());
	}

	// ==============================Methods=============================================
	@Override
	public Mapx clone() throws CloneNotSupportedException {
		return (Mapx) super.clone();
	}

	// ==============================OverrideMethods=====================================
	/**
	 * 设置键值对
	 * @param key 键
	 * @param value 值对象
	 * @return 旧值
	 */
	@Override
	public Object put(String key, Object value) {
		return super.put(convertKey(key), value);
	}

	@Override
	public Object get(Object key) {
		return super.get(convertKey(key));
	}

	@Override
	public Object remove(Object key) {
		return super.remove(convertKey(key));
	}

	// ==============================StandardMethods=====================================
	/**
	 * 设置键值对
	 * @param key 键
	 * @param value 值对象
	 */
	public void set(String key, Object value) {
		put(key, value);
	}

	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	public Integer getInteger(String key) {
		return getInteger(key, null);
	}

	public Long getLong(String key) {
		return getLong(key, null);
	}

	public Float getFloat(String key) {
		return getFloat(key, null);
	}

	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public Date getDate(String key) {
		return getDate(key, null);
	}

	public <T extends Enum<T>> T getEnum(String key, Class<T> enumType) {
		return getEnum(key, enumType, null);
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		return ConvertUtil.toBoolean(get(key), defaultValue);
	}

	public Integer getInteger(String key, Integer defaultValue) {
		return ConvertUtil.toInteger(get(key), defaultValue);
	}

	public Long getLong(String key, Long defaultValue) {
		return ConvertUtil.toLong(get(key), defaultValue);
	}

	public Float getFloat(String key, Float defaultValue) {
		return ConvertUtil.toFloat(get(key), defaultValue);
	}

	public Double getDouble(String key, Double defaultValue) {
		return ConvertUtil.toDouble(get(key), defaultValue);
	}

	public String getString(String key, String defaultValue) {
		return ConvertUtil.toString(get(key), defaultValue);
	}

	public Date getDate(String key, Date defaultValue) {
		return ConvertUtil.toDate(get(key), defaultValue);
	}

	public <T extends Enum<T>> T getEnum(String key, Class<T> enumType, T defaultValue) {
		return ConvertUtil.toEnum(get(key), enumType, defaultValue);
	}

	// ==============================ExtendMethods=======================================
	public Mapx getMap(String key) {
		return getMap(key, null);
	}

	public Mapx getMap(String key, Mapx defaultValue) {
		Object value = get(key);
		if (value instanceof Mapx) {
			return (Mapx) value;
		}
		return defaultValue;
	}

	public Listx getList(String key) {
		return getList(key, null);
	}

	public Listx getList(String key, Listx defaultValue) {
		Object value = get(key);
		if (value instanceof Listx) {
			return (Listx) value;
		}
		return defaultValue;
	}

	// ==============================PrivateMethods======================================
	/**
	 * 将输入键转换为另一个对象以存储在Map中
	 * @param key 键
	 * @return 转换后的键
	 */
	private String convertKey(final Object key) {
		return String.valueOf(key);
	}
}
