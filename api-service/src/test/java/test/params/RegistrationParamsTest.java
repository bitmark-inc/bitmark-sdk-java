package test.params;

import crypto.key.KeyPair;
import crypto.key.StandardKeyPair;
import error.ValidateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import service.params.RegistrationParams;
import test.BaseTest;
import utils.Address;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;
import static util.FileUtils.getResourceFile;
import static util.FileUtils.loadRequest;

/**
 * @author Hieu Pham
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RegistrationParamsTest extends BaseTest {

    private static final String PRIVATE_KEY =
            "0246A917D422E596168185CEA9943459C09751532C52FE4DDC27B06E2893EF2258760A01EDF5ED4F95BFE977D77A27627CD57A25DF5DEA885972212C2B1C0E2F";

    private static final String PUBLIC_KEY =
            "58760A01EDF5ED4F95BFE977D77A27627CD57A25DF5DEA885972212C2B1C0E2F";

    private static final KeyPair KEY = StandardKeyPair.from(HEX.decode(PUBLIC_KEY),
            HEX.decode(PRIVATE_KEY));

    private static final Address ADDRESS = Address.fromAccountNumber(
            "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva");

    private static final Map<String, String> METADATA = new HashMap<String, String>() {{
        put("name", "name");
        put("des", "des");
    }};

    private static final String NAME = "name";


    @DisplayName("Verify function new RegistrationParams(String, Map, Address) works well with " +
                         "happy condition")
    @ParameterizedTest
    @MethodSource("createValidNameMetadataAddress")
    public void testConstructRegistrationParams_ValidAllParams_ValidInstanceIsReturn(String name,
                                                                                     Map<String,
                                                                                             String> metadata, Address address) {
        assertDoesNotThrow(() -> new RegistrationParams(name, metadata, address));
    }

    @DisplayName("Verify function new RegistrationParams(String, Map, Address) throws error with " +
                         "invalid params")
    @ParameterizedTest
    @MethodSource("createInvalidNameMetadataAddress")
    public void testConstructRegistrationParams_InvalidParams_ErrorIsThrow(String name,
                                                                           Map<String,
                                                                                   String> metadata, Address address) {
        assertThrows(ValidateException.class, () -> new RegistrationParams(name, metadata,
                address));
    }

    @DisplayName("Verify function RegistrationParams.generateFingerprint(File) works well with " +
                         "happy condition")
    @ParameterizedTest
    @MethodSource("createFileFingerprint")
    public void testGenerateFingerprint_ValidFile_CorrectFingerprintIsReturn(File file,
                                                                             String expectedFingerprint) {
        final RegistrationParams params = new RegistrationParams(NAME, METADATA, ADDRESS);
        String fingerprint = params.generateFingerprint(file);
        assertTrue(expectedFingerprint.equalsIgnoreCase(fingerprint));
    }

    @DisplayName("Verify function RegistrationParams.generateFingerprint(File) throws error with " +
                         "invalid file")
    @ParameterizedTest
    @MethodSource("createInvalidFile")
    public void testGenerateFingerprint_InvalidFile_ErrorIsThrow(File file) {
        assertThrows(Exception.class, () -> new RegistrationParams(NAME, METADATA,
                ADDRESS).generateFingerprint(file));
    }

    @DisplayName("Verify function RegistrationParams.sign(KeyPair) works well with " +
                         "happy condition")
    @ParameterizedTest
    @MethodSource("createParamsSignature")
    public void testSign_NoCondition_CorrectSignatureIsReturn(RegistrationParams params,
                                                              String expectedSignature) {
        params.sign(KEY);
        assertTrue(expectedSignature.equalsIgnoreCase(params.getSignature()));
    }

    @DisplayName("Verify function RegistrationParams.sign(KeyPair) throws error with " +
                         "invalid key")
    @ParameterizedTest
    @MethodSource("createValidParamsJson")
    public void testToJson_NoCondition_CorrectJsonIsReturn(RegistrationParams params,
                                                           String expectedJson) {
        final String json =
                expectedJson.replace("\\" + "u" + "0000", String.valueOf('\u0000'));
        assertTrue(json.equalsIgnoreCase(params.toJson()));
    }

    private static Stream<Arguments> createValidNameMetadataAddress() {
        return Stream.of(Arguments.of("Asset1", new HashMap<String, String>() {{
            put("name", "name");
            put("description", "description");
        }}, ADDRESS), Arguments.of("Asset2", new HashMap<String, String>() {{
            put("name", "name");
            put("description", "description");
        }}, ADDRESS), Arguments.of("Asset3", new HashMap<String, String>() {{
            put("name", "name");
        }}, ADDRESS));
    }

    private static Stream<Arguments> createInvalidNameMetadataAddress() {
        return Stream.of(Arguments.of("", new HashMap<String, String>() {{
            put("name", "name");
            put("description", "description");
        }}, ADDRESS), Arguments.of("Asset2", new HashMap<String, String>() {{
            put("name", "name");
            put("description", "description");
        }}, null), Arguments.of("Asset3", new HashMap<String, String>(), ADDRESS));
    }

    private static Stream<Arguments> createFileFingerprint() {
        final File file1 = getResourceFile("asset1.txt");
        final File file2 = getResourceFile("asset2.txt");
        return Stream.of(Arguments.of(file1,
                "016e627499a0a1db93e04c4b32cc8dce498c549ac69300ce6ff46e4a2c225929be3a6081f5b072da50cb819ae297aa154430c5be7b046f834692f581f9cf2d4fb0"),
                Arguments.of(file2,
                        "018a4487f170f242a244079db8412bb5174d04dd5b971d71ca24c78ab88db5e4bcaf39f899a120345e5c89be90ff70dde31638eeb91fb122c5085138735740b36a"));
    }

    private static Stream<File> createInvalidFile() {
        return Stream.of(null, new File(""), new File("test.abc"));
    }

    private static Stream<Arguments> createValidParamsJson() throws IOException {
        final String json1 = loadRequest("/registration/registration1.json").replace(" ", "");
        final String json2 = loadRequest("/registration/registration2.json").replace(" ", "");
        final RegistrationParams params1 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS);
        final RegistrationParams params2 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS);
        params1.generateFingerprint(getResourceFile("asset1.txt"));
        params2.generateFingerprint(getResourceFile("asset2.txt"));
        params1.sign(KEY);
        params2.sign(KEY);
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }

    private static Stream<Arguments> createParamsSignature() {
        final RegistrationParams params1 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS);
        final RegistrationParams params2 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS);
        params1.generateFingerprint(getResourceFile("asset1.txt"));
        params2.generateFingerprint(getResourceFile("asset2.txt"));
        return Stream.of(Arguments.of(params1,
                "28e283ad53978b6cf76f1fbc6ebfabd5eed1730cc50c45084f23c9c04ec8afed431b63279da72c0d6e5defd0fe112cee757cb0a55432c783a02a4c225cad7403"),
                Arguments.of(params2,
                        "844ef8946a676bf17ba2b642c8b6a52275435c8ba227c5b6e7ccbd0041dea255ba908697a4241a923ca114fe8c5bef130e9d098eb73e68e104c909d733197001"));
    }
}
