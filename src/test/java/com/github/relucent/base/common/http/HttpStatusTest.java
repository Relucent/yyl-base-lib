package com.github.relucent.base.common.http;

import org.junit.Assert;
import org.junit.Test;

public class HttpStatusTest {
    @Test
    public void testValues() {
        for (HttpStatus httpStatus : HttpStatus.values()) {
            Assert.assertEquals(httpStatus, HttpStatus.resolve(httpStatus.value()));
        }
    }
}
