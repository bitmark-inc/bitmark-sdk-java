/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.params.query;

import com.bitmark.apiservice.utils.HttpUtils;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbsQueryBuilder implements QueryBuilder {

    @Override
    public QueryParams build() {
        return new QueryParamsImpl(this);
    }

    @Override
    public String toUrlQuery() {
        try {
            StringBuilder builder = new StringBuilder();
            Map<String, Object> valueMap = getValues(this);
            int iteration = 0;
            for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                iteration++;
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value.getClass().isArray()) {
                    builder.append(HttpUtils.buildArrayQueryString(
                            name,
                            value
                    ));
                } else {
                    builder.append(name).append("=").append(value.toString());
                }
                if (iteration < valueMap.size()) {
                    builder.append("&");
                }
            }
            return builder.toString();
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private Map<String, Object> getValues(QueryBuilder builder)
            throws IllegalAccessException {
        Map<String, Object> valueMap = new TreeMap<>();
        Field[] fields = builder.getClass().getDeclaredFields();
        if (fields.length > 0) {
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(builder);
                if (value == null) {
                    continue;
                }
                SerializedName annotationName = field.getAnnotation(
                        SerializedName.class);
                String name;
                if (annotationName != null) {
                    name = annotationName.value();
                } else {
                    name = field.getName();
                }
                valueMap.put(name, value);
            }
        }
        return valueMap;
    }
}
