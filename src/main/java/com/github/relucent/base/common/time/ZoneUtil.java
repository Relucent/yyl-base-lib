package com.github.relucent.base.common.time;

import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

import com.github.relucent.base.common.lang.ObjectUtil;

/**
 * 时区工具类
 */
public class ZoneUtil {

	// =================================Fields=================================================
	/**
	 * 全局的默认时区ID，初始化时取系统默认的时区，可以通过{@code #setDefaultZoneId(ZoneId)}方法设置
	 */
	private static final AtomicReference<ZoneId> DEFAULT_ZONE_ID = new AtomicReference<>(ZoneId.systemDefault());

	// =================================Methods===============================================
	/**
	 * 获得默认时区ID
	 * @return 获得默认时区ID
	 */
	public static ZoneId getDefaultZoneId() {
		return DEFAULT_ZONE_ID.get();
	}

	/**
	 * 设置默认时区ID
	 * @param zoneId 时区ID，为 {@code null} 时重置为系统默认时区
	 */
	public static void setDefaultZoneId(ZoneId zoneId) {
		DEFAULT_ZONE_ID.set(ObjectUtil.defaultIfNull(zoneId, ZoneId.systemDefault()));
	}

	/**
	 * 重置默认时区ID为系统默认时区{@link ZoneId#systemDefault()}<br>
	 * 由于默认时区是全局静态状态，单元测试中修改后应在 {@code @After} 中调用本方法还原，避免污染其他测试
	 * @return 重置前的时区ID
	 */
	public static ZoneId resetDefaultZoneId() {
		return DEFAULT_ZONE_ID.getAndSet(ZoneId.systemDefault());
	}
}
