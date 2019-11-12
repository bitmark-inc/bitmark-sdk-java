/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.ktx.util

import java.security.SecureRandom

private val ALPHANUMERIC = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"

fun randomString(length: Int): String {
    val builder = StringBuilder()
    val random = SecureRandom()
    val bound = ALPHANUMERIC.length - 1
    for (i in 0 until length) {
        builder.append(ALPHANUMERIC[random.nextInt(bound)])
    }
    return builder.toString()
}