package com.github.relucent.base.common.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Test;

public class NumberUtilTest {

	@Test
	public void testToScaledBigDecimalDefault() {
		// 默认保留两位小数
		Assert.assertEquals(new BigDecimal("2.50"), NumberUtil.toScaledBigDecimal(new BigDecimal("2.5")));
		Assert.assertEquals(new BigDecimal("1.23"), NumberUtil.toScaledBigDecimal("1.234"));
	}

	@Test
	public void testToScaledBigDecimalRoundingHalfEven() {
		// 默认舍入模式为 HALF_EVEN（银行家舍入），并非 javadoc 所说的“四舍五入”(HALF_UP)
		Assert.assertEquals(new BigDecimal("0.12"), NumberUtil.toScaledBigDecimal(new BigDecimal("0.125"), 2, RoundingMode.HALF_EVEN));
		Assert.assertEquals(new BigDecimal("0.14"), NumberUtil.toScaledBigDecimal(new BigDecimal("0.135"), 2, RoundingMode.HALF_EVEN));
	}

	@Test
	public void testToScaledBigDecimalNull() {
		Assert.assertEquals(BigDecimal.ZERO, NumberUtil.toScaledBigDecimal((BigDecimal) null, 2, RoundingMode.HALF_EVEN));
		Assert.assertEquals(BigDecimal.ZERO, NumberUtil.toScaledBigDecimal((String) null));
	}

	@Test
	public void testToBigDecimal() {
		Assert.assertNull(NumberUtil.toBigDecimal("NaN")); // NaN 按 null 处理，与其他方式一致
		Assert.assertEquals(new BigDecimal("26"), NumberUtil.toBigDecimal("0x1A")); // 十六进制
		Assert.assertEquals(new BigDecimal("1.5"), NumberUtil.toBigDecimal("+1.5")); // 前导 + 号
		Assert.assertNull(NumberUtil.toBigDecimal((Number) null));
	}

	@Test(expected = NumberFormatException.class)
	public void testToBigDecimalBlankThrows() {
		NumberUtil.toBigDecimal("");
	}

	@Test
	public void testToBigInteger() {
		Assert.assertEquals(BigInteger.valueOf(123), NumberUtil.toBigInteger(123));
		Assert.assertEquals(BigInteger.valueOf(7), NumberUtil.toBigInteger(new BigInteger("7")));
	}

	@Test
	public void testMinMax() {
		Assert.assertEquals(1, NumberUtil.min(1, 2, 3));
		Assert.assertEquals(2, NumberUtil.min(new int[] { 5, 2, 8 }));
		Assert.assertEquals(8, NumberUtil.max(new int[] { 5, 2, 8 }));
		Assert.assertEquals(1L, NumberUtil.min(3L, 1L, 2L));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMinNullThrows() {
		NumberUtil.min((int[]) null);
	}

	@Test
	public void testIsDigits() {
		Assert.assertTrue(NumberUtil.isDigits("123"));
		Assert.assertFalse(NumberUtil.isDigits("12a3"));
	}

	@Test
	public void testIsNumber() {
		Assert.assertTrue(NumberUtil.isNumber("123"));
		Assert.assertTrue(NumberUtil.isNumber("1.5"));
		Assert.assertTrue(NumberUtil.isNumber("1E3"));
		Assert.assertTrue(NumberUtil.isNumber("0x1F")); // 十六进制
		Assert.assertTrue(NumberUtil.isNumber("123L")); // 类型限定符
		Assert.assertFalse(NumberUtil.isNumber("08")); // 八进制非法字符 8
		Assert.assertFalse(NumberUtil.isNumber(""));
		Assert.assertFalse(NumberUtil.isNumber("abc"));
	}
}
