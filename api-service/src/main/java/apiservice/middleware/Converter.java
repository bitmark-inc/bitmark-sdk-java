package apiservice.middleware;

import apiservice.response.*;
import apiservice.utils.callback.Callback1;
import apiservice.utils.error.UnexpectedException;
import apiservice.utils.record.AssetRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Hieu Pham
 * @since 9/4/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Converter {

    private static final Gson GSON = new GsonBuilder().create();

    public static Callback1<Response> toIssueResponse(Callback1<List<String>> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> mapJson = GSON.fromJson(raw, new TypeToken<Map<String,
                            Object>>() {
                    }.getType());
                    JsonArray jsonArray = GSON.toJsonTree(mapJson.get("bitmarks")).getAsJsonArray();
                    List<String> txIds = new ArrayList<>(jsonArray.size());
                    for (JsonElement jsonElement : jsonArray) {
                        txIds.add(jsonElement.getAsJsonObject().get("id").getAsString());
                    }
                    callback.onSuccess(txIds);
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
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
                } catch (Exception e) {
                    callback.onError(new UnexpectedException(e));
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toAssetRecord(Callback1<AssetRecord> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> jsonMap = GSON.fromJson(raw, new TypeToken<Map<String,
                            Object>>() {
                    }.getType());
                    String json =
                            GSON.toJsonTree(jsonMap.get("asset")).getAsJsonObject().toString();
                    AssetRecord asset = GSON.fromJson(json, AssetRecord.class);
                    callback.onSuccess(asset);
                } catch (Exception e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toAssetRecords(Callback1<List<AssetRecord>> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> jsonMap = GSON.fromJson(raw, new TypeToken<Map<String,
                            Object>>() {
                    }.getType());
                    String json =
                            GSON.toJsonTree(jsonMap.get("assets")).getAsJsonArray().toString();
                    List<AssetRecord> assets = GSON.fromJson(json,
                            new TypeToken<List<AssetRecord>>() {
                            }.getType());
                    callback.onSuccess(assets);
                } catch (Exception e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGetTransactionResponse(Callback1<GetTransactionResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    GetTransactionResponse response = GSON.fromJson(raw,
                            GetTransactionResponse.class);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGetTransactionsResponse(Callback1<GetTransactionsResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    GetTransactionsResponse response = GSON.fromJson(raw,
                            GetTransactionsResponse.class);
                    callback.onSuccess(response);
                } catch (Exception e) {
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
