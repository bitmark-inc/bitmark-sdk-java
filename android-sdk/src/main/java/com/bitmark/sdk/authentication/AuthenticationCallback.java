package com.bitmark.sdk.authentication;

import io.reactivex.annotations.NonNull;

import javax.crypto.Cipher;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public interface AuthenticationCallback {

    void onSucceeded(@NonNull Cipher cipher);

    void onFailed();

    void onError(String error);

    void onCancelled();
}
