package com.bitmark.sdk.keymanagement;

import android.content.Context;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
abstract class AbsEncryption implements Encryption {

    Context context;

    AbsEncryption(Context context) {
        this.context = context.getApplicationContext();
    }

    abstract String getTransformation();

    Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(getTransformation());
    }
}
