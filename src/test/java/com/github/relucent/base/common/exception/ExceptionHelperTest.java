package com.github.relucent.base.common.exception;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionHelperTest {

    @Test(expected = GeneralException.class)
    public void testError() {
        throw ExceptionHelper.error("error");
    }

    @Test(expected = PromptException.class)
    public void testPrompt() {
        throw ExceptionHelper.prompt("prompt");
    }

    @Test
    public void testExceptionMessage() {
        String message = "hello";
        try {
            throw ExceptionHelper.prompt(message);
        } catch (Exception e) {
            Assert.assertEquals(message, e.getMessage());
        }
    }
}
