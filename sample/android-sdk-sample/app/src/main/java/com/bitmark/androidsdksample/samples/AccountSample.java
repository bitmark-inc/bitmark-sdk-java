package com.bitmark.androidsdksample.samples;

import android.text.TextUtils;

import com.bitmark.sdk.features.Account;

public class AccountSample {
    public static Account createNewAccount() {
        return new Account();
    }

    public static Account getAccountFromRecoveryPhrase(String recoveryPhrase) {
        return Account.fromRecoveryPhrase(recoveryPhrase.split(" "));
    }

    public static String getRecoveryPhraseFromAccount(Account account) {
        return TextUtils.join(" ", account.getRecoveryPhrase().getMnemonicWords());
    }
}
