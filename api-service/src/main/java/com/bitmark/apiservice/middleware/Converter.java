package com.bitmark.apiservice.middleware;

import com.bitmark.apiservice.response.*;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.error.UnexpectedException;
import com.bitmark.apiservice.utils.record.AssetRecord;
import com.bitmark.apiservice.utils.record.ShareGrantRecord;
import com.bitmark.apiservice.utils.record.ShareRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bitmark.apiservice.utils.HttpUtils.jsonToMap;

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
                    Map<String, Object> mapJson = jsonToMap(raw);
                    JsonArray jsonArray = GSON.toJsonTree(mapJson.get("bitmarks")).getAsJsonArray();
                    List<String> txIds = new ArrayList<>(jsonArray.size());
                    for (JsonElement jsonElement : jsonArray) {
                        txIds.add(jsonElement.getAsJsonObject().get("id").getAsString());
                    }
                    callback.onSuccess(txIds);
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toRegistrationResponse(
            Callback1<RegistrationResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    RegistrationResponse response = GSON.fromJson(raw, RegistrationResponse.class);
                    callback.onSuccess(response);
                } catch (Throwable e) {
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
                    Map<String, Object> json = jsonToMap(raw);
                    if (json.containsKey("status")) {
                        String status = json.get("status").toString();
                        if (status.equals("ok")) callback.onSuccess("");
                        else callback.onError(new UnexpectedException("response status is not ok"));
                    } else if (json.containsKey("txid")) {
                        callback.onSuccess(json.get("txid").toString());
                    } else if (json.containsKey("txId")) {
                        callback.onSuccess(json.get("txId").toString());
                    }
                } catch (Throwable e) {
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
                    Map<String, Object> json = jsonToMap(raw);
                    callback.onSuccess(json.get("status").toString());
                } catch (Throwable e) {
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
                    Map<String, Object> json = jsonToMap(raw);
                    callback.onSuccess(json.get("offer_id").toString());
                } catch (Throwable e) {
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
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }

            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGetBitmarksResponse(
            Callback1<GetBitmarksResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    callback.onSuccess(GSON.fromJson(raw, GetBitmarksResponse.class));
                } catch (Throwable e) {
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
                    Map<String, Object> jsonMap = jsonToMap(raw);
                    String json =
                            GSON.toJsonTree(jsonMap.get("asset")).getAsJsonObject().toString();
                    AssetRecord asset = GSON.fromJson(json, new TypeToken<AssetRecord>() {
                    }.getType());
                    callback.onSuccess(asset);
                } catch (Throwable e) {
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
                    Map<String, Object> jsonMap = jsonToMap(raw);
                    String json =
                            GSON.toJsonTree(jsonMap.get("assets")).getAsJsonArray().toString();
                    List<AssetRecord> assets = GSON.fromJson(json,
                                                             new TypeToken<List<AssetRecord>>() {
                                                             }.getType());
                    callback.onSuccess(assets);
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGetTransactionResponse(
            Callback1<GetTransactionResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    GetTransactionResponse response =
                            GSON.fromJson(raw, GetTransactionResponse.class);
                    callback.onSuccess(response);
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGetTransactionsResponse(
            Callback1<GetTransactionsResponse> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    GetTransactionsResponse response =
                            GSON.fromJson(raw, GetTransactionsResponse.class);
                    callback.onSuccess(response);
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toCreateShareResponse(
            Callback1<Pair<String, String>> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> mapRes = jsonToMap(raw);
                    callback.onSuccess(new Pair<>(mapRes.get("tx_id").toString(),
                                                  mapRes.get("share_id").toString()));
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGrantShareResponse(Callback1<String> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> mapRes = jsonToMap(raw);
                    callback.onSuccess(mapRes.get("offer_id").toString());
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toGetShareResponse(Callback1<ShareRecord> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> jsonMap = jsonToMap(raw);
                    String json =
                            GSON.toJsonTree(jsonMap.get("shares")).getAsJsonArray().toString();
                    List<ShareRecord> shares = GSON.fromJson(json,
                                                             new TypeToken<List<ShareRecord>>() {
                                                             }.getType());
                    callback.onSuccess(!shares.isEmpty() ? shares.get(0) : null);
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toListSharesResponse(Callback1<List<ShareRecord>> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> jsonMap = jsonToMap(raw);
                    String json =
                            GSON.toJsonTree(jsonMap.get("shares")).getAsJsonArray().toString();
                    List<ShareRecord> shares = GSON.fromJson(json,
                                                             new TypeToken<List<ShareRecord>>() {
                                                             }.getType());
                    callback.onSuccess(shares);
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toListShareOffersResponse(
            Callback1<List<ShareGrantRecord>> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> jsonMap = jsonToMap(raw);
                    String json =
                            GSON.toJsonTree(jsonMap.get("offers")).getAsJsonArray().toString();
                    List<ShareGrantRecord> shareGrantRecords = GSON.fromJson(json,
                                                                             new TypeToken<List<ShareGrantRecord>>() {
                                                                             }.getType());
                    callback.onSuccess(shareGrantRecords);
                } catch (Throwable e) {
                    callback.onError(new UnexpectedException(e));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        };
    }

    public static Callback1<Response> toWsToken(Callback1<String> callback) {
        return new Callback1<Response>() {
            @Override
            public void onSuccess(Response res) {
                try {
                    String raw = res.body().string();
                    Map<String, Object> jsonMap = jsonToMap(raw);
                    callback.onSuccess(jsonMap.get("token").toString());
                } catch (Throwable e) {
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
