/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.androidsdksample.utils;

import android.content.Context;

import com.bitmark.androidsdksample.BuildConfig;
import com.bitmark.sdk.utils.SharedPreferenceApi;

import static com.bitmark.androidsdksample.constants.AppConstants.ACTIVE_ACCOUNT_NUMBER;
import static com.bitmark.androidsdksample.constants.AppConstants.ENCRYPTION_KEY_ALIAS;

public class KeyUtil {
    public static String getRandomEncryptionKeyAlias(String accountNumber) {
        return accountNumber + "." + System.currentTimeMillis() + ".encryption_key";
    }

    public static void saveEncryptionKeyAlias(Context context, String alias) {
        SharedPreferenceApi preferenceApi = new SharedPreferenceApi(context, BuildConfig.APPLICATION_ID);
        preferenceApi.put(ENCRYPTION_KEY_ALIAS, alias);
    }

    public static String getEncryptionKeyAlias(Context context) {
        SharedPreferenceApi preferenceApi = new SharedPreferenceApi(context, BuildConfig.APPLICATION_ID);
        return preferenceApi.get(ENCRYPTION_KEY_ALIAS, String.class);
    }

    public static void saveAccountNumber(Context context, String accountNumber) {
        new SharedPreferenceApi(context, BuildConfig.APPLICATION_ID)
                .put(ACTIVE_ACCOUNT_NUMBER, accountNumber);
    }

    public static String getAccountNumber(Context context) {
        return new SharedPreferenceApi(context, BuildConfig.APPLICATION_ID).get(ACTIVE_ACCOUNT_NUMBER, String.class);
    }
}
