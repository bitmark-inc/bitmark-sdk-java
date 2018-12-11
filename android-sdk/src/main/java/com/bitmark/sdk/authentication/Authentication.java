package com.bitmark.sdk.authentication;

import android.support.annotation.NonNull;

import javax.crypto.Cipher;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public interface Authentication {

    void authenticate(@NonNull Cipher cipher);
}
