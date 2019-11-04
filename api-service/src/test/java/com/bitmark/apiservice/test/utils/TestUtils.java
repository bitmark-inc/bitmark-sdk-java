/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.test.utils;

import com.bitmark.apiservice.utils.Pair;

import java.lang.reflect.Field;

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
