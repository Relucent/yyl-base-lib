package com.github.relucent.base.common.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.chrono.Era;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahEra;
import java.time.chrono.IsoChronology;
import java.time.chrono.IsoEra;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.time.chrono.MinguoChronology;
import java.time.chrono.MinguoDate;
import java.time.chrono.MinguoEra;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.chrono.ThaiBuddhistEra;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link EraUtil} 单元测试
 */
public class EraUtilTest {

	/**
	 * 当前 JDK 已知的最新日本纪年。<br>
	 * 不能直接写 {@code JapaneseEra.REIWA}：该常量自 Java 13 才存在，JDK 8 的 {@code JapaneseEra} 只有 MEIJI/TAISHO/SHOWA/HEISEI 四个常量（令和
	 * 2019-05 才开始，JDK 8 的历法数据也早于该时间点）
	 */
	private static final JapaneseEra LATEST_JAPANESE_ERA;
	static {
		JapaneseEra[] eras = JapaneseEra.values();
		LATEST_JAPANESE_ERA = eras[eras.length - 1];
	}

	@Test
	public void testIsBce() {
		Assert.assertTrue(EraUtil.isBce(IsoEra.BCE));
		Assert.assertFalse(EraUtil.isBce(IsoEra.CE));
		// 民国历、佛历的"纪元前"同样判定为公元前
		Assert.assertTrue(EraUtil.isBce(MinguoEra.BEFORE_ROC));
		Assert.assertFalse(EraUtil.isBce(MinguoEra.ROC));
		Assert.assertTrue(EraUtil.isBce(ThaiBuddhistEra.BEFORE_BE));
		Assert.assertFalse(EraUtil.isBce(ThaiBuddhistEra.BE));
	}

	@Test
	public void testIsBceForJapaneseEra() {
		// 日本年号是纪年序列而非公元前后的划分，即使数值不大于 0 也不属于公元前
		// 遍历全部年号而非写死常量，避免依赖 JDK 13+ 才有的 REIWA
		for (JapaneseEra era : JapaneseEra.values()) {
			Assert.assertFalse("日本年号不应判为公元前: " + era, EraUtil.isBce(era));
		}
		Assert.assertFalse(EraUtil.isBce(JapaneseEra.MEIJI));
		Assert.assertFalse(EraUtil.isBce(JapaneseEra.HEISEI));
	}

	@Test
	public void testIsBceNull() {
		Assert.assertFalse(EraUtil.isBce(null));
	}

	@Test
	public void testIsCe() {
		Assert.assertTrue(EraUtil.isCe(IsoEra.CE));
		Assert.assertFalse(EraUtil.isCe(IsoEra.BCE));
		Assert.assertTrue(EraUtil.isCe(MinguoEra.ROC));
		Assert.assertFalse(EraUtil.isCe(MinguoEra.BEFORE_ROC));
		// 日本年号与伊斯兰历纪元视为公元后
		Assert.assertTrue(EraUtil.isCe(LATEST_JAPANESE_ERA));
		Assert.assertTrue(EraUtil.isCe(HijrahEra.AH));
	}

	@Test
	public void testIsCeNull() {
		Assert.assertFalse(EraUtil.isCe(null));
	}

	@Test
	public void testOf() {
		Assert.assertSame(IsoEra.BCE, EraUtil.of(IsoChronology.INSTANCE, 0));
		Assert.assertSame(IsoEra.CE, EraUtil.of(IsoChronology.INSTANCE, 1));
		Assert.assertSame(MinguoEra.ROC, EraUtil.of(MinguoChronology.INSTANCE, 1));
		Assert.assertSame(ThaiBuddhistEra.BEFORE_BE, EraUtil.of(ThaiBuddhistChronology.INSTANCE, 0));
		Assert.assertSame(LATEST_JAPANESE_ERA, EraUtil.of(JapaneseChronology.INSTANCE, LATEST_JAPANESE_ERA.getValue()));
	}

	@Test
	public void testOfInvalid() {
		// 历法不存在对应数值的纪元时返回 null，不抛出异常
		Assert.assertNull(EraUtil.of(IsoChronology.INSTANCE, 2));
		Assert.assertNull(EraUtil.of(IsoChronology.INSTANCE, -1));
		Assert.assertNull(EraUtil.of(JapaneseChronology.INSTANCE, 99));
		Assert.assertNull(EraUtil.of(HijrahChronology.INSTANCE, 2));
		Assert.assertNull(EraUtil.of(null, 0));
	}

	@Test
	public void testOfWithDefault() {
		Assert.assertSame(IsoEra.CE, EraUtil.of(IsoChronology.INSTANCE, 1, IsoEra.BCE));
		Assert.assertSame(IsoEra.CE, EraUtil.of(IsoChronology.INSTANCE, 9, IsoEra.CE));
		Assert.assertSame(IsoEra.BCE, EraUtil.of(null, 0, IsoEra.BCE));
	}

	@Test
	public void testGetValue() {
		Assert.assertEquals(Integer.valueOf(0), EraUtil.getValue(IsoEra.BCE));
		Assert.assertEquals(Integer.valueOf(1), EraUtil.getValue(IsoEra.CE));
		Assert.assertEquals(Integer.valueOf(-1), EraUtil.getValue(JapaneseEra.MEIJI));
		Assert.assertNull(EraUtil.getValue((Era) null));
	}

	@Test
	public void testGetValueWithDefault() {
		Assert.assertEquals(1, EraUtil.getValue(IsoEra.CE, -1));
		Assert.assertEquals(0, EraUtil.getValue(IsoEra.BCE, -1));
		Assert.assertEquals(-1, EraUtil.getValue(null, -1));
	}

	@Test
	public void testGetName() {
		// ISO 纪元的名称在各 JDK 版本稳定，可作字面断言
		Assert.assertEquals("公元前", EraUtil.getName(IsoEra.BCE));
		Assert.assertEquals("公元", EraUtil.getName(IsoEra.CE));
		Assert.assertNull(EraUtil.getName(null));
	}

	@Test
	public void testGetNameForChronologies() {
		// 非 ISO 历法的中文名取自 JDK 内置 CLDR 数据，各版本并不一致
		// （例如 JDK 8 下 MinguoEra.BEFORE_ROC 回退为"公元前"，令和的名字要 JDK 13+ 才有），
		// 因此只断言"名称来自 Era#getDisplayName"这一委托契约，不写死 JDK 数据
		Era[] eras = { MinguoEra.BEFORE_ROC, MinguoEra.ROC, JapaneseEra.HEISEI, HijrahEra.AH,
				ThaiBuddhistEra.BEFORE_BE, ThaiBuddhistEra.BE, LATEST_JAPANESE_ERA };
		for (Era era : eras) {
			String name = EraUtil.getName(era);
			Assert.assertNotNull(name);
			Assert.assertFalse("纪元名称不应为空: " + era, name.isEmpty());
			Assert.assertEquals("应委托 Era#getDisplayName: " + era, era.getDisplayName(TextStyle.FULL, Locale.CHINA),
					name);
		}
	}

	@Test
	public void testGetNameWithLocale() {
		// 指定语言环境时使用该语言环境的显示名称
		String english = EraUtil.getName(IsoEra.BCE, Locale.ENGLISH);
		Assert.assertEquals(IsoEra.BCE.getDisplayName(TextStyle.FULL, Locale.ENGLISH), english);
		Assert.assertNotEquals("公元前", english);
		// 语言环境为 null 时回退到中文
		Assert.assertEquals("公元前", EraUtil.getName(IsoEra.BCE, null));
	}

	@Test
	public void testFromLocalDate() {
		Assert.assertSame(IsoEra.CE, EraUtil.from(LocalDate.of(2026, 9, 14)));
		Assert.assertSame(IsoEra.BCE, EraUtil.from(LocalDate.of(-1, 1, 1)));
		Assert.assertSame(IsoEra.BCE, EraUtil.from(LocalDate.of(0, 6, 1)));
		Assert.assertSame(IsoEra.CE, EraUtil.from(LocalDateTime.of(2026, 9, 14, 10, 20)));
	}

	@Test
	public void testFromChronoDate() {
		// 时间对象自带历法时使用其历法提取纪元
		// 不构造 JapaneseDate.of(era, 1, 1, 1)：年号"元年1月1日"可能早于年号起始日
		// （令和始于 5 月 1 日、平成始于 1 月 8 日），会抛 DateTimeException
		JapaneseDate japaneseDate = JapaneseDate.now();
		Assert.assertSame(japaneseDate.getEra(), EraUtil.from(japaneseDate));
		Assert.assertSame(LATEST_JAPANESE_ERA, japaneseDate.getEra());
		Assert.assertSame(MinguoEra.ROC, EraUtil.from(MinguoDate.now()));
	}

	@Test
	public void testFromUnsupported() {
		// 不含纪元字段的时间对象返回 null，不抛出异常
		Assert.assertNull(EraUtil.from(LocalTime.NOON));
		Assert.assertNull(EraUtil.from(Instant.now()));
		Assert.assertNull(EraUtil.from(MonthDay.of(1, 1)));
		Assert.assertNull(EraUtil.from((TemporalAccessor) null));
	}

	@Test
	public void testFromWithDefault() {
		Assert.assertSame(IsoEra.CE, EraUtil.from(LocalDate.of(2026, 9, 14), IsoEra.BCE));
		Assert.assertSame(IsoEra.CE, EraUtil.from(LocalTime.NOON, IsoEra.CE));
		Assert.assertSame(IsoEra.BCE, EraUtil.from((TemporalAccessor) null, IsoEra.BCE));
	}
}
