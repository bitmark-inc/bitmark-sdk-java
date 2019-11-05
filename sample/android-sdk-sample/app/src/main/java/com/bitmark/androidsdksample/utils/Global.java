/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.androidsdksample.utils;

import android.content.Context;
import android.widget.Toast;

import com.bitmark.sdk.features.Account;

public class Global {
    public static Account currentAccount;

    public static boolean hasCurrentAccount(Context context) {
        boolean hasCurrentAccount = currentAccount != null;
        if (!hasCurrentAccount) {
            Toast.makeText(context, "Please create account first", Toast.LENGTH_LONG).show();
        }
        return hasCurrentAccount;
    }
}
