package com.github.relucent.base.common.codec;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.codec.Morse;
import com.github.relucent.base.common.codec.Morse.MarkOption;

public class MorseTest {

    @Test
    public void testEncodeAndDecode() {
        MarkOption option = new MarkOption().setDit('-').setDah('+').setSpace('|');
        String plaintext = "关关雎鸠,在河之洲。";
        String encoded = Morse.encode(plaintext, option);
        String decoded = Morse.decode(encoded, option);
        Assert.assertEquals(plaintext, decoded);
    }
}
