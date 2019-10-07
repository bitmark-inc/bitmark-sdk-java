package com.bitmark.apiservice.test.unittest.middleware;

import com.bitmark.apiservice.middleware.Converter;
import com.bitmark.apiservice.response.*;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callback1;
import com.bitmark.apiservice.utils.record.*;
import com.google.gson.internal.LinkedTreeMap;
import okhttp3.*;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static com.bitmark.apiservice.test.utils.FileUtils.loadResponse;
import static com.bitmark.apiservice.test.utils.TestUtils.reflectionSet;
import static com.bitmark.apiservice.utils.record.Head.HEAD;
import static com.bitmark.apiservice.utils.record.TransactionRecord.Status.CONFIRMED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class ConverterTest extends BaseTest {

    private static final MediaType JSON = MediaType.parse(
            "application/json; charset=utf-8");

    @ParameterizedTest
    @MethodSource("createSuccessResponseListTxId")
    public void testConvertIssueResponse_ValidResponse_CorrectIssueResponseIsReturn(
            Response response,
            List<String> expectedTxIds
    ) {
        Callback1<Response> callback = Converter.toIssueResponse(new Callback1<List<String>>() {
            @Override
            public void onSuccess(List<String> txIds) {
                assertEquals(expectedTxIds, txIds);
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable, throwable.getMessage());
            }
        });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createSuccessResponseRegistrationResponse")
    public void testConvertRegistrationResponse_ValidResponse_CorrectRegistrationResponseIsReturn(
            Response response,
            RegistrationResponse expectedResponse
    ) {
        Callback1<Response> callback =
                Converter.toRegistrationResponse(new Callback1<RegistrationResponse>() {
                    @Override
                    public void onSuccess(RegistrationResponse response) {
                        assertEquals(expectedResponse, response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createSuccessResponseTxId")
    public void testConvertTxId_ValidResponse_CorrectTxIdIsReturn(
            Response response,
            String expectedTxId
    ) {
        Callback1<Response> callback = Converter.toTxId(new Callback1<String>() {
            @Override
            public void onSuccess(String txId) {
                assertEquals(expectedTxId, txId);
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable, throwable.getMessage());
            }
        });
        callback.onSuccess(response);
    }

    @Test
    public void testConvertStatus_ValidResponse_CorrectStatusIsReturn()
            throws IOException {
        Response response = new Response.Builder().request(new Request.Builder()
                .url("http" +
                        "://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/transfer/transfer_response2.json")
                ))
                .message("dummy").build();
        Callback1<Response> callback = Converter.toStatus(new Callback1<String>() {
            @Override
            public void onSuccess(String status) {
                assertEquals("ok", status);
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable, throwable.getMessage());
            }
        });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createSuccessResponseOfferId")
    public void testConvertOfferId_ValidResponse_CorrectTxIdIsReturn(
            Response response,
            String expectedOfferId
    ) {
        Callback1<Response> callback = Converter.toOfferId(new Callback1<String>() {
            @Override
            public void onSuccess(String offerId) {
                assertEquals(expectedOfferId, offerId);
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable, throwable.getMessage());
            }
        });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createSuccessResponseGetBitmarkResponse")
    public void testConvertGetBitmarkResponse_ValidResponse_CorrectGetBitmarkResponseIsReturn(
            Response response, GetBitmarkResponse expectedResponse
    ) {
        Callback1<Response> callback =
                Converter.toGetBitmarkResponse(new Callback1<GetBitmarkResponse>() {
                    @Override
                    public void onSuccess(GetBitmarkResponse res) {
                        assertEquals(expectedResponse, res);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createSuccessResponseGetBitmarksResponse")
    public void testConvertGetBitmarksResponse_ValidResponse_CorrectGetBitmarkResponseIsReturn(
            Response response,
            GetBitmarksResponse expectedResponse
    ) {
        Callback1<Response> callback =
                Converter.toGetBitmarksResponse(new Callback1<GetBitmarksResponse>() {
                    @Override
                    public void onSuccess(GetBitmarksResponse res) {
                        assertEquals(expectedResponse, res);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseAssetRecord")
    public void testConvertAssetRecord_ValidResponse_CorrectAssetRecordIsReturn(
            Response response
            , AssetRecord expectedAsset
    ) {
        Callback1<Response> callback = Converter.toAssetRecord(new Callback1<AssetRecord>() {
            @Override
            public void onSuccess(AssetRecord asset) {
                assertEquals(expectedAsset, asset);
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable, throwable.getMessage());
            }
        });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseAssetRecords")
    public void testConvertAssetRecords_ValidResponse_CorrectAssetRecordIsReturn(
            Response response,
            List<AssetRecord> expectedAssets
    ) {
        Callback1<Response> callback = Converter.toAssetRecords(new Callback1<List<AssetRecord>>() {
            @Override
            public void onSuccess(List<AssetRecord> assets) {
                assertEquals(expectedAssets, assets);
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable, throwable.getMessage());
            }
        });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseGetTransactionResponse")
    public void testConvertGetTransactionResponse_ValidResponse_CorrectGetTransactionResponseIsReturn(
            Response response,
            GetTransactionResponse expectedResponse
    ) {
        Callback1<Response> callback =
                Converter.toGetTransactionResponse(new Callback1<GetTransactionResponse>() {
                    @Override
                    public void onSuccess(GetTransactionResponse res) {
                        assertEquals(expectedResponse, res);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseGetTransactionsResponse")
    public void testConvertGetTransactionsResponse_ValidResponse_CorrectGetTransactionsResponseIsReturn(
            Response response, GetTransactionsResponse expectedResponse
    ) {
        Callback1<Response> callback =
                Converter.toGetTransactionsResponse(new Callback1<GetTransactionsResponse>() {
                    @Override
                    public void onSuccess(GetTransactionsResponse res) {
                        assertEquals(expectedResponse, res);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseTxIdShareId")
    public void testConvertCreateShareResponse_ValidResponse_CorrectValueReturn(
            Response response,
            String txId,
            String shareId
    ) {
        Callback1<Response> callback = Converter.toCreateShareResponse(
                new Callback1<Pair<String, String>>() {
                    @Override
                    public void onSuccess(Pair<String, String> pair) {
                        assertEquals(txId, pair.first());
                        assertEquals(shareId, pair.second());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseOfferId")
    public void testConvertGrantShareResponse_ValidResponse_CorrectValueReturn(
            Response response,
            String expectedOfferId
    ) {
        Callback1<Response> callback = Converter.toGrantShareResponse(new Callback1<String>() {
            @Override
            public void onSuccess(String offerId) {
                assertEquals(expectedOfferId, offerId);
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable, throwable.getMessage());
            }
        });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseShareRecord")
    public void testConvertGetShareResponse_ValidResponse_CorrectValueReturn(
            Response response, ShareRecord expectedRecord
    ) {
        Callback1<Response> callback = Converter.toGetShareResponse(
                new Callback1<ShareRecord>() {
                    @Override
                    public void onSuccess(ShareRecord record) {
                        assertEquals(expectedRecord, record);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseListShareRecord")
    public void testConvertListShareResponse_ValidResponse_CorrectValueReturn(
            Response response, List<ShareRecord> expectedRecords
    ) {
        Callback1<Response> callback = Converter.toListSharesResponse(
                new Callback1<List<ShareRecord>>() {
                    @Override
                    public void onSuccess(List<ShareRecord> records) {
                        assertEquals(expectedRecords, records);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @ParameterizedTest
    @MethodSource("createValidResponseListGrantRecord")
    public void testConvertGetShareOfferResponse_ValidResponse_CorrectValueReturn(
            Response response,
            List<ShareGrantRecord> expectedRecords
    ) {
        Callback1<Response> callback = Converter.toListShareOffersResponse(
                new Callback1<List<ShareGrantRecord>>() {
                    @Override
                    public void onSuccess(List<ShareGrantRecord> records) {
                        assertEquals(expectedRecords, records);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        assertNull(throwable, throwable.getMessage());
                    }
                });
        callback.onSuccess(response);
    }

    @Test
    public void testConvertRegisterWsResponse__CorrectValueReturn()
            throws IOException {
        String expectedToken = "58582819-09f6-4948-a03d-fde9256e0354";
        Response response = new Response.Builder()
                .request(new Request.Builder().url("http://dummy.com").build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse("/ws/register.json")
                )).message("dummy")
                .build();
        Callback1<Response> callback = Converter.toWsToken(new Callback1<String>() {
            @Override
            public void onSuccess(String token) {
                assertEquals(expectedToken, token);
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable, throwable.getMessage());
            }
        });
        callback.onSuccess(response);
    }

    private static Stream<Arguments> createSuccessResponseListTxId()
            throws IOException {
        final List<String> txIds1 = new ArrayList<String>() {{
            add("e8f8867231590f19a4c353a3487b4931a462ae7b9e0cd5471618aa3e955f236f");
            add("995f2d5f3bcb6cab43d4a758efe39016d5012f07098eaf63179a60dedba85a25");
            add("d25e784a72d2c12a7813368167a6d3a2b47a38e836ab88bb10e6b2b71dfb8cb7");
            add("682e1d3762a6b2c103d2b98233db04aadfd66e39d6a5d4f32cd5762f7352d492");
            add("e489a1dc930f55ce74f3f39e2e4b52cb1e0a57e720cea2ba7c2dc0b471354e27");
            add("1246e2a223a9c4e7a63b12ad0b854a92d26a79cf53bd780783779e08f88131bd");
            add("8ca6cfb7e689e11bf32d6a44528678155f77278349b8d8c938050951a9c8b664");
            add("a845c1666f45ac7ac7a6413a1b36f846ef638201cceb85fcef7333df8aa20e53");
            add("eb799ecdc2e0911d1744899c830c75600469f316ad033312d64cb0c1739bf0a6");
            add("0107f4dbc256591c540722fd2f558e43dd7f197faa88ace608ab82438aa650c0");
        }};

        final List<String> txIds2 = new ArrayList<String>() {{
            add("e8f8867231590f19a4c353a3487b4931a462ae7b9e0cd5471618aa3e955f236f");
        }};
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/issue/multiple_issue.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/issue/single_issue.json")
                ))
                .message("dummy").build();
        return Stream.of(
                Arguments.of(response1, txIds1),
                Arguments.of(response2, txIds2)
        );
    }

    private static Stream<Arguments> createSuccessResponseRegistrationResponse()
            throws IOException {
        final RegistrationResponse registrationResponse1 =
                new RegistrationResponse(new ArrayList<RegistrationResponse.Asset>() {{
                    add(new RegistrationResponse.Asset(
                            "d20f8bc16350a4f53fb5b685d4e7cedf6e07ab9be9533effb2008ea8434a7685646e042fbdf8a9df46085c19648fb9ce99095c5fa16df25d56721d233646d38b",
                            false
                    ));
                }});

        final RegistrationResponse registrationResponse2 =
                new RegistrationResponse(new ArrayList<RegistrationResponse.Asset>() {{
                    add(new RegistrationResponse.Asset(
                            "d20f8bc16350a4f53fb5b685d4e7cedf6e07ab9be9533effb2008ea8434a7685646e042fbdf8a9df46085c19648fb9ce99095c5fa16df25d56721d233646d38b",
                            true
                    ));
                }});
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/registration/registration1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/registration/registration2.json")
                ))
                .message("dummy").build();
        return Stream.of(
                Arguments.of(response1, registrationResponse1),
                Arguments.of(
                        response2,
                        registrationResponse2
                )
        );
    }

    private static Stream<Arguments> createSuccessResponseTxId()
            throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/transfer/transfer1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/transfer/transfer2.json")
                ))
                .message("dummy").build();
        return Stream.of(
                Arguments.of(
                        response1,
                        "9696d29472c1a54d01444d6135746ceffef2d6430cfb4c363ca0cb136240bf39"
                ),
                Arguments.of(
                        response2,
                        "5f138ce10c0b62805c6cf28d41ef87b82e594b4bb5b285de0425b65a065e535e"
                )
        );
    }

    private static Stream<Arguments> createSuccessResponseOfferId()
            throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/transfer/transfer_offer1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/transfer/transfer_offer2.json")
                ))
                .message("dummy").build();
        return Stream.of(
                Arguments.of(
                        response1,
                        "58582819-09f6-4948-a03d-fde9256e0354"
                ),
                Arguments.of(
                        response2,
                        "e91fd018-2bdf-4a99-9f9e-7e88261a7a24"
                )
        );
    }

    private static Stream<Arguments> createSuccessResponseGetBitmarkResponse()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/bitmark/bitmark1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/bitmark/bitmark2.json")
                ))
                .message("dummy").build();

        final BitmarkRecord bitmark1 = new BitmarkRecord();
        reflectionSet(bitmark1, new Pair<>(
                        "assetId",
                        "7ffe70ce4383e81b26f7a5fd8c6532c8a50525696272677120f10f44f16e377edd75e1507acd6572978697bcada43d30fb496caef86a3c77fc1808b8de2cbda3"
                ),
                new Pair<>("blockNumber", 9022),
                new Pair<>("confirmedAt", "2018-09-16T03:23:32.000000Z"),
                new Pair<>("createdAt", "2018-09-16T03:23:32.000000Z"),
                new Pair<>("head", HEAD), new Pair<>(
                        "headId",
                        "ad0c27e5200160ac107e471754643bd60511d5dd44e7862018cd7ec4d9688fac"
                ),
                new Pair<>(
                        "id",
                        "ad0c27e5200160ac107e471754643bd60511d5dd44e7862018cd7ec4d9688fac"
                ),
                new Pair<>("issuedAt", "2018-09-16T03:23:32.000000Z"),
                new Pair<>(
                        "issuer",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("offset", 744377),
                new Pair<>(
                        "owner",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("status", BitmarkRecord.Status.SETTLED)
        );

        final BitmarkRecord bitmark2 = new BitmarkRecord();
        reflectionSet(bitmark2, new Pair<>(
                        "assetId",
                        "7ffe70ce4383e81b26f7a5fd8c6532c8a50525696272677120f10f44f16e377edd75e1507acd6572978697bcada43d30fb496caef86a3c77fc1808b8de2cbda3"
                ),
                new Pair<>("blockNumber", 9022),
                new Pair<>("confirmedAt", "2018-09-16T03:23:32.000000Z"),
                new Pair<>("createdAt", "2018-09-16T03:23:32.000000Z"),
                new Pair<>("head", HEAD), new Pair<>(
                        "headId",
                        "ad0c27e5200160ac107e471754643bd60511d5dd44e7862018cd7ec4d9688fac"
                ),
                new Pair<>(
                        "id",
                        "ad0c27e5200160ac107e471754643bd60511d5dd44e7862018cd7ec4d9688fac"
                ),
                new Pair<>("issuedAt", "2018-09-16T03:23:32.000000Z"),
                new Pair<>(
                        "issuer",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("offset", 744377),
                new Pair<>(
                        "owner",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("status", BitmarkRecord.Status.SETTLED)
        );

        final AssetRecord asset1 = new AssetRecord();
        reflectionSet(
                asset1,
                new Pair<>("blockNumber", 9022),
                new Pair<>("blockOffset", 1),
                new Pair<>("createdAt", "2018-09-16T03:23:32.000000Z"),
                new Pair<>(
                        "fingerprint",
                        "013727524647e0d9c4132af6fe6a57ca77e815280dde494d75b140050e322554ad92e27d8ebdc0e39ff4a3fa6d105419c6455c66256faecb816e7b1e1b2804beea"
                ),
                new Pair<>(
                        "id",
                        "7ffe70ce4383e81b26f7a5fd8c6532c8a50525696272677120f10f44f16e377edd75e1507acd6572978697bcada43d30fb496caef86a3c77fc1808b8de2cbda3"
                ),
                new Pair<>("metadata", new HashMap<String, String>() {{
                    put(
                            "name",
                            "JavaSDK_Test_1537068149724.txt"
                    );
                    put(
                            "description",
                            "Temporary File create from java sdk test"
                    );
                }}),
                new Pair<>("name", "JavaSDK_Test_1537068149724.txt"),
                new Pair<>("offset", 9918),
                new Pair<>(
                        "registrant",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("status", AssetRecord.Status.CONFIRMED)
        );
        final GetBitmarkResponse expectedResponse1 = new GetBitmarkResponse(
                bitmark1,
                asset1
        );
        final GetBitmarkResponse expectedResponse2 = new GetBitmarkResponse(
                bitmark2,
                null
        );
        return Stream.of(
                Arguments.of(response1, expectedResponse1),
                Arguments.of(
                        response2,
                        expectedResponse2
                )
        );
    }

    private static Stream<Arguments> createSuccessResponseGetBitmarksResponse()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/bitmark/bitmarks1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/bitmark/bitmarks2.json")
                ))
                .message("dummy").build();

        final BitmarkRecord bitmark1 = new BitmarkRecord();
        reflectionSet(bitmark1, new Pair<>(
                        "assetId",
                        "dbca8f9d3f6d7a55f5ffa6f22abbccceafe599c2b0af99ccae8dbe8620c02c4facfc5748863d4236005d2fb66b1a078c694d8038fb5aaae0e08486c2c9512710"
                ),
                new Pair<>("blockNumber", 9028),
                new Pair<>("confirmedAt", "2018-09-16T05:47:24.000000Z"),
                new Pair<>("createdAt", "2018-09-16T05:47:24.000000Z"),
                new Pair<>("head", HEAD), new Pair<>(
                        "headId",
                        "12d11a294088bc15aa05965098dd965afb21c87d4df317bd8acb2a7b5127ecd2"
                ),
                new Pair<>(
                        "id",
                        "12d11a294088bc15aa05965098dd965afb21c87d4df317bd8acb2a7b5127ecd2"
                ),
                new Pair<>("issuedAt", "2018-09-16T05:47:24.000000Z"),
                new Pair<>(
                        "issuer",
                        "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p"
                ),
                new Pair<>("offset", 744455),
                new Pair<>(
                        "owner",
                        "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p"
                ),
                new Pair<>("status", BitmarkRecord.Status.SETTLED)
        );

        final BitmarkRecord bitmark2 = new BitmarkRecord();
        reflectionSet(bitmark2, new Pair<>(
                        "assetId",
                        "9ef1590645df106ce428ec9cfdb48cfae29e95ee543a42bc3dcf95969ff9d8285dd07a558abf540838c151a7b34dd8c443430c766e8b1c934bdffa23cc06aa04"
                ),
                new Pair<>("blockNumber", 9027),
                new Pair<>("confirmedAt", "2018-09-16T04:08:04.000000Z"),
                new Pair<>("createdAt", "2018-09-16T04:08:04.000000Z"),
                new Pair<>("head", HEAD), new Pair<>(
                        "headId",
                        "402e895c66b9d8e3920a01489b21e33c265bf191ccdd33f040d50168496435d3"
                ),
                new Pair<>(
                        "id",
                        "402e895c66b9d8e3920a01489b21e33c265bf191ccdd33f040d50168496435d3"
                ),
                new Pair<>("issuedAt", "2018-09-16T04:08:04.000000Z"),
                new Pair<>(
                        "issuer",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("offset", 744439),
                new Pair<>(
                        "owner",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("status", BitmarkRecord.Status.SETTLED)
        );

        final AssetRecord asset1 = new AssetRecord();
        reflectionSet(
                asset1,
                new Pair<>("blockNumber", 9028),
                new Pair<>("blockOffset", 1),
                new Pair<>("createdAt", "2018-09-16T05:47:24.000000Z"),
                new Pair<>(
                        "fingerprint",
                        "01ea9198b08291a94b95db2c959c173db5c7cc48149c4cc455476e5ec925f630b0478c40b207eeb4b99825d412bfe19799a36c983c69bfdb715b6680ce1a562acd"
                ),
                new Pair<>(
                        "id",
                        "dbca8f9d3f6d7a55f5ffa6f22abbccceafe599c2b0af99ccae8dbe8620c02c4facfc5748863d4236005d2fb66b1a078c694d8038fb5aaae0e08486c2c9512710"
                ),
                new Pair<>("metadata", new HashMap<String, String>() {{
                    put(
                            "Saved Time",
                            "2018-09-16T05:46:22.686Z"
                    );
                    put("Source", "HealthKit");
                }}),
                new Pair<>("name", "HK61158509"),
                new Pair<>("offset", 9944),
                new Pair<>(
                        "registrant",
                        "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p"
                ),
                new Pair<>("status", AssetRecord.Status.CONFIRMED)
        );

        final GetBitmarksResponse expectedResponse1 =
                new GetBitmarksResponse(new ArrayList<BitmarkRecord>() {{
                    add(bitmark1);
                }}, new ArrayList<AssetRecord>() {{
                    add(asset1);
                }});
        final GetBitmarksResponse expectedResponse2 =
                new GetBitmarksResponse(new ArrayList<BitmarkRecord>() {{
                    add(bitmark2);
                }}, null);
        return Stream.of(
                Arguments.of(response1, expectedResponse1),
                Arguments.of(
                        response2,
                        expectedResponse2
                )
        );
    }

    private static Stream<Arguments> createValidResponseAssetRecord()
            throws IOException, NoSuchFieldException, IllegalAccessException {

        final AssetRecord asset1 = new AssetRecord();
        reflectionSet(
                asset1,
                new Pair<>("blockNumber", 9028),
                new Pair<>("blockOffset", 1),
                new Pair<>("createdAt", "2018-09-16T05:47:24.000000Z"),
                new Pair<>(
                        "fingerprint",
                        "01ea9198b08291a94b95db2c959c173db5c7cc48149c4cc455476e5ec925f630b0478c40b207eeb4b99825d412bfe19799a36c983c69bfdb715b6680ce1a562acd"
                ),
                new Pair<>(
                        "id",
                        "dbca8f9d3f6d7a55f5ffa6f22abbccceafe599c2b0af99ccae8dbe8620c02c4facfc5748863d4236005d2fb66b1a078c694d8038fb5aaae0e08486c2c9512710"
                ),
                new Pair<>("metadata", new HashMap<String, String>() {{
                    put(
                            "Saved Time",
                            "2018-09-16T05:46:22.686Z"
                    );
                    put("Source", "HealthKit");
                }}),
                new Pair<>("name", "HK61158509"),
                new Pair<>("offset", 9944),
                new Pair<>(
                        "registrant",
                        "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p"
                ),
                new Pair<>("status", AssetRecord.Status.CONFIRMED)
        );

        final AssetRecord asset2 = new AssetRecord();
        reflectionSet(
                asset2,
                new Pair<>("blockNumber", 9027),
                new Pair<>("blockOffset", 4),
                new Pair<>("createdAt", "2018-09-16T04:08:04.000000Z"),
                new Pair<>(
                        "fingerprint",
                        "011b1860a4cf9c0142248773c84f24f8db6e2b54b6c1803700fce2818dcd001f7a6b0134fe3cb7d7bf0310c749fd0bf977b9f5060c0c34c1d7a755c6f159ab5b1b"
                ),
                new Pair<>(
                        "id",
                        "9ef1590645df106ce428ec9cfdb48cfae29e95ee543a42bc3dcf95969ff9d8285dd07a558abf540838c151a7b34dd8c443430c766e8b1c934bdffa23cc06aa04"
                ),
                new Pair<>("metadata", new HashMap<String, String>() {{
                    put(
                            "name",
                            "JavaSDK_Test_1537070880489.txt"
                    );
                    put(
                            "description",
                            "Temporary File create from java sdk test"
                    );
                }}),
                new Pair<>("name", "JavaSDK_Test_1537070880489.txt"),
                new Pair<>("offset", 9942),
                new Pair<>(
                        "registrant",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("status", AssetRecord.Status.CONFIRMED)
        );

        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/asset/asset1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/asset/asset2.json")
                ))
                .message("dummy").build();
        return Stream.of(
                Arguments.of(response1, asset1),
                Arguments.of(response2, asset2)
        );
    }

    private static Stream<Arguments> createValidResponseAssetRecords()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final AssetRecord asset1 = new AssetRecord();
        reflectionSet(
                asset1,
                new Pair<>("blockNumber", 9028),
                new Pair<>("blockOffset", 1),
                new Pair<>("createdAt", "2018-09-16T05:47:24.000000Z"),
                new Pair<>(
                        "fingerprint",
                        "01ea9198b08291a94b95db2c959c173db5c7cc48149c4cc455476e5ec925f630b0478c40b207eeb4b99825d412bfe19799a36c983c69bfdb715b6680ce1a562acd"
                ),
                new Pair<>(
                        "id",
                        "dbca8f9d3f6d7a55f5ffa6f22abbccceafe599c2b0af99ccae8dbe8620c02c4facfc5748863d4236005d2fb66b1a078c694d8038fb5aaae0e08486c2c9512710"
                ),
                new Pair<>("metadata", new HashMap<String, String>() {{
                    put(
                            "Saved Time",
                            "2018-09-16T05:46:22.686Z"
                    );
                    put("Source", "HealthKit");
                }}),
                new Pair<>("name", "HK61158509"),
                new Pair<>("offset", 9944),
                new Pair<>(
                        "registrant",
                        "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p"
                ),
                new Pair<>("status", AssetRecord.Status.CONFIRMED)
        );

        final AssetRecord asset2 = new AssetRecord();
        reflectionSet(
                asset2,
                new Pair<>("blockNumber", 9027),
                new Pair<>("blockOffset", 4),
                new Pair<>("createdAt", "2018-09-16T04:08:04.000000Z"),
                new Pair<>(
                        "fingerprint",
                        "011b1860a4cf9c0142248773c84f24f8db6e2b54b6c1803700fce2818dcd001f7a6b0134fe3cb7d7bf0310c749fd0bf977b9f5060c0c34c1d7a755c6f159ab5b1b"
                ),
                new Pair<>(
                        "id",
                        "9ef1590645df106ce428ec9cfdb48cfae29e95ee543a42bc3dcf95969ff9d8285dd07a558abf540838c151a7b34dd8c443430c766e8b1c934bdffa23cc06aa04"
                ),
                new Pair<>("metadata", new HashMap<String, String>() {{
                    put(
                            "name",
                            "JavaSDK_Test_1537070880489.txt"
                    );
                    put(
                            "description",
                            "Temporary File create from java sdk test"
                    );
                }}),
                new Pair<>("name", "JavaSDK_Test_1537070880489.txt"),
                new Pair<>("offset", 9942),
                new Pair<>(
                        "registrant",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("status", AssetRecord.Status.CONFIRMED)
        );
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/asset/assets1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/asset/assets2.json")
                ))
                .message("dummy").build();
        return Stream.of(Arguments.of(response1, new ArrayList<AssetRecord>() {{
            add(asset1);
            add(asset2);
        }}), Arguments.of(response2, new ArrayList<AssetRecord>() {{
            add(asset1);
        }}));
    }

    private static Stream<Arguments> createValidResponseGetTransactionResponse()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/transaction/transaction1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/transaction/transaction2.json")
                ))
                .message("dummy").build();
        final TransactionRecord txRecord1 = new TransactionRecord();
        reflectionSet(txRecord1, new Pair<>(
                        "id",
                        "fd645350320c9c0b1fbcab2b9678cb50719bff6d0f0d235d9565afdaa470ef4a"
                ),
                new Pair<>(
                        "owner",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>(
                        "assetId",
                        "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607"
                ),
                new Pair<>("head", HEAD), new Pair<>("status", CONFIRMED),
                new Pair<>("blockNumber", 9093), new Pair<>("blockOffset", 8),
                new Pair<>("offset", 744968), new Pair<>("payId", ""),
                new Pair<>(
                        "bitmarkId",
                        "fd645350320c9c0b1fbcab2b9678cb50719bff6d0f0d235d9565afdaa470ef4a"
                ),
                new Pair<>("isCounterSignature", false)
        );

        final TransactionRecord txRecord2 = new TransactionRecord();
        reflectionSet(txRecord2, new Pair<>(
                        "id",
                        "441d7a04b3d75c3f0f55564331f4bfadecf0a5acc8c0c6a1f5cb9a0db6dca58e"
                ),
                new Pair<>(
                        "owner",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>(
                        "assetId",
                        "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607"
                ),
                new Pair<>("head", HEAD), new Pair<>("status", CONFIRMED),
                new Pair<>("blockNumber", 9093), new Pair<>("blockOffset", 7),
                new Pair<>("offset", 744967), new Pair<>("payId", ""),
                new Pair<>(
                        "bitmarkId",
                        "441d7a04b3d75c3f0f55564331f4bfadecf0a5acc8c0c6a1f5cb9a0db6dca58e"
                ),
                new Pair<>("isCounterSignature", true)
        );

        final AssetRecord assetRecord = new AssetRecord();
        reflectionSet(
                assetRecord,
                new Pair<>("blockNumber", 9093),
                new Pair<>("blockOffset", 1),
                new Pair<>("createdAt", "2018-09-18T08:09:20.000000Z"),
                new Pair<>(
                        "fingerprint",
                        "01beef7f85a4bae3bc8e46930a38f7f96812b67d43d2e2796e8cfa3cecea6d32d699f48d96366c51fa2aef7a0e0e9271c189dea51e3bc250dc1e4a53763baf80b3"
                ),
                new Pair<>(
                        "id",
                        "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607"
                ),
                new Pair<>("metadata", new HashMap<String, String>() {{
                    put(
                            "name",
                            "JavaSDK_Test_1537258135324.txt"
                    );
                    put(
                            "description",
                            "Temporary File create from java sdk test"
                    );
                }}),
                new Pair<>("name", "JavaSDK_Test_1537258135324.txt"),
                new Pair<>("offset", 10086),
                new Pair<>(
                        "registrant",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("status", AssetRecord.Status.CONFIRMED)
        );

        final GetTransactionResponse getTransactionResponse1 =
                new GetTransactionResponse(txRecord1, null);
        final GetTransactionResponse getTransactionResponse2 =
                new GetTransactionResponse(txRecord2, assetRecord);
        return Stream.of(
                Arguments.of(response1, getTransactionResponse1),
                Arguments.of(response2
                        , getTransactionResponse2)
        );
    }

    private static Stream<Arguments> createValidResponseGetTransactionsResponse()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/transaction/transactions1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/transaction/transactions2.json")
                ))
                .message("dummy").build();

        final TransactionRecord txRecord1 = new TransactionRecord();
        reflectionSet(txRecord1, new Pair<>(
                        "id",
                        "fd645350320c9c0b1fbcab2b9678cb50719bff6d0f0d235d9565afdaa470ef4a"
                ),
                new Pair<>(
                        "owner",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>(
                        "assetId",
                        "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607"
                ),
                new Pair<>("head", HEAD), new Pair<>("status", CONFIRMED),
                new Pair<>("blockNumber", 9093), new Pair<>("blockOffset", 8),
                new Pair<>("offset", 744968), new Pair<>("payId", ""),
                new Pair<>(
                        "bitmarkId",
                        "fd645350320c9c0b1fbcab2b9678cb50719bff6d0f0d235d9565afdaa470ef4a"
                ),
                new Pair<>("isCounterSignature", false)
        );

        final TransactionRecord txRecord2 = new TransactionRecord();
        reflectionSet(txRecord2, new Pair<>(
                        "id",
                        "441d7a04b3d75c3f0f55564331f4bfadecf0a5acc8c0c6a1f5cb9a0db6dca58e"
                ),
                new Pair<>(
                        "owner",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>(
                        "assetId",
                        "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607"
                ),
                new Pair<>("head", HEAD), new Pair<>("status", CONFIRMED),
                new Pair<>("blockNumber", 9093), new Pair<>("blockOffset", 7),
                new Pair<>("offset", 744967), new Pair<>("payId", ""),
                new Pair<>(
                        "bitmarkId",
                        "441d7a04b3d75c3f0f55564331f4bfadecf0a5acc8c0c6a1f5cb9a0db6dca58e"
                ),
                new Pair<>("isCounterSignature", true)
        );

        final AssetRecord assetRecord = new AssetRecord();
        reflectionSet(
                assetRecord,
                new Pair<>("blockNumber", 9093),
                new Pair<>("blockOffset", 1),
                new Pair<>("createdAt", "2018-09-18T08:09:20.000000Z"),
                new Pair<>(
                        "fingerprint",
                        "01beef7f85a4bae3bc8e46930a38f7f96812b67d43d2e2796e8cfa3cecea6d32d699f48d96366c51fa2aef7a0e0e9271c189dea51e3bc250dc1e4a53763baf80b3"
                ),
                new Pair<>(
                        "id",
                        "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607"
                ),
                new Pair<>("metadata", new HashMap<String, String>() {{
                    put(
                            "name",
                            "JavaSDK_Test_1537258135324.txt"
                    );
                    put(
                            "description",
                            "Temporary File create from java sdk test"
                    );
                }}),
                new Pair<>("name", "JavaSDK_Test_1537258135324.txt"),
                new Pair<>("offset", 10086),
                new Pair<>(
                        "registrant",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva"
                ),
                new Pair<>("status", AssetRecord.Status.CONFIRMED)
        );
        final GetTransactionsResponse getTransactionsResponse1 =
                new GetTransactionsResponse(new ArrayList<TransactionRecord>() {{
                    add(txRecord1);
                    add(txRecord2);
                }}, null);
        final GetTransactionsResponse getTransactionsResponse2 =
                new GetTransactionsResponse(new ArrayList<TransactionRecord>() {{
                    add(txRecord1);
                }}, new ArrayList<AssetRecord>() {{
                    add(assetRecord);
                }});
        return Stream.of(
                Arguments.of(response1, getTransactionsResponse1),
                Arguments.of(response2
                        , getTransactionsResponse2)
        );
    }

    private static Stream<Arguments> createValidResponseTxIdShareId()
            throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/share/create_share1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/share/create_share2.json")
                ))
                .message("dummy").build();
        return Stream.of(
                Arguments.of(
                        response1,
                        "828feb6aae57b07f8852f9d9836ab2420f14353120abd3d8980a798c02ba477c",
                        "d97cb558f58fcaa70d1a654e45c21545096077f2de4adfeb0bf8f239b97115ad"
                ),
                Arguments.of(
                        response2,
                        "88b2c2fa588fb4857306a65151df8a28d15e712c6c3aff8a1dfb0fa65e2bc736",
                        "6e94d6ec293aa96350229bd419115eaceb54cefb5bab745326c0aadb8bb9a22a"
                )
        );
    }

    private static Stream<Arguments> createValidResponseOfferId()
            throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/share/grant_share1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/share/grant_share2.json")
                ))
                .message("dummy").build();
        return Stream.of(
                Arguments.of(
                        response1,
                        "c0731450-74f0-49cc-90f8-84e84a2eaeab"
                ),
                Arguments.of(
                        response2,
                        "d7c68e86-e585-405f-bd9b-20d6e95fa81c"
                )
        );
    }

    private static Stream<Arguments> createValidResponseShareRecord()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/share/share_record1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/share/share_record2.json")
                ))
                .message("dummy").build();
        final ShareRecord shareRecord1 = new ShareRecord();
        reflectionSet(shareRecord1, new Pair<>(
                        "id",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "owner",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("balance", 2),
                new Pair<>("available", 2)
        );

        final ShareRecord shareRecord2 = new ShareRecord();
        reflectionSet(shareRecord2, new Pair<>(
                        "id",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "owner",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>("balance", 98),
                new Pair<>("available", 98)
        );

        return Stream.of(
                Arguments.of(response1, shareRecord1),
                Arguments.of(response2, shareRecord2)
        );
    }

    private static Stream<Arguments> createValidResponseListShareRecord()
            throws IOException, NoSuchFieldException, IllegalAccessException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/share/share_record1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/share/share_record2.json")
                ))
                .message("dummy").build();
        final ShareRecord shareRecord1 = new ShareRecord();
        reflectionSet(shareRecord1, new Pair<>(
                        "id",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "owner",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("balance", 2),
                new Pair<>("available", 2)
        );

        final ShareRecord shareRecord2 = new ShareRecord();
        reflectionSet(shareRecord2, new Pair<>(
                        "id",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "owner",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>("balance", 98),
                new Pair<>("available", 98)
        );

        return Stream.of(
                Arguments.of(response1, new ArrayList<ShareRecord>() {{
                    add(shareRecord1);
                    add(shareRecord2);
                }}),
                Arguments.of(response2, new ArrayList<ShareRecord>() {{
                    add(shareRecord2);
                    add(shareRecord1);
                }})
        );
    }

    private static Stream<Arguments> createValidResponseListGrantRecord()
            throws NoSuchFieldException, IllegalAccessException, IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/share/list_grant_share1.json")
                ))
                .message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder()
                .url("http://dummy.com")
                .build())
                .protocol(Protocol.HTTP_1_1).code(200)
                .body(ResponseBody.create(
                        JSON,
                        loadResponse(
                                "/query/share/list_grant_share2.json")
                ))
                .message("dummy").build();
        final ShareGrantRecord grantRecord1 = new ShareGrantRecord();
        final ShareGrantRecord.Record record1 = new ShareGrantRecord.Record();
        reflectionSet(record1, new Pair<>(
                        "shareId",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>("quantity", 1),
                new Pair<>(
                        "owner",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>(
                        "receiver",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("beforeBlock", 150212348), new Pair<>(
                        "signature",
                        "63d0492088db46296f8fc220234be211a678dd6de576350250f79c716d9a5c920b4bbd3f1ce1c112365fa496f41e63fddd4899abfa9c1bf94a572e052ba50a0f"
                )
        );

        reflectionSet(
                grantRecord1,
                new Pair<>(
                        "id",
                        "73781d92-6352-4b79-a404-cb8e88f96a85"
                ),
                new Pair<>(
                        "shareId",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "from",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>(
                        "to",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("status", "open"),
                new Pair<>("txId", ""),
                new Pair<>("record", record1),
                new Pair<>("extraInfo", new LinkedTreeMap<>()),
                new Pair<>("createdAt", "2019-03-11T15:35:23.456511Z")
        );

        final ShareGrantRecord grantRecord2 = new ShareGrantRecord();
        final ShareGrantRecord.Record record2 = new ShareGrantRecord.Record();
        reflectionSet(record2, new Pair<>(
                        "shareId",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>("quantity", 1),
                new Pair<>(
                        "owner",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>(
                        "receiver",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("beforeBlock", 989824143), new Pair<>(
                        "signature",
                        "1efe017096bf5d159e4fb734f39f5684534e21a355623ddcf6a2f2de27f48698dc755af1c9715f544178c14cbbb360343fbd69b4f86b3c3e0a8c890b7fcb170e"
                )
        );

        reflectionSet(
                grantRecord2,
                new Pair<>(
                        "id",
                        "59e079b6-0534-4bc5-ae14-7ce965724562"
                ),
                new Pair<>(
                        "shareId",
                        "4ff47c5ca692699c3049eccad23ea98892d7a884c088d730f3ec42a966b85c41"
                ),
                new Pair<>(
                        "from",
                        "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs"
                ),
                new Pair<>(
                        "to",
                        "f7nuKToBByL3jEcArZWoB9PJ8MVmGPjrYkW88v3Yw8p7G5Sxhy"
                ),
                new Pair<>("status", "open"),
                new Pair<>("txId", ""),
                new Pair<>("record", record2),
                new Pair<>("extraInfo", new LinkedTreeMap<>()),
                new Pair<>("createdAt", "2019-03-11T15:35:23.011111Z")
        );

        return Stream.of(
                Arguments.of(response1, new ArrayList<ShareGrantRecord>() {{
                    add(grantRecord1);
                }}),
                Arguments.of(response2, new ArrayList<ShareGrantRecord>() {{
                    add(grantRecord1);
                    add(grantRecord2);
                }})
        );
    }
}