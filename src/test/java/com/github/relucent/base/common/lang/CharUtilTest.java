package com.github.relucent.base.common.lang;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CharUtilTest {

    @Test
    public void testIsAscii() {
        assertTrue(CharUtil.isAscii('a'));
        assertTrue(CharUtil.isAscii('~'));
        assertFalse(CharUtil.isAscii('中'));
    }

    @Test
    public void testIsAsciiPrintable() {
        assertTrue(CharUtil.isAsciiPrintable('a'));
        assertFalse(CharUtil.isAsciiPrintable('\n'));
        assertFalse(CharUtil.isAsciiPrintable('中'));
    }

    @Test
    public void testIsAsciiControl() {
        assertTrue(CharUtil.isAsciiControl('\n'));
        assertTrue(CharUtil.isAsciiControl((char) 127));
        assertFalse(CharUtil.isAsciiControl('a'));
    }

    @Test
    public void testIsAsciiAlpha() {
        assertTrue(CharUtil.isAsciiAlpha('a'));
        assertTrue(CharUtil.isAsciiAlpha('Z'));
        assertFalse(CharUtil.isAsciiAlpha('1'));
        assertFalse(CharUtil.isAsciiAlpha('中'));
    }

    @Test
    public void testIsAsciiAlphaUpper() {
        assertTrue(CharUtil.isAsciiAlphaUpper('A'));
        assertFalse(CharUtil.isAsciiAlphaUpper('a'));
    }

    @Test
    public void testIsAsciiAlphaLower() {
        assertTrue(CharUtil.isAsciiAlphaLower('a'));
        assertFalse(CharUtil.isAsciiAlphaLower('A'));
    }

    @Test
    public void testIsAsciiNumber() {
        assertTrue(CharUtil.isAsciiNumber('3'));
        assertFalse(CharUtil.isAsciiNumber('a'));
    }

    @Test
    public void testIsAsciiAlphaOrNumber() {
        assertTrue(CharUtil.isAsciiAlphaOrNumber('a'));
        assertTrue(CharUtil.isAsciiAlphaOrNumber('3'));
        assertFalse(CharUtil.isAsciiAlphaOrNumber('-'));
    }

    @Test
    public void testIsBlankChar() {
        assertTrue(CharUtil.isBlankChar(' '));
        assertTrue(CharUtil.isBlankChar('\t'));
        assertFalse(CharUtil.isBlankChar('a'));
    }

	@Test
	public void testIsEmoji() {
		// 普通 BMP 字符（字母、数字、中文、标点）不是 emoji
		assertFalse(CharUtil.isEmoji('a'));
		assertFalse(CharUtil.isEmoji('1'));
		assertFalse(CharUtil.isEmoji('中'));
		assertFalse(CharUtil.isEmoji('!'));
		// 箭头 U+2192 不在 emoji 区块，按实现返回 false
		assertFalse(CharUtil.isEmoji('\u2192'));

		// BMP 内的 Emoji 区块
		assertTrue(CharUtil.isEmoji('\u2600')); // ☀ Miscellaneous Symbols
		assertTrue(CharUtil.isEmoji('\u2764')); // ❤ Dingbats
		assertTrue(CharUtil.isEmoji('\u00A9')); // © 具 Emoji 表现形式的字符
		assertTrue(CharUtil.isEmoji('\u00AE')); // ® 具 Emoji 表现形式的字符

		// 补充平面 Emoji：以 Unicode 码点（int）传入，而非单独代理区字符
		assertTrue(CharUtil.isEmoji(0x1F600)); // 😀 Emoticons
		assertTrue(CharUtil.isEmoji(0x1F680)); // 🚀 Transport and Map Symbols
		assertTrue(CharUtil.isEmoji(0x1F3F4)); // 🏴 Miscellaneous Symbols and Pictographs
		// 单独的代理区字符不是合法码点，按实现返回 false（需先转成 codePoint 再判断）
		assertFalse(CharUtil.isEmoji(0xD83D)); // 高代理区
		assertFalse(CharUtil.isEmoji(0xDE00)); // 低代理区
	}

    @Test
    public void testIsFileSeparator() {
        assertTrue(CharUtil.isFileSeparator('/'));
        assertTrue(CharUtil.isFileSeparator('\\'));
        assertFalse(CharUtil.isFileSeparator('a'));
    }

    @Test
    public void testIsCharClass() {
        assertTrue(CharUtil.isCharClass(char.class));
        assertTrue(CharUtil.isCharClass(Character.class));
        assertFalse(CharUtil.isCharClass(String.class));
    }

    @Test
    public void testEquals() {
        assertTrue(CharUtil.equals('a', 'a', false));
        assertFalse(CharUtil.equals('a', 'b', false));
        assertTrue(CharUtil.equals('a', 'A', true));
        assertFalse(CharUtil.equals('a', 'A', false));
    }
}
