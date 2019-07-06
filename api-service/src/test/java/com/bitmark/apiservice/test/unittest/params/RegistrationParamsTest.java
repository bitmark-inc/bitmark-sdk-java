package com.bitmark.apiservice.test.unittest.params;

import com.bitmark.apiservice.params.RegistrationParams;
import com.bitmark.apiservice.test.BaseTest;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.bitmark.apiservice.test.unittest.DataProvider.*;
import static com.bitmark.apiservice.test.utils.FileUtils.getResourceFile;
import static com.bitmark.apiservice.test.utils.FileUtils.loadRequest;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RegistrationParamsTest extends BaseTest {

    @ParameterizedTest
    @MethodSource("createValidNameMetadataAddress")
    public void testConstructRegistrationParams_ValidAllParams_ValidInstanceIsReturn(String name,
                                                                                     Map<String,
                                                                                             String> metadata,
                                                                                     Address address) {
        assertDoesNotThrow(() -> new RegistrationParams(name, metadata, address));
    }

    @ParameterizedTest
    @MethodSource("createInvalidNameMetadataAddress")
    public void testConstructRegistrationParams_InvalidParams_ErrorIsThrow(String name,
                                                                           Map<String,
                                                                                   String> metadata,
                                                                           Address address) {
        assertThrows(ValidateException.class, () -> new RegistrationParams(name, metadata,
                                                                           address));
    }

    @ParameterizedTest
    @MethodSource("createFileFingerprint")
    public void testGenerateFingerprint_ValidFile_CorrectFingerprintIsReturn(File file,
                                                                             String expectedFingerprint) {
        final RegistrationParams params = new RegistrationParams(ASSET_NAME, METADATA, ADDRESS1);
        String fingerprint = params.generateFingerprint(file);
        assertTrue(expectedFingerprint.equalsIgnoreCase(fingerprint));
    }

    @ParameterizedTest
    @MethodSource("createInvalidFile")
    public void testGenerateFingerprint_InvalidFile_ErrorIsThrow(File file) {
        assertThrows(Exception.class, () -> new RegistrationParams(ASSET_NAME, METADATA,
                                                                   ADDRESS1)
                .generateFingerprint(file));
    }

    @ParameterizedTest
    @MethodSource("createParamsSignature")
    public void testSign_NoCondition_CorrectSignatureIsReturn(RegistrationParams params,
                                                              String expectedSignature) {
        params.sign(KEY_PAIR_1);
        assertTrue(expectedSignature.equalsIgnoreCase(params.getSignature()));
    }

    @ParameterizedTest
    @MethodSource("createValidParamsJson")
    public void testToJson_NoCondition_CorrectJsonIsReturn(RegistrationParams params,
                                                           String expectedJson) {
        assertTrue(expectedJson.equalsIgnoreCase(params.toJson()));
    }

    @ParameterizedTest
    @MethodSource("createNotSignedRegistrationParams")
    public void testToJson_ParamsIsNotSigned_ErrorIsThrow(RegistrationParams params) {
        assertThrows(UnsupportedOperationException.class, params::toJson);
    }

    private static Stream<Arguments> createValidNameMetadataAddress() {
        return Stream.of(Arguments.of("", new HashMap<String, String>() {{
            put("name", "name");
            put("description", "description");
        }}, ADDRESS1), Arguments.of("Asset2", new HashMap<String, String>() {{
            put("name", "name");
            put("description", "description");
        }}, ADDRESS1), Arguments.of(null, null, ADDRESS1));
    }

    private static Stream<Arguments> createInvalidNameMetadataAddress() {
        return Stream.of(Arguments
                                 .of("This is too longggggggggggggggggggggggggggggggggggggggggggggggggggggggg name",
                                     new HashMap<String, String>() {{
                                         put("name", "name");
                                         put("description", "description");
                                     }}, ADDRESS1),
                         Arguments.of("Asset1", new HashMap<String, String>() {{
                             put("name",
                                 "Tarketios and his two older companions were skilled metalworkers from a region some two hundred miles to the north, where the hills were rich with iron, copper, and lead." +
                                 " They had been on a trading journey to the south and were returning home. Just as the river path carried Larth’s people from the seashore to the hills, so another path," +
                                 " perpendicular to the river, traversed the long coastal plain. Because the island provided an easy place to ford the river, it was here that the two paths intersected." +
                                 " On this occasion, the salt traders and the metal traders happened to arrive at the island on the same day. Now they met for the first time.The two groups made separate" +
                                 " camps at opposite ends of the island. As a gesture of friendship, speaking with his hands, Larth invited Tarketios and the others to share the venison that night." +
                                 " As the hosts and their guests feasted around the roasting fire, Tarketios tried to explain something of his craft. Firelight glittered in Lara’s eyes as she watched" +
                                 " Tarketios point at the flames and mime the act of hammering. Firelight danced across the flexing muscles of his arms and shoulders. When he smiled at her, his grin was like a boast." +
                                 " She had never seen teeth so white and so perfect.Po saw the looks the two exchanged and frowned. Lara’s father saw the same looks and smiled.The meal was over." +
                                 " The metal traders, after many gestures of gratitude for the venison, withdrew to their camp at the far side of the island. Before he disappeared into the shadows," +
                                 " Tarketios looked over his shoulder and gave Lara a parting grin.While the others settled down to sleep, Larth stayed awake a while longer, as was his habit." +
                                 " He liked to watch the fire. Like all other things, fire possessed a numen that sometimes communicated with him, showing him visions." +
                                 " As the last of the embers faded into darkness, Larth fell asleep.Larth blinked. The flames, which had dwindled to almost nothing, suddenly shot up again." +
                                 " Hot air rushed over his face. His eyes were seared by white flames brighter than the sun.");
                         }}, ADDRESS1),
                         Arguments.of("Asset2", new HashMap<String, String>() {{
                             put("name", "name");
                             put("description", "description");
                         }}, null));
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
        final String json1 = loadRequest("/registration/registration1.json");
        final String json2 = loadRequest("/registration/registration2.json");
        final RegistrationParams params1 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS1);
        final RegistrationParams params2 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS1);
        params1.generateFingerprint(getResourceFile("asset1.txt"));
        params2.generateFingerprint(getResourceFile("asset2.txt"));
        params1.sign(KEY_PAIR_1);
        params2.sign(KEY_PAIR_1);
        return Stream.of(Arguments.of(params1, json1), Arguments.of(params2, json2));
    }

    private static Stream<Arguments> createParamsSignature() {
        final RegistrationParams params1 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS1);
        final RegistrationParams params2 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS1);
        params1.generateFingerprint(getResourceFile("asset1.txt"));
        params2.generateFingerprint(getResourceFile("asset2.txt"));
        return Stream.of(Arguments.of(params1,
                                      "28e283ad53978b6cf76f1fbc6ebfabd5eed1730cc50c45084f23c9c04ec8afed431b63279da72c0d6e5defd0fe112cee757cb0a55432c783a02a4c225cad7403"),
                         Arguments.of(params2,
                                      "844ef8946a676bf17ba2b642c8b6a52275435c8ba227c5b6e7ccbd0041dea255ba908697a4241a923ca114fe8c5bef130e9d098eb73e68e104c909d733197001"));
    }

    private static Stream<RegistrationParams> createNotSignedRegistrationParams() {
        final RegistrationParams params1 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS1);
        final RegistrationParams params2 = new RegistrationParams("name", new HashMap<String,
                String>() {{
            put("author", "test");
        }}, ADDRESS1);
        params1.generateFingerprint(getResourceFile("asset1.txt"));
        params2.generateFingerprint(getResourceFile("asset2.txt"));
        return Stream.of(params1, params2);
    }
}
