package test.middleware;

import okhttp3.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.middleware.Converter;
import service.response.RegistrationResponse;
import test.BaseTest;
import utils.callback.Callback1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.FileUtils.loadResponse;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class ConverterTest extends BaseTest {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @DisplayName("Verify function Converter.toTxIds(Callback1<List<String>>) works well with " +
                         "happy condition")
    @ParameterizedTest
    @MethodSource("createSuccessResponseIssue")
    public void testConvertTxIds_ValidResponse_CorrectTxIdsIsReturn(Response response,
                                                                    String[] expectedTxIds) {
        Callback1<Response> callback = Converter.toTxIds(new Callback1<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                assertTrue(Arrays.equals(expectedTxIds,
                        data.toArray(new String[expectedTxIds.length])));
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
    public void testConvertRegistrationResponse_ValidResponse_CorrectRegistrationResponseIsReturn(Response response, RegistrationResponse expectedResponse) {
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

    @DisplayName("Verify function Converter.toOfferId(Callback1<String> works well)")
    @ParameterizedTest
    @MethodSource("createSuccessResponseOfferId")
    public void testConvertOfferId_ValidResponse_CorrectTxIdIsReturn(Response response,
                                                                     String expectedOfferId) {
        Callback1<Response> callback = Converter.toTxId(new Callback1<String>() {
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

    private static Stream<Arguments> createSuccessResponseIssue() throws IOException {
        final String[] txIds1 = new String[]{
                "e8f8867231590f19a4c353a3487b4931a462ae7b9e0cd5471618aa3e955f236f",
                "995f2d5f3bcb6cab43d4a758efe39016d5012f07098eaf63179a60dedba85a25",
                "d25e784a72d2c12a7813368167a6d3a2b47a38e836ab88bb10e6b2b71dfb8cb7",
                "682e1d3762a6b2c103d2b98233db04aadfd66e39d6a5d4f32cd5762f7352d492",
                "e489a1dc930f55ce74f3f39e2e4b52cb1e0a57e720cea2ba7c2dc0b471354e27",
                "1246e2a223a9c4e7a63b12ad0b854a92d26a79cf53bd780783779e08f88131bd",
                "8ca6cfb7e689e11bf32d6a44528678155f77278349b8d8c938050951a9c8b664",
                "a845c1666f45ac7ac7a6413a1b36f846ef638201cceb85fcef7333df8aa20e53",
                "eb799ecdc2e0911d1744899c830c75600469f316ad033312d64cb0c1739bf0a6",
                "0107f4dbc256591c540722fd2f558e43dd7f197faa88ace608ab82438aa650c0"};
        final Response response1 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/issue/multiple_issue.json"))).message("dummy").build();
        final String[] txIds2 = new String[]{
                "e8f8867231590f19a4c353a3487b4931a462ae7b9e0cd5471618aa3e955f236f"};
        final Response response2 = new Response.Builder().request(new Request.Builder().url("http" +
                "://dummy.com").build()).protocol(Protocol.HTTP_1_1).code(200).body(ResponseBody.create(JSON,
                loadResponse("/issue/single_issue.json"))).message("dummy").build();
        return Stream.of(Arguments.of(response1, txIds1), Arguments.of(response2, txIds2));
    }

    private static Stream<Arguments> createSuccessResponseRegistrationResponse() throws IOException {
        final RegistrationResponse registrationResponse1 = new RegistrationResponse(
                "d20f8bc16350a4f53fb5b685d4e7cedf6e07ab9be9533effb2008ea8434a7685646e042fbdf8a9df46085c19648fb9ce99095c5fa16df25d56721d233646d38b", false);
        final RegistrationResponse registrationResponse2 = new RegistrationResponse(
                "d20f8bc16350a4f53fb5b685d4e7cedf6e07ab9be9533effb2008ea8434a7685646e042fbdf8a9df46085c19648fb9ce99095c5fa16df25d56721d233646d38b", true);
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
}