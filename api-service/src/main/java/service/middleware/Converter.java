package service.middleware;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import okhttp3.Response;
import service.response.GetBitmarkResponse;
import service.response.GetBitmarksResponse;
import service.response.IssueResponse;
import service.response.RegistrationResponse;
import utils.callback.Callback1;
import utils.error.UnexpectedException;

import java.io.IOException;
import java.util.Map;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Converter {

    private static final Gson GSON = new GsonBuilder().create();

    public static Callback1<Response> toIssueResponse(Callback1<IssueResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    IssueResponse response = GSON.fromJson(raw, IssueResponse.class);
                    callback.onSuccess(response);
                } catch (IOException | JsonSyntaxException e) {
                    callback.onError(new UnexpectedException(e));
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
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    RegistrationResponse response = GSON.fromJson(raw, RegistrationResponse.class);
                    callback.onSuccess(response);
                } catch (IOException | JsonSyntaxException e) {
                    callback.onError(new UnexpectedException(e));
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
                    Map<String, String> json = GSON.fromJson(raw, new TypeToken<Map<String,
                            String>>() {
                    }.getType());
                    callback.onSuccess(json.get("txid"));
                } catch (IOException | JsonSyntaxException e) {
                    callback.onError(new UnexpectedException(e));
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toStatus(Callback1<String> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    Map<String, String> json = GSON.fromJson(raw, new TypeToken<Map<String,
                            String>>() {
                    }.getType());
                    callback.onSuccess(json.get("status"));
                } catch (IOException | JsonSyntaxException e) {
                    callback.onError(new UnexpectedException(e));
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
                    Map<String, String> json = GSON.fromJson(raw, new TypeToken<Map<String,
                            String>>() {
                    }.getType());
                    callback.onSuccess(json.get("offer_id"));
                } catch (IOException | JsonSyntaxException e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGetBitmarkResponse(Callback1<GetBitmarkResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    callback.onSuccess(GSON.fromJson(raw, GetBitmarkResponse.class));
                } catch (IOException e) {
                    callback.onError(new UnexpectedException(e));
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGetBitmarksResponse(Callback1<GetBitmarksResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    callback.onSuccess(GSON.fromJson(raw, GetBitmarksResponse.class));
                } catch (IOException e) {
                    callback.onError(new UnexpectedException(e));
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

}
