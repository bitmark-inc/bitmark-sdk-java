/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class HttpUtils {

    private HttpUtils() {
    }

    public static String buildArrayQueryString(String name, Object value) {
        if (!value.getClass().isArray()) {
            return name + "=" + value;
        }

        if (value instanceof String[]) {
            return buildArrayQueryString(name, (String[]) value);
        } else if (value instanceof Integer[]) {
            return buildArrayQueryString(name, (Integer[]) value);
        } else if (value instanceof Long[]) {
            return buildArrayQueryString(name, (Long[]) value);
        } else if (value instanceof Float[]) {
            return buildArrayQueryString(name, (Float[]) value);
        } else if (value instanceof Double[]) {
            return buildArrayQueryString(name, (Double[]) value);
        } else if (value instanceof int[]) {
            return buildArrayQueryString(
                    name,
                    ArrayUtil.toIntegerArray((int[]) value)
            );
        } else if (value instanceof long[]) {
            return buildArrayQueryString(
                    name,
                    ArrayUtil.toLongArray((long[]) value)
            );
        } else if (value instanceof double[]) {
            return buildArrayQueryString(
                    name,
                    ArrayUtil.toDoubleArray((double[]) value)
            );
        } else if (value instanceof float[]) {
            return buildArrayQueryString(
                    name,
                    ArrayUtil.toFloatArray((float[]) value)
            );
        } else {
            throw new UnsupportedOperationException("Unsupported object value");
        }

    }

    public static <T> String buildArrayQueryString(String name, T[] values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, length = values.length; i < length; i++) {
            builder.append(name).append("=").append(values[i]);
            if (i < length - 1) {
                builder.append("&");
            }
        }
        return builder.toString();
    }

    public static <T> String mapToJson(Map<String, T> map) {
        if (map == null) {
            return "{}";
        }
        return new GsonBuilder().enableComplexMapKeySerialization()
                .create()
                .toJson(map, Map.class);
    }

    public static Map<String, Object> jsonToMap(String json) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        return new GsonBuilder().create().fromJson(json, type);
    }
}
