package com.bitmark.androidsdksample.samples;

import android.app.Activity;
import android.content.Context;

import com.bitmark.apiservice.utils.callback.Callback0;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.sdk.authentication.KeyAuthenticationSpec;
import com.bitmark.sdk.features.Account;

import io.reactivex.Single;

import static com.bitmark.androidsdksample.utils.KeyUtil.getEncryptionKeyAlias;
import static com.bitmark.androidsdksample.utils.KeyUtil.getRandomEncryptionKeyAlias;
import static com.bitmark.androidsdksample.utils.KeyUtil.saveAccountNumber;
import static com.bitmark.androidsdksample.utils.KeyUtil.saveEncryptionKeyAlias;

public class StoreKeySample {
    public static void storeAccountToKeystore(Account account, Context context, Activity activity) {
        String accountNumber = account.getAccountNumber();
        final String encryptionKeyAlias = getRandomEncryptionKeyAlias(accountNumber);

        KeyAuthenticationSpec spec = new KeyAuthenticationSpec.Builder(context)
                .setKeyAlias(encryptionKeyAlias)
                .setAuthenticationRequired(false).build();

        account.saveToKeyStore(activity, spec, new Callback0() {
            @Override
            public void onSuccess() {
                saveEncryptionKeyAlias(context, encryptionKeyAlias);
                saveAccountNumber(context, accountNumber);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public static Single<Account> getAccountFromKeyStore(String accountNumber, Context context, Activity activity) {
        KeyAuthenticationSpec spec = new KeyAuthenticationSpec.Builder(
                context).setKeyAlias(getEncryptionKeyAlias(context))
                .build();

        Single<Account> single = Single.create(singleSubscriber -> {
            Account.loadFromKeyStore(activity, accountNumber, spec,
                    new Callback1<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            singleSubscriber.onSuccess(account);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            singleSubscriber.onError(throwable);
                        }
                    });
        });

        return single;
    }
}
