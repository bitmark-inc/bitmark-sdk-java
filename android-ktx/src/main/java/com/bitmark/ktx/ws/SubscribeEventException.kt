/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.ktx.ws

class SubscribeEventException(val code: Int,
                              message: String
) : Throwable("code: $code, message: $message ")