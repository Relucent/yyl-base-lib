package com.github.relucent.base.common.time;

import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

import com.github.relucent.base.common.lang.ObjectUtil;

/**
 * 时区工具类
 */
public class ZoneUtil {

    /**
     * 全局的默认时区ID，初始化时取系统默认的时区，可以通过{@code #setDefaultZoneId(ZoneId)}方法设置
     */
    private static final AtomicReference<ZoneId> DEFAULT_ZONE_ID = new AtomicReference<>(ZoneId.systemDefault());

    /**
     * 获得默认时区ID
     * @return 获得默认时区ID
     */
    public static ZoneId getDefaultZoneId() {
        return DEFAULT_ZONE_ID.get();
    }

    /**
     * 设置默认时区ID
     * @param zoneId 时区ID
     */
    public static void setDefaultZoneId(ZoneId zoneId) {
        DEFAULT_ZONE_ID.set(ObjectUtil.defaultIfNull(zoneId, ZoneId.systemDefault()));
    }
}
