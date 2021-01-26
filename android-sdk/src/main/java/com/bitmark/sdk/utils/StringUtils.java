package com.bitmark.sdk.utils;

public class StringUtils {

    public static String join(String delimiter, CharSequence... elements) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            builder.append(elements[i]);
            if (i < elements.length - 1) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }
}
