package service.middleware;

import okhttp3.Response;
import service.response.RegistrationResponse;
import utils.callback.Callback1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Converter {

    public static Callback1<Response> toBitmarkIds(Callback1<List<String>> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    String[] txIds =
                            raw.replaceAll("(\\[|\\]|\\{|\\}|\"|bitmarks|:|id)", "").split(",");
                    callback.onSuccess(new ArrayList<>(Arrays.asList(txIds)));
                } catch (IOException e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toRegistrationResponse(Callback1<RegistrationResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    String[] content =
                            raw.replaceAll("(\\[|\\]|\\{|\\}|\"|assets|:|id|duplicate)", "").split(",");
                    callback.onSuccess(new RegistrationResponse(content[0],
                            Boolean.valueOf(content[1])));
                } catch (IOException e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toTxId(Callback1<String> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    String[] content = raw.replaceAll("[{}\"]", "").split(":");
                    callback.onSuccess(content[1]);
                } catch (IOException e) {
                    callback.onError(e);
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toOfferId(Callback1<String> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    String[] content = raw.replaceAll("[{}\"]", "").split(":");
                    callback.onSuccess(content[1]);
                } catch (IOException e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

}
