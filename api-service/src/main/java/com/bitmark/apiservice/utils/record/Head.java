/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils.record;

import com.google.gson.annotations.SerializedName;

public enum Head {

    @SerializedName("head")
    HEAD,

    @SerializedName("moved")
    MOVED,

    @SerializedName("prior")
    PRIOR
}
