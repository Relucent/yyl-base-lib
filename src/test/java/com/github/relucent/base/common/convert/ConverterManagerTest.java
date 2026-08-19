package com.github.relucent.base.common.convert;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link ConverterManager} 单元测试
 */
public class ConverterManagerTest {

    @Test
    public void testGetInstanceIsSingleton() {
        Assert.assertSame(ConverterManager.getInstance(), ConverterManager.getInstance());
    }

    @Test
    public void testLookupDefaultConverters() {
        ConverterManager manager = ConverterManager.getInstance();
        // 数值类型应该注册了 NumberConverter
        Assert.assertNotNull(manager.lookup(Integer.class));
        Assert.assertNotNull(manager.lookup(Long.class));
        Assert.assertNotNull(manager.lookup(Double.class));
        Assert.assertNotNull(manager.lookup(BigDecimal.class));
        // 布尔/字符/字符串/日期
        Assert.assertNotNull(manager.lookup(Boolean.class));
        Assert.assertNotNull(manager.lookup(Character.class));
        Assert.assertNotNull(manager.lookup(String.class));
        Assert.assertNotNull(manager.lookup(java.util.Date.class));
        Assert.assertNotNull(manager.lookup(java.util.Calendar.class));
        // JDK8 时间
        Assert.assertNotNull(manager.lookup(java.time.LocalDateTime.class));
        Assert.assertNotNull(manager.lookup(java.time.LocalDate.class));
        Assert.assertNotNull(manager.lookup(java.time.Instant.class));
        Assert.assertNotNull(manager.lookup(java.time.Duration.class));
        Assert.assertNotNull(manager.lookup(java.time.Period.class));
        // 其它
        Assert.assertNotNull(manager.lookup(java.util.TimeZone.class));
        Assert.assertNotNull(manager.lookup(java.time.ZoneId.class));
        Assert.assertNotNull(manager.lookup(java.util.Locale.class));
    }

    @Test
    public void testLookupPrimitive() {
        ConverterManager manager = ConverterManager.getInstance();
        Assert.assertNotNull(manager.lookup(int.class));
        Assert.assertNotNull(manager.lookup(boolean.class));
        Assert.assertNotNull(manager.lookup(byte.class));
        Assert.assertNotNull(manager.lookup(long.class));
        Assert.assertNotNull(manager.lookup(double.class));
    }

    @Test
    public void testLookupUnregisteredReturnsNull() {
        ConverterManager manager = ConverterManager.getInstance();
        // 没有注册转换器的自定义类型应返回 null
        Assert.assertNull(manager.lookup(MyUnregisteredType.class));
    }

    @Test
    public void testRegisterAndUnregister() {
        ConverterManager manager = ConverterManager.getInstance();
        Type type = MyUnregisteredType.class;
        // 注册前不存在
        Assert.assertNull(manager.lookup(type));
        // 注册自定义转换器
        Converter<MyUnregisteredType> customConverter = (source, toType) -> new MyUnregisteredType("custom");
        manager.register(MyUnregisteredType.class, customConverter);
        Assert.assertSame(customConverter, manager.lookup(type));
        // 取消注册后应消失
        manager.unregister(type);
        Assert.assertNull(manager.lookup(type));
    }

    @Test
    public void testLookupByType() {
        ConverterManager manager = ConverterManager.getInstance();
        // 通过 Type 重载检索
        Converter<?> converter = manager.lookup((Type) String.class);
        Assert.assertNotNull(converter);
    }

    /** 用于测试的未注册类型 */
    private static class MyUnregisteredType {
        private final String value;

        MyUnregisteredType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
