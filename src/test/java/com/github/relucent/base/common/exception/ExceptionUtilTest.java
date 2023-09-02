package com.github.relucent.base.common.exception;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionUtilTest {

    @Test(expected = GeneralException.class)
    public void testError() {
        throw ExceptionUtil.error("error");
    }

    @Test(expected = PromptException.class)
    public void testPrompt() {
        throw ExceptionUtil.prompt("prompt");
    }

    @Test
    public void testExceptionMessage() {
        String message = "hello";
        try {
            throw ExceptionUtil.prompt(message);
        } catch (Exception e) {
            Assert.assertEquals(message, e.getMessage());
        }
    }
}
