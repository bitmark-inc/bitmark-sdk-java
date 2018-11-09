package com.bitmark.apiservice.utils.error;

import java.io.IOException;

/**
 * @author Hieu Pham
 * @since 9/14/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class NetworkException extends IOException {

    public NetworkException(String message) {
        super(message);
    }

}
