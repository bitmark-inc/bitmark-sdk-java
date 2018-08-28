package utils.error;

import error.ValidateException;

/**
 * @author Hieu Pham
 * @since 8/27/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class InvalidNetworkException extends ValidateException {

    public InvalidNetworkException(String message) {
        super(message);
    }

    public InvalidNetworkException(int actual) {
        this("Invalid network with value is " + actual);
    }
}
