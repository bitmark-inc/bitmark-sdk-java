package com.bitmark.apiservice.middleware;

import com.bitmark.apiservice.response.GetTransactionsResponse;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.error.UnexpectedException;
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

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toIssueResponse(com.bitmark.apiservice.utils.callback.Callback1<List<String>> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
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
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toRegistrationResponse(com.bitmark.apiservice.utils.callback.Callback1<com.bitmark.apiservice.response.RegistrationResponse> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    com.bitmark.apiservice.response.RegistrationResponse response = GSON.fromJson(raw, com.bitmark.apiservice.response.RegistrationResponse.class);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toTxId(com.bitmark.apiservice.utils.callback.Callback1<String> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    Map<String, String> json = GSON.fromJson(raw, new TypeToken<Map<String,
                            String>>() {
                    }.getType());
                    callback.onSuccess(json.get("txid"));
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toStatus(com.bitmark.apiservice.utils.callback.Callback1<String> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    Map<String, String> json = GSON.fromJson(raw, new TypeToken<Map<String,
                            String>>() {
                    }.getType());
                    callback.onSuccess(json.get("status"));
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toOfferId(com.bitmark.apiservice.utils.callback.Callback1<String> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response response) {
                try {
                    String raw = response.body().string();
                    Map<String, String> json = GSON.fromJson(raw, new TypeToken<Map<String,
                            String>>() {
                    }.getType());
                    callback.onSuccess(json.get("offer_id"));
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toGetBitmarkResponse(com.bitmark.apiservice.utils.callback.Callback1<com.bitmark.apiservice.response.GetBitmarkResponse> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    callback.onSuccess(GSON.fromJson(raw, com.bitmark.apiservice.response.GetBitmarkResponse.class));
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toGetBitmarksResponse(com.bitmark.apiservice.utils.callback.Callback1<com.bitmark.apiservice.response.GetBitmarksResponse> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    callback.onSuccess(GSON.fromJson(raw, com.bitmark.apiservice.response.GetBitmarksResponse.class));
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toAssetRecord(com.bitmark.apiservice.utils.callback.Callback1<AssetRecord> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> jsonMap = GSON.fromJson(raw, new TypeToken<Map<String,
                            Object>>() {
                    }.getType());
                    String json =
                            GSON.toJsonTree(jsonMap.get("asset")).getAsJsonObject().toString();
                    com.bitmark.apiservice.utils.record.AssetRecord asset = GSON.fromJson(json, com.bitmark.apiservice.utils.record.AssetRecord.class);
                    callback.onSuccess(asset);
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toAssetRecords(com.bitmark.apiservice.utils.callback.Callback1<List<AssetRecord>> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> jsonMap = GSON.fromJson(raw, new TypeToken<Map<String,
                            Object>>() {
                    }.getType());
                    String json =
                            GSON.toJsonTree(jsonMap.get("assets")).getAsJsonArray().toString();
                    List<com.bitmark.apiservice.utils.record.AssetRecord> assets = GSON.fromJson(json,
                            new TypeToken<List<com.bitmark.apiservice.utils.record.AssetRecord>>() {
                            }.getType());
                    callback.onSuccess(assets);
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toGetTransactionResponse(com.bitmark.apiservice.utils.callback.Callback1<com.bitmark.apiservice.response.GetTransactionResponse> callback) {
        return new com.bitmark.apiservice.utils.callback.Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    com.bitmark.apiservice.response.GetTransactionResponse response = GSON.fromJson(raw,
                            com.bitmark.apiservice.response.GetTransactionResponse.class);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onError(new com.bitmark.apiservice.utils.error.UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static com.bitmark.apiservice.utils.callback.Callback1<Response> toGetTransactionsResponse(com.bitmark.apiservice.utils.callback.Callback1<com.bitmark.apiservice.response.GetTransactionsResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    com.bitmark.apiservice.response.GetTransactionsResponse response = GSON.fromJson(raw,
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
