package service.middleware;

import okhttp3.Response;
import service.response.RegistrationResponse;
import utils.callback.Callback1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Converter {

    public static Callback1<Response> toTxIds(Callback1<List<String>> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    List<String> result = new ArrayList<>();
                    String[] txIds = raw.replaceAll("[\\[\\]]", "").split(",");
                    for (String item : txIds) {
                        result.add(item.replaceAll("[{}\"]", "").split(":")[1].trim());
                    }
                    callback.onSuccess(result);
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
                            raw.replaceAll("[\\[\\]{}\"]", "").replaceAll("(id:|duplicate:| )",
                                    "").trim().split(",");
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
