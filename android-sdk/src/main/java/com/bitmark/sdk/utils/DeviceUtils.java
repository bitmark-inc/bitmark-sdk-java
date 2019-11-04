/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils;

import android.os.Build;

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
