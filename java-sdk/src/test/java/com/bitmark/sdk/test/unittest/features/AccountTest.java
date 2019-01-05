package com.bitmark.sdk.test.unittest.features;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.internal.Seed;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.features.internal.SeedTwentyFour;
import com.bitmark.sdk.test.unittest.BaseTest;
import com.bitmark.sdk.utils.AccountNumberData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hieu Pham
 * @since 8/31/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class AccountTest extends BaseTest {

    @Test
    public void testNewAccount_NoCondition_ValidAccountIsCreated() {
        final Account account = new Account();
        assertEquals(account.getKeyPair().privateKey().size(), Ed25519.PRIVATE_KEY_LENGTH);
        assertEquals(account.getKeyPair().publicKey().size(), Ed25519.PUBLIC_KEY_LENGTH);
        assertNotNull(account.getAccountNumber());
        assertTrue(Account.isValidAccountNumber(account.getAccountNumber()));
    }

    @ParameterizedTest
    @MethodSource("createSeedAccountNumberPubKeyString")
    public void testNewAccountFromSeed_ValidSeed_ValidAccountIsCreated(Seed seed,
                                                                       String accountNumber,
                                                                       String publicKey) {
        final Account account = Account.fromSeed(seed);
        assertEquals(account.getAccountNumber(), accountNumber);
        assertEquals(HEX.encode(account.getKeyPair().publicKey().toBytes()), publicKey);
    }

    @ParameterizedTest
    @MethodSource("createInvalidSeedBecauseOfNetwork")
    public void testNewAccountFromSeed_InvalidSeed_ErrorIsThrow(Seed seed) {
        ValidateException exception = assertThrows(ValidateException.class,
                                                   () -> Account.fromSeed(seed));
        assertEquals("Incorrect network from Seed", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("createRecoveryPhraseAccountNumberPublicKey")
    public void testNewAccountFromRecoveryPhrase_ValidRecoveryPhrase_ValidAccountIsCreated(
            String recoveryPhrase, String accountNumber, String publicKey) {
        final Account account = Account.fromRecoveryPhrase(recoveryPhrase.split(" "));
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(publicKey, HEX.encode(account.getKeyPair().publicKey().toBytes()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"% ^", "crime cricket castle fun purse announce nephew profit cloth " +
                                   "trim delivery", "",
                            "this is the recovery phrase that has 24 words but not contains " +
                            "the words from bip39 list so it will throw exception $ ^"})
    public void testNewAccountFromRecoveryPhrase_InvalidRecoveryPhrase_ErrorIsThrow(
            String recoveryPhrase) {
        assertThrows(ValidateException.class,
                     () -> Account.fromRecoveryPhrase(recoveryPhrase.split(" ")));
    }

    @ParameterizedTest
    @CsvSource({"file earn crack fever crack differ wreck crazy salon imitate swamp sample, " +
                "9J878SbnM2GFqAELkkiZbqHJDkAj57fYK",
                "during kingdom crew atom practice brisk weird " +
                "document eager artwork ride then, 9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH", "accident " +
                                                                                       "syrup inquiry you clutch liquid fame upset joke glow best " +
                                                                                       "school repeat birth library combine access camera organ trial crazy jeans lizard " +
                                                                                       "science, 5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD",
                "abuse tooth riot" +
                " whale dance dawn armor patch tube sugar edit clean guilt person lake height tilt " +
                "wall prosper episode produce spy artist account, " +
                "5XEECsXPYA9wDVXMtRMAVrtaWx7WSc5tG2hqj6b8iiz9rARjg2BgA9w"})
    public void testGetSeedFromExistedAccount_ValidAccount_CorrectSeedIsReturn(
            String recoveryPhrase, String expectedSeed) {
        final Account account = Account.fromRecoveryPhrase(recoveryPhrase.split(" "));
        final String encodedSeed = account.getSeed().getEncodedSeed();
        assertEquals(expectedSeed, encodedSeed);
    }

    @ParameterizedTest
    @MethodSource("createSeedRecoveryPhrase")
    public void testGetRecoveryPhrase_NoCondition_CorrectRecoveryPhraseIsReturn(Seed seed,
                                                                                String expectedRecoveryPhrase) {
        final Account account = Account.fromSeed(seed);
        assertTrue(Arrays.equals(expectedRecoveryPhrase.split(" "),
                                 account.getRecoveryPhrase().getMnemonicWords()));
    }

    @ParameterizedTest
    @MethodSource("createValidEncodedSeedLocaleRecoveryPhrase")
    public void testGetRecoveryPhraseWithLocale_NoCondition_CorrectRecoveryPhraseIsReturn(
            Seed seed, Locale locale, String expectedRecoveryPhrase) {
        final Account account = Account.fromSeed(seed);
        assertTrue(Arrays.equals(expectedRecoveryPhrase.split(" "),
                                 account.getRecoveryPhrase(locale).getMnemonicWords()));
    }

    @ParameterizedTest
    @CsvSource({"ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva, true",
                "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva, true",
                "ec6yMcJATX6gjNwvqpBNbc4jNEasoUgbfBBGGMM5NvoJ54NXva, false"})
    public void testCheckValidAccountNumber_NoCondition_CorrectResultIsReturn(String accountNumber,
                                                                              boolean expectedResult) {
        assertEquals(Account.isValidAccountNumber(accountNumber), expectedResult);
    }

    @ParameterizedTest
    @MethodSource("createAccountNumberPublicKeyNetwork")
    public void testParseAccountNumber_ValidAccountNumber_CorrectResultIsReturn(
            String accountNumber, String publicKey, Network network) {
        final AccountNumberData data = Account.parseAccountNumber(accountNumber);
        assertEquals(data.getNetwork(), network);
        assertEquals(HEX.encode(data.getPublicKey().toBytes()), publicKey);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54N",
                            "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f", ""})
    public void testParseAccountNumber_InvalidAccountNumber_ErrorIsThrow(String accountNumber) {
        assertThrows(ValidateException.class, () -> Account.parseAccountNumber(accountNumber));
    }

    private static Stream<Arguments> createRecoveryPhraseAccountNumberPublicKey() {
        return Stream.of(Arguments.of("name gaze apart lamp lift zone believe steak session " +
                                      "laptop crowd hill",
                                      "eMCcmw1SKoohNUf3LeioTFKaYNYfp2bzFYpjm3EddwxBSWYVCb",
                                      "369f6ceb1c23dbccc61b75e7990d0b2db8e1ee8da1c44db32280e63ca5804f38"),
                         Arguments
                                 .of("depend crime cricket castle fun purse announce nephew profit cloth " +
                                     "trim deliver",
                                     "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs",
                                     "d1c177ef358e9d1f0d4b09328cc1213e8d3580703aee51ccf97e482be977f7bc"),
                         Arguments.of("accident syrup inquiry you clutch liquid fame upset joke " +
                                      "glow best school repeat birth library combine access camera " +
                                      "organ trial crazy jeans lizard science",
                                      "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                                      "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f"),
                         Arguments
                                 .of("abuse tooth riot whale dance dawn armor patch tube sugar edit clean" +
                                     " guilt person lake height tilt wall prosper episode produce spy " +
                                     "artist account",
                                     "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX",
                                     "807f4d123c944e0c3ecc95d9bde89916ced6341a8c8cedeb8caafef8f35654e7"));
    }

    private static Stream<Arguments> createAccountNumberPublicKeyNetwork() {
        return Stream.of(Arguments.of("ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                                      "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f",
                                      Network.TEST_NET));
    }

    private static Stream<Seed> createInvalidSeedBecauseOfNetwork() {
        return Stream.of(new SeedTwelve(HEX.decode(
                "ba0e357d9157a1a7299fbc4cb4c933bd00")),
                         new SeedTwelve(HEX.decode("00d00c884d08394698fbffbb6259d646b0")),
                         new SeedTwentyFour(HEX.decode(
                                 "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                                            Network.LIVE_NET));
    }

    private static Stream<Arguments> createValidEncodedSeedLocaleRecoveryPhrase() {
        return Stream.of(Arguments.of(SeedTwelve
                                              .fromEncodedSeed("9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"),
                                      Locale.ENGLISH,
                                      "during kingdom crew atom practice brisk weird document eager artwork ride then")
                ,
                         Arguments.of(SeedTwelve
                                              .fromEncodedSeed("9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2"),
                                      Locale.CHINESE, "專 青 辦 增 孔 咱 " +
                                                      "裡 耕 窮 節 撲 易"),
                         Arguments.of(
                                 SeedTwentyFour.fromEncodedSeed(
                                         "5XEECsXPYA9wDVXMtRMAVrtaWx7WSc5tG2hqj6b8iiz9rARjg2BgA9w"),
                                 Locale.ENGLISH,
                                 "abuse tooth riot whale dance dawn armor patch tube sugar" +
                                 " edit clean guilt person lake height tilt wall prosper episode " +
                                 "produce spy artist account"));
    }

    private static Stream<Arguments> createSeedAccountNumberPubKeyString() {

        return Stream.of(Arguments.of(SeedTwentyFour.fromEncodedSeed(
                "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD"),
                                      "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                                      "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f"),
                         Arguments.of(SeedTwentyFour.fromEncodedSeed(
                                 "5XEECsXPYA9wDVXMtRMAVrtaWx7WSc5tG2hqj6b8iiz9rARjg2BgA9w"),
                                      "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX",
                                      "807f4d123c944e0c3ecc95d9bde89916ced6341a8c8cedeb8caafef8f35654e7"),
                         Arguments.of(SeedTwelve
                                              .fromEncodedSeed("9J87CAsHdFdoEu6N1unZk3sqhVBkVL8Z8"),
                                      "eMCcmw1SKoohNUf3LeioTFKaYNYfp2bzFYpjm3EddwxBSWYVCb",
                                      "369f6ceb1c23dbccc61b75e7990d0b2db8e1ee8da1c44db32280e63ca5804f38"));
    }

    private static Stream<Arguments> createSeedRecoveryPhrase() {
        return Stream.of(Arguments.of(SeedTwelve
                                              .fromEncodedSeed("9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"),
                                      "during kingdom crew atom practice brisk weird document eager artwork ride then"),
                         Arguments.of(SeedTwelve
                                              .fromEncodedSeed("9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2"),
                                      "depend crime cricket castle fun purse announce nephew profit cloth trim deliver"),
                         Arguments.of(SeedTwentyFour.fromEncodedSeed(
                                 "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD"),
                                      "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat " +
                                      "birth library combine access camera organ trial crazy jeans lizard science"));
    }

}
