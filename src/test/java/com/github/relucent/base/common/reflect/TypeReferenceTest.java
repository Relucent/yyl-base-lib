package com.github.relucent.base.common.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TypeReferenceTest {

    @Test
    public void testToken() throws ParseException {
        TypeReference<String> stringTypeToken = TypeReference.of(String.class);
        Assert.assertEquals(stringTypeToken.getType(), String.class);
        TypeReference<Map<String, Object>> complexTypeToken = new TypeReference<Map<String, Object>>() {
        };
        Type complexType = complexTypeToken.getType();
        Assert.assertTrue(complexType instanceof ParameterizedType);
        ParameterizedType complexParameterizedType = (ParameterizedType) complexType;
        Type[] typeArguments = complexParameterizedType.getActualTypeArguments();
        Assert.assertTrue(typeArguments.length == 2);
        Assert.assertTrue(String.class.isAssignableFrom((Class<?>) typeArguments[0]));
        Assert.assertTrue(Object.class.isAssignableFrom((Class<?>) typeArguments[1]));
    }
}
