package apiservice.test.unittest.middleware;

import apiservice.middleware.Converter;
import apiservice.response.*;
import apiservice.utils.callback.Callback1;
import apiservice.utils.record.AssetRecord;
import apiservice.utils.record.BitmarkRecord;
import apiservice.utils.record.TransactionRecord;
import apiservice.test.BaseTest;
import okhttp3.*;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static apiservice.utils.record.Head.HEAD;
import static apiservice.utils.record.TransactionRecord.Status.CONFIRMED;
import static apiservice.test.utils.FileUtils.loadResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class ConverterTest extends BaseTest {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @DisplayName("Verify function Converter.toIssueResponse(Callback1<List<String>>) works well " +
                         "with happy condition")
    @ParameterizedTest
    @MethodSource("createSuccessResponseListTxId")
    public void testConvertIssueResponse_ValidResponse_CorrectIssueResponseIsReturn(Response response,
                                                                                    List<String> expectedTxIds) {
        Callback1<Response> callback = Converter.toIssueResponse(new Callback1<List<String>>() {
            @Override
            public void onSuccess(List<String> txIds) {
                assertEquals(expectedTxIds, txIds);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toRegistrationResponse" +
                         "(Callback1<RegistrationResponse>) works well with happy condition")
    @ParameterizedTest
    @MethodSource("createSuccessResponseRegistrationResponse")
    public void testConvertRegistrationResponse_ValidResponse_CorrectRegistrationResponseIsReturn(Response response,
                                                                                                  RegistrationResponse expectedResponse) {
        Callback1<Response> callback =
                Converter.toRegistrationResponse(new Callback1<RegistrationResponse>() {
                    @Override
                    public void onSuccess(RegistrationResponse response) {
                        assertEquals(expectedResponse, response);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toTxId(Callback1<String> works well)")
    @ParameterizedTest
    @MethodSource("createSuccessResponseTxId")
    public void testConvertTxId_ValidResponse_CorrectTxIdIsReturn(Response response,
                                                                  String expectedTxId) {
        Callback1<Response> callback = Converter.toTxId(new Callback1<String>() {
            @Override
            public void onSuccess(String txId) {
                assertEquals(expectedTxId, txId);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toStatus(Callback1<String> works well)")
    @Test
    public void testConvertStatus_ValidResponse_CorrectStatusIsReturn() throws IOException {
        Response response = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/transfer/transfer_response2.json"))).message("dummy").build();
        Callback1<Response> callback = Converter.toStatus(new Callback1<String>() {
            @Override
            public void onSuccess(String status) {
                assertEquals("ok", status);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toOfferId(Callback1<String> works well)")
    @ParameterizedTest
    @MethodSource("createSuccessResponseOfferId")
    public void testConvertOfferId_ValidResponse_CorrectTxIdIsReturn(Response response,
                                                                     String expectedOfferId) {
        Callback1<Response> callback = Converter.toOfferId(new Callback1<String>() {
            @Override
            public void onSuccess(String offerId) {
                assertEquals(expectedOfferId, offerId);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toGetBitmarkResponse(Callback1<GetBitmarkResponse> " +
                         "works well)")
    @ParameterizedTest
    @MethodSource("createSuccessResponseGetBitmarkResponse")
    public void testConvertGetBitmarkResponse_ValidResponse_CorrectGetBitmarkResponseIsReturn(Response response, GetBitmarkResponse expectedResponse) {
        Callback1<Response> callback =
                Converter.toGetBitmarkResponse(new Callback1<GetBitmarkResponse>() {
                    @Override
                    public void onSuccess(GetBitmarkResponse res) {
                        assertEquals(expectedResponse, res);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toGetBitmarksResponse(Callback1<GetBitmarksResponse> " +
                         "works well)")
    @ParameterizedTest
    @MethodSource("createSuccessResponseGetBitmarksResponse")
    public void testConvertGetBitmarksResponse_ValidResponse_CorrectGetBitmarkResponseIsReturn(Response response, GetBitmarksResponse expectedResponse) {
        Callback1<Response> callback =
                Converter.toGetBitmarksResponse(new Callback1<GetBitmarksResponse>() {
                    @Override
                    public void onSuccess(GetBitmarksResponse res) {
                        assertEquals(expectedResponse, res);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toAssetRecord(Callback1<AssetRecord>) works well")
    @ParameterizedTest
    @MethodSource("createValidResponseAssetRecord")
    public void testConvertAssetRecord_ValidResponse_CorrectAssetRecordIsReturn(Response response
            , AssetRecord expectedAsset) {
        Callback1<Response> callback = Converter.toAssetRecord(new Callback1<AssetRecord>() {
            @Override
            public void onSuccess(AssetRecord asset) {
                assertEquals(expectedAsset, asset);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toAssetRecords(Callback1<List<AssetRecord>>) works " +
                         "well")
    @ParameterizedTest
    @MethodSource("createValidResponseAssetRecords")
    public void testConvertAssetRecords_ValidResponse_CorrectAssetRecordIsReturn(Response response, List<AssetRecord> expectedAssets) {
        Callback1<Response> callback = Converter.toAssetRecords(new Callback1<List<AssetRecord>>() {
            @Override
            public void onSuccess(List<AssetRecord> assets) {
                assertEquals(expectedAssets, assets);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toGetTransactionResponse" +
                         "(Callback1<GetTransactionResponse>) works well")
    @ParameterizedTest
    @MethodSource("createValidResponseGetTransactionResponse")
    public void testConvertGetTransactionResponse_ValidResponse_CorrectGetTransactionResponseIsReturn(Response response, GetTransactionResponse expectedResponse) {
        Callback1<Response> callback =
                Converter.toGetTransactionResponse(new Callback1<GetTransactionResponse>() {
                    @Override
                    public void onSuccess(GetTransactionResponse res) {
                        assertEquals(expectedResponse, res);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
        callback.onSuccess(response);
    }

    @DisplayName("Verify function Converter.toGetTransactionsResponse" +
                         "(Callback1<GetTransactionsResponse>) works well")
    @ParameterizedTest
    @MethodSource("createValidResponseGetTransactionsResponse")
    public void testConvertGetTransactionsResponse_ValidResponse_CorrectGetTransactionsResponseIsReturn(Response response, GetTransactionsResponse expectedResponse) {
        Callback1<Response> callback =
                Converter.toGetTransactionsResponse(new Callback1<GetTransactionsResponse>() {
                    @Override
                    public void onSuccess(GetTransactionsResponse res) {
                        assertEquals(expectedResponse, res);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
        callback.onSuccess(response);
    }

    private static Stream<Arguments> createSuccessResponseListTxId() throws IOException {
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
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/issue/multiple_issue.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/issue/single_issue.json"))).message("dummy").build();
        return Stream.of(Arguments.of(response1, txIds1), Arguments.of(response2, txIds2));
    }

    private static Stream<Arguments> createSuccessResponseRegistrationResponse() throws IOException {
        final RegistrationResponse registrationResponse1 =
                new RegistrationResponse(new ArrayList<RegistrationResponse.Asset>() {{
                    add(new RegistrationResponse.Asset(
                            "d20f8bc16350a4f53fb5b685d4e7cedf6e07ab9be9533effb2008ea8434a7685646e042fbdf8a9df46085c19648fb9ce99095c5fa16df25d56721d233646d38b", false));
                }});

        final RegistrationResponse registrationResponse2 =
                new RegistrationResponse(new ArrayList<RegistrationResponse.Asset>() {{
                    add(new RegistrationResponse.Asset(
                            "d20f8bc16350a4f53fb5b685d4e7cedf6e07ab9be9533effb2008ea8434a7685646e042fbdf8a9df46085c19648fb9ce99095c5fa16df25d56721d233646d38b", true));
                }});
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/registration/registration1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/registration/registration2.json"))).message("dummy").build();
        return Stream.of(Arguments.of(response1, registrationResponse1), Arguments.of(response2,
                registrationResponse2));
    }

    private static Stream<Arguments> createSuccessResponseTxId() throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/transfer/transfer1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/transfer/transfer2.json"))).message("dummy").build();
        return Stream.of(Arguments.of(response1,
                "9696d29472c1a54d01444d6135746ceffef2d6430cfb4c363ca0cb136240bf39"),
                Arguments.of(response2,
                        "5f138ce10c0b62805c6cf28d41ef87b82e594b4bb5b285de0425b65a065e535e"));
    }

    private static Stream<Arguments> createSuccessResponseOfferId() throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/transfer/transfer_offer1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/transfer/transfer_offer2.json"))).message("dummy").build();
        return Stream.of(Arguments.of(response1,
                "58582819-09f6-4948-a03d-fde9256e0354"),
                Arguments.of(response2,
                        "e91fd018-2bdf-4a99-9f9e-7e88261a7a24"));
    }

    private static Stream<Arguments> createSuccessResponseGetBitmarkResponse() throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/bitmark/bitmark1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/bitmark/bitmark2.json"))).message("dummy").build();

        final BitmarkRecord bitmark1 = new BitmarkRecord(
                "7ffe70ce4383e81b26f7a5fd8c6532c8a50525696272677120f10f44f16e377edd75e1507acd6572978697bcada43d30fb496caef86a3c77fc1808b8de2cbda3",
                9022, "2018-09-16T03:23:32.000000Z", "2018-09-16T03:23:32.000000Z", HEAD,
                "ad0c27e5200160ac107e471754643bd60511d5dd44e7862018cd7ec4d9688fac",
                "ad0c27e5200160ac107e471754643bd60511d5dd44e7862018cd7ec4d9688fac", "2018-09" +
                "-16T03:23:32.000000Z", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                744377, "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                BitmarkRecord.Status.SETTLED, null);
        final BitmarkRecord bitmark2 = new BitmarkRecord(
                "7ffe70ce4383e81b26f7a5fd8c6532c8a50525696272677120f10f44f16e377edd75e1507acd6572978697bcada43d30fb496caef86a3c77fc1808b8de2cbda3",
                9022, "2018-09-16T03:23:32.000000Z", "2018-09-16T03:23:32.000000Z", HEAD,
                "ad0c27e5200160ac107e471754643bd60511d5dd44e7862018cd7ec4d9688fac",
                "ad0c27e5200160ac107e471754643bd60511d5dd44e7862018cd7ec4d9688fac", "2018-09" +
                "-16T03:23:32.000000Z", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                744377, "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                BitmarkRecord.Status.SETTLED, null);
        final AssetRecord asset1 = new AssetRecord(9022, 1, "2018-09-16T03:23:32.000000Z", null,
                "013727524647e0d9c4132af6fe6a57ca77e815280dde494d75b140050e322554ad92e27d8ebdc0e39ff4a3fa6d105419c6455c66256faecb816e7b1e1b2804beea",
                "7ffe70ce4383e81b26f7a5fd8c6532c8a50525696272677120f10f44f16e377edd75e1507acd6572978697bcada43d30fb496caef86a3c77fc1808b8de2cbda3", new HashMap<String, String>() {{
            put("name", "JavaSDK_Test_1537068149724.txt");
            put("description", "Temporary File create from java sdk test");
        }}, "JavaSDK_Test_1537068149724.txt", 9918,
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva", AssetRecord.Status.CONFIRMED);
        final GetBitmarkResponse expectedResponse1 = new GetBitmarkResponse(bitmark1, asset1);
        final GetBitmarkResponse expectedResponse2 = new GetBitmarkResponse(bitmark2, null);
        return Stream.of(Arguments.of(response1, expectedResponse1), Arguments.of(response2,
                expectedResponse2));
    }

    private static Stream<Arguments> createSuccessResponseGetBitmarksResponse() throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/bitmark/bitmarks1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/bitmark/bitmarks2.json"))).message("dummy").build();

        final BitmarkRecord bitmark1 = new BitmarkRecord(
                "dbca8f9d3f6d7a55f5ffa6f22abbccceafe599c2b0af99ccae8dbe8620c02c4facfc5748863d4236005d2fb66b1a078c694d8038fb5aaae0e08486c2c9512710",
                9028, "2018-09-16T05:47:24.000000Z", "2018-09-16T05:47:24.000000Z", HEAD,
                "12d11a294088bc15aa05965098dd965afb21c87d4df317bd8acb2a7b5127ecd2",
                "12d11a294088bc15aa05965098dd965afb21c87d4df317bd8acb2a7b5127ecd2", "2018-09" +
                "-16T05:47:24.000000Z", "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p",
                744455, "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p",
                BitmarkRecord.Status.SETTLED, null);
        final BitmarkRecord bitmark2 = new BitmarkRecord(
                "9ef1590645df106ce428ec9cfdb48cfae29e95ee543a42bc3dcf95969ff9d8285dd07a558abf540838c151a7b34dd8c443430c766e8b1c934bdffa23cc06aa04",
                9027, "2018-09-16T04:08:04.000000Z", "2018-09-16T04:08:04.000000Z", HEAD,
                "402e895c66b9d8e3920a01489b21e33c265bf191ccdd33f040d50168496435d3",
                "402e895c66b9d8e3920a01489b21e33c265bf191ccdd33f040d50168496435d3", "2018-09" +
                "-16T04:08:04.000000Z", "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                744439, "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                BitmarkRecord.Status.SETTLED, null);
        final AssetRecord asset1 = new AssetRecord(9028, 1, "2018-09-16T05:47:24.000000Z", null,
                "01ea9198b08291a94b95db2c959c173db5c7cc48149c4cc455476e5ec925f630b0478c40b207eeb4b99825d412bfe19799a36c983c69bfdb715b6680ce1a562acd",
                "dbca8f9d3f6d7a55f5ffa6f22abbccceafe599c2b0af99ccae8dbe8620c02c4facfc5748863d4236005d2fb66b1a078c694d8038fb5aaae0e08486c2c9512710", new HashMap<String, String>() {{
            put("Saved Time", "2018-09-16T05:46:22.686Z");
            put("Source", "HealthKit");
        }}, "HK61158509", 9944,
                "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p", AssetRecord.Status.CONFIRMED);
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
        return Stream.of(Arguments.of(response1, expectedResponse1), Arguments.of(response2,
                expectedResponse2));
    }

    private static Stream<Arguments> createValidResponseAssetRecord() throws IOException {
        final AssetRecord asset1 = new AssetRecord(9028, 1, "2018-09-16T05:47:24.000000Z", null,
                "01ea9198b08291a94b95db2c959c173db5c7cc48149c4cc455476e5ec925f630b0478c40b207eeb4b99825d412bfe19799a36c983c69bfdb715b6680ce1a562acd",
                "dbca8f9d3f6d7a55f5ffa6f22abbccceafe599c2b0af99ccae8dbe8620c02c4facfc5748863d4236005d2fb66b1a078c694d8038fb5aaae0e08486c2c9512710", new HashMap<String, String>() {{
            put("Saved Time", "2018-09-16T05:46:22.686Z");
            put("Source", "HealthKit");
        }}, "HK61158509", 9944,
                "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p", AssetRecord.Status.CONFIRMED);
        final AssetRecord asset2 = new AssetRecord(9027, 4, "2018-09-16T04:08:04.000000Z", null,
                "011b1860a4cf9c0142248773c84f24f8db6e2b54b6c1803700fce2818dcd001f7a6b0134fe3cb7d7bf0310c749fd0bf977b9f5060c0c34c1d7a755c6f159ab5b1b",
                "9ef1590645df106ce428ec9cfdb48cfae29e95ee543a42bc3dcf95969ff9d8285dd07a558abf540838c151a7b34dd8c443430c766e8b1c934bdffa23cc06aa04", new HashMap<String, String>() {{
            put("name", "JavaSDK_Test_1537070880489.txt");
            put("description", "Temporary File create from java sdk test");
        }}, "JavaSDK_Test_1537070880489.txt", 9942,
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva", AssetRecord.Status.CONFIRMED);
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/asset/asset1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/asset/asset2.json"))).message("dummy").build();
        return Stream.of(Arguments.of(response1, asset1), Arguments.of(response2, asset2));
    }

    private static Stream<Arguments> createValidResponseAssetRecords() throws IOException {
        final AssetRecord asset1 = new AssetRecord(9028, 1, "2018-09-16T05:47:24.000000Z", null,
                "01ea9198b08291a94b95db2c959c173db5c7cc48149c4cc455476e5ec925f630b0478c40b207eeb4b99825d412bfe19799a36c983c69bfdb715b6680ce1a562acd",
                "dbca8f9d3f6d7a55f5ffa6f22abbccceafe599c2b0af99ccae8dbe8620c02c4facfc5748863d4236005d2fb66b1a078c694d8038fb5aaae0e08486c2c9512710", new HashMap<String, String>() {{
            put("Saved Time", "2018-09-16T05:46:22.686Z");
            put("Source", "HealthKit");
        }}, "HK61158509", 9944,
                "fVuED2jekRdEAoKKMw9xZvvtuLi1iyhgXkmLD1w7LLm7m2Pk4p", AssetRecord.Status.CONFIRMED);
        final AssetRecord asset2 = new AssetRecord(9027, 4, "2018-09-16T04:08:04.000000Z", null,
                "011b1860a4cf9c0142248773c84f24f8db6e2b54b6c1803700fce2818dcd001f7a6b0134fe3cb7d7bf0310c749fd0bf977b9f5060c0c34c1d7a755c6f159ab5b1b",
                "9ef1590645df106ce428ec9cfdb48cfae29e95ee543a42bc3dcf95969ff9d8285dd07a558abf540838c151a7b34dd8c443430c766e8b1c934bdffa23cc06aa04", new HashMap<String, String>() {{
            put("name", "JavaSDK_Test_1537070880489.txt");
            put("description", "Temporary File create from java sdk test");
        }}, "JavaSDK_Test_1537070880489.txt", 9942,
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva", AssetRecord.Status.CONFIRMED);
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/asset/assets1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/asset/assets2.json"))).message("dummy").build();
        return Stream.of(Arguments.of(response1, new ArrayList<AssetRecord>() {{
            add(asset1);
            add(asset2);
        }}), Arguments.of(response2, new ArrayList<AssetRecord>() {{
            add(asset1);
        }}));
    }

    private static Stream<Arguments> createValidResponseGetTransactionResponse() throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/transaction/transaction1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/transaction/transaction2.json"))).message("dummy").build();
        final TransactionRecord txRecord1 = new TransactionRecord(
                "fd645350320c9c0b1fbcab2b9678cb50719bff6d0f0d235d9565afdaa470ef4a",
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607",
                HEAD, CONFIRMED, 9093, 8, 744968, null, "", null,
                "fd645350320c9c0b1fbcab2b9678cb50719bff6d0f0d235d9565afdaa470ef4a");
        final TransactionRecord txRecord2 = new TransactionRecord(
                "441d7a04b3d75c3f0f55564331f4bfadecf0a5acc8c0c6a1f5cb9a0db6dca58e",
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607",
                HEAD, CONFIRMED, 9093, 7, 744967, null, "", null,
                "441d7a04b3d75c3f0f55564331f4bfadecf0a5acc8c0c6a1f5cb9a0db6dca58e");
        final AssetRecord assetRecord = new AssetRecord(9093, 1, "2018-09-18T08:09:20.000000Z",
                null,
                "01beef7f85a4bae3bc8e46930a38f7f96812b67d43d2e2796e8cfa3cecea6d32d699f48d96366c51fa2aef7a0e0e9271c189dea51e3bc250dc1e4a53763baf80b3",
                "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607", new HashMap<String, String>() {{
            put("name", "JavaSDK_Test_1537258135324.txt");
            put("description", "Temporary File create from java sdk test");
        }}, "JavaSDK_Test_1537258135324.txt", 10086,
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva", AssetRecord.Status.CONFIRMED);
        final GetTransactionResponse getTransactionResponse1 =
                new GetTransactionResponse(txRecord1, null);
        final GetTransactionResponse getTransactionResponse2 =
                new GetTransactionResponse(txRecord2, assetRecord);
        return Stream.of(Arguments.of(response1, getTransactionResponse1), Arguments.of(response2
                , getTransactionResponse2));
    }

    private static Stream<Arguments> createValidResponseGetTransactionsResponse() throws IOException {
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/transaction/transactions1.json"))).message("dummy").build();
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/query/transaction/transactions2.json"))).message("dummy").build();
        final TransactionRecord txRecord1 = new TransactionRecord(
                "fd645350320c9c0b1fbcab2b9678cb50719bff6d0f0d235d9565afdaa470ef4a",
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607",
                HEAD, CONFIRMED, 9093, 8, 744968, null, "", null,
                "fd645350320c9c0b1fbcab2b9678cb50719bff6d0f0d235d9565afdaa470ef4a");
        final TransactionRecord txRecord2 = new TransactionRecord(
                "441d7a04b3d75c3f0f55564331f4bfadecf0a5acc8c0c6a1f5cb9a0db6dca58e",
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607",
                HEAD, CONFIRMED, 9093, 7, 744967, null, "", null,
                "441d7a04b3d75c3f0f55564331f4bfadecf0a5acc8c0c6a1f5cb9a0db6dca58e");
        final AssetRecord assetRecord = new AssetRecord(9093, 1, "2018-09-18T08:09:20.000000Z",
                null,
                "01beef7f85a4bae3bc8e46930a38f7f96812b67d43d2e2796e8cfa3cecea6d32d699f48d96366c51fa2aef7a0e0e9271c189dea51e3bc250dc1e4a53763baf80b3",
                "86ec6f1fb9717ce9dd212e236d97495b1bdbbb146e316a6ada589eda1c86914854015f5bd92ad1701491e71ebab471cb1d45a31f9bd476bad4b12aeeeced8607", new HashMap<String, String>() {{
            put("name", "JavaSDK_Test_1537258135324.txt");
            put("description", "Temporary File create from java sdk test");
        }}, "JavaSDK_Test_1537258135324.txt", 10086,
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva", AssetRecord.Status.CONFIRMED);
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
        return Stream.of(Arguments.of(response1, getTransactionsResponse1), Arguments.of(response2
                , getTransactionsResponse2));
    }
}