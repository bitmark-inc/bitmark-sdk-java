/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication;

import android.app.Activity;
import com.bitmark.sdk.authentication.error.AuthenticationRequiredException;
import com.bitmark.sdk.authentication.error.HardwareNotSupportedException;

import javax.crypto.Cipher;

public interface Authenticator {

    void authenticate(
            Activity activity,
            String title,
            String description,
            Cipher cipher,
            AuthenticationCallback callback
    );

    boolean isHardwareDetected();

    boolean isEnrolled();

    void checkAvailability() throws
            AuthenticationRequiredException,
            HardwareNotSupportedException;
}
