/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication;

import javax.annotation.Nullable;
import javax.crypto.Cipher;

public interface AuthenticationCallback {

    void onSucceeded(@Nullable Cipher cipher);

    void onFailed();

    void onError(String error);

    void onCancelled();
}
