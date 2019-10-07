package com.bitmark.apiservice.test.utils;

import com.bitmark.apiservice.utils.Pair;

import java.lang.reflect.Field;

/**
 * @author Hieu Pham
 * @since 1/18/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class TestUtils {

    public static void reflectionSet(
            Object object,
            Pair<String, Object>... fieldValuePairs
    )
            throws NoSuchFieldException, IllegalAccessException {
        for (Pair<String, Object> fieldValuePair : fieldValuePairs) {
            Field field =
                    object.getClass().getDeclaredField(fieldValuePair.first());
            field.setAccessible(true);
            field.set(object, fieldValuePair.second());
        }
    }

    public static Object reflectionGet(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}
