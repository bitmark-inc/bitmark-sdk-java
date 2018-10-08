package apiservice.utils.error;

import cryptography.error.ValidateException;

/**
 * @author Hieu Pham
 * @since 8/28/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class InvalidAddressException extends ValidateException {

    public InvalidAddressException() {
        this("This is not an address");
    }

    public InvalidAddressException(String message) {
        super(message);
    }
}
