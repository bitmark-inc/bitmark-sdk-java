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
