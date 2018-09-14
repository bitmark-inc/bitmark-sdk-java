package utils.error;

/**
 * @author Hieu Pham
 * @since 9/14/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class HttpException extends RuntimeException {

    private int code;

    private String message;

    public HttpException(int code, String message) {
        super(String.format("A Http Exception has occurred when trying to connect with server. " +
                "The status code is %d. \n Root cause : %s", code, message));
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
