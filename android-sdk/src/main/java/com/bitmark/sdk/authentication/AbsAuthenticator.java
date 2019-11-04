/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.authentication;

import android.content.Context;

abstract class AbsAuthenticator implements Authenticator {

    protected final Context context;

    AbsAuthenticator(Context context) {
        this.context = context;
    }
}
