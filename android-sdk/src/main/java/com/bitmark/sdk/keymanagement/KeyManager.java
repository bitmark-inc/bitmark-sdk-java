/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.keymanagement;

import com.bitmark.apiservice.utils.callback.Callback0;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.sdk.authentication.KeyAuthenticationSpec;

public interface KeyManager {

    void getKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            Callback1<byte[]> callback
    );

    void saveKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            byte[] key,
            Callback0 callback
    );

    void removeKey(
            String alias,
            KeyAuthenticationSpec keyAuthSpec,
            Callback0 callback
    );
}
