package com.bitmark.sdk.utils;

import android.os.Build;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public class DeviceUtils {

    private DeviceUtils() {
    }

    public static boolean isAboveP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    public static boolean isAboveN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean isAboveM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
