/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.sample.samples;
import com.bitmark.sdk.features.Account;

public class AccountSample {
    public static Account createNewAccount() {
        return new Account();
    }

    public static Account getAccountFromRecoveryPhrase(String recoveryPhrase) {
        return Account.fromRecoveryPhrase(recoveryPhrase.split(" "));
    }

    public static String getRecoveryPhraseFromAccount(Account account) {
        return String.join(" ", account.getRecoveryPhrase().getMnemonicWords()) ;
    }
}
