package com.bitmark.sdk.authentication;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * @author Hieu Pham
 * @since 12/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */
interface ActivityListener {

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}
