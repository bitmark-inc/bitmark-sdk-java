package com.bitmark.sdk.authentication;

import android.app.Activity;
import com.bitmark.sdk.authentication.error.AuthenticationRequiredException;
import com.bitmark.sdk.authentication.error.HardwareNotSupportedException;

import javax.crypto.Cipher;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
public interface Authenticator {

    void authenticate(Activity activity, String title, String description, Cipher cipher,
                      AuthenticationCallback callback);

    boolean isHardwareDetected();

    boolean isEnrolled();

    void checkAvailability() throws AuthenticationRequiredException, HardwareNotSupportedException;
}
