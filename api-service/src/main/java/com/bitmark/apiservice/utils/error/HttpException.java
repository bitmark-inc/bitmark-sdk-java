package com.bitmark.apiservice.utils.error;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.Locale;
import java.util.Map;

/**
 * @author Hieu Pham
 * @since 9/14/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class HttpException extends RuntimeException {

    private int statusCode;

    private int errorCode;

    private String reason;

    private String message;

    public HttpException(int code, String response) {
        super(String.format(
                Locale.getDefault(),
                "A Http Exception has occurred when trying to connect with server. The status code is %d. \n Root cause : %s",
                code,
                response
        ));
        this.statusCode = code;
        Map<String, String> jsonMap = deserialize(response);
        if (jsonMap != null) {
            String codeStr = jsonMap.get("code");
            this.errorCode = codeStr == null ? -1 : Integer.valueOf(codeStr);
            this.message = jsonMap.get("message");
            this.reason = jsonMap.get("reason");
        } else {
            this.message = response;
        }
    }

    public String getErrorMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getReason() {
        return reason;
    }

    private Map<String, String> deserialize(String response) {
        try {
            return new GsonBuilder().create()
                    .fromJson(response, new TypeToken<Map<String,
                            String>>() {
                    }.getType());
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
