/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.unittest.features;

import com.bitmark.apiservice.configuration.Network;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.Random;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.crypto.key.PrivateKey;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.internal.Seed;
import com.bitmark.sdk.features.internal.SeedTwelve;
import com.bitmark.sdk.features.internal.SeedTwentyFour;
import com.bitmark.sdk.test.unittest.BaseTest;
import org.junit.jupiter.api.RepeatedTest;
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

public class AccountTest extends BaseTest {

    private static Stream<Arguments> createRecoveryPhraseAccountNumberPublicKey() {
        return Stream.of(
                Arguments.of(
                        "name gaze apart lamp lift zone believe steak session laptop crowd hill",
                        "eMCcmw1SKoohNUf3LeioTFKaYNYfp2bzFYpjm3EddwxBSWYVCb",
                        "369f6ceb1c23dbccc61b75e7990d0b2db8e1ee8da1c44db32280e63ca5804f38"
                ),
                Arguments
                        .of(
                                "depend crime cricket castle fun purse announce nephew profit cloth trim deliver",
                                "fXXHGtCdFPuQvNhJ4nDPKCdwPxH7aSZ4842n2katZi319NsaCs",
                                "d1c177ef358e9d1f0d4b09328cc1213e8d3580703aee51ccf97e482be977f7bc"
                        ),
                Arguments.of(
                        "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                        "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f"
                ),
                Arguments
                        .of(
                                "abuse tooth riot whale dance dawn armor patch tube sugar edit clean guilt person lake height tilt wall prosper episode produce spy artist account",
                                "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX",
                                "807f4d123c944e0c3ecc95d9bde89916ced6341a8c8cedeb8caafef8f35654e7"
                        )
        );
    }

    private static Stream<Seed> createInvalidSeedBecauseOfNetwork() {
        return Stream.of(
                new SeedTwelve(HEX.decode(
                        "ba0e357d9157a1a7299fbc4cb4c933bd00")),
                new SeedTwelve(HEX.decode("00d00c884d08394698fbffbb6259d646b0")),
                new SeedTwentyFour(
                        HEX.decode(
                                "7b95d37f92c904949f79784c7855606b6a2d60416f01441671f4132cef60b607"),
                        Network.LIVE_NET
                )
        );
    }

    private static Stream<Arguments> createValidEncodedSeedLocaleRecoveryPhrase() {
        return Stream.of(Arguments.of(
                SeedTwelve
                        .fromEncodedSeed("9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"),
                Locale.ENGLISH,
                "during kingdom crew atom practice brisk weird document eager artwork ride then"
                )
                ,
                Arguments.of(SeedTwelve
                                .fromEncodedSeed("9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2"),
                        Locale.TRADITIONAL_CHINESE, "專 青 辦 增 孔 咱 " +
                                "裡 耕 窮 節 撲 易"
                ),
                Arguments.of(
                        SeedTwentyFour.fromEncodedSeed(
                                "5XEECsXPYA9wDVXMtRMAVrtaWx7WSc5tG2hqj6b8iiz9rARjg2BgA9w"),
                        Locale.ENGLISH,
                        "abuse tooth riot whale dance dawn armor patch tube sugar" +
                                " edit clean guilt person lake height tilt wall prosper episode " +
                                "produce spy artist account"
                )
        );
    }

    private static Stream<Arguments> createSeedAccountNumberPubKeyString() {

        return Stream.of(
                Arguments.of(
                        SeedTwentyFour.fromEncodedSeed(
                                "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD"),
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                        "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f"
                ),
                Arguments.of(
                        SeedTwentyFour.fromEncodedSeed(
                                "5XEECsXPYA9wDVXMtRMAVrtaWx7WSc5tG2hqj6b8iiz9rARjg2BgA9w"),
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX",
                        "807f4d123c944e0c3ecc95d9bde89916ced6341a8c8cedeb8caafef8f35654e7"
                ),
                Arguments.of(
                        SeedTwelve
                                .fromEncodedSeed(
                                        "9J87CAsHdFdoEu6N1unZk3sqhVBkVL8Z8"),
                        "eMCcmw1SKoohNUf3LeioTFKaYNYfp2bzFYpjm3EddwxBSWYVCb",
                        "369f6ceb1c23dbccc61b75e7990d0b2db8e1ee8da1c44db32280e63ca5804f38"
                )
        );
    }

    private static Stream<Arguments> createEncodedSeedAccountNumberPubKeyString() {

        return Stream.of(
                Arguments.of(
                        "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD",
                        "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva",
                        "58760a01edf5ed4f95bfe977d77a27627cd57a25df5dea885972212c2b1c0e2f"
                ),
                Arguments.of(
                        "5XEECsXPYA9wDVXMtRMAVrtaWx7WSc5tG2hqj6b8iiz9rARjg2BgA9w",
                        "eujeF5ZBDV3qJyKeHxNqnmJsrc9iN7eHJGECsRuSXvLmnNjsWX",
                        "807f4d123c944e0c3ecc95d9bde89916ced6341a8c8cedeb8caafef8f35654e7"
                ),
                Arguments.of(
                        "9J87CAsHdFdoEu6N1unZk3sqhVBkVL8Z8",
                        "eMCcmw1SKoohNUf3LeioTFKaYNYfp2bzFYpjm3EddwxBSWYVCb",
                        "369f6ceb1c23dbccc61b75e7990d0b2db8e1ee8da1c44db32280e63ca5804f38"
                )
        );
    }

    private static Stream<Arguments> createPrivateKeyAccountNumberPubKeyString() {

        return Stream.of(
                Arguments.of(
                        "599b8bd18f92738c69683119172cc132b840653ebc6fc29476f56a791e93b03941e6258f3c7e12bd77d22d100315e33746a6383ee7e8dc9af1a6f78fbae3d507",
                        "eSAeuiZUd1AFatviHmhEukMeuTWWaN1mTn6HukPNiKzXK5msB4",
                        "41e6258f3c7e12bd77d22d100315e33746a6383ee7e8dc9af1a6f78fbae3d507"
                ),
                Arguments.of(
                        "6e54fd8d8552f5333f00d7f483cbaf21043862eea53d310eb30266a218946f3d29d076de06ab7e95a654c912ad131f13b5893d6dc2dbb0dc42af9d86ea4afac5",
                        "eFZSf42hsm7UgabpRLS2pQcwcSYC7rXqLo7LGqWpXiKL3vTub3",
                        "29d076de06ab7e95a654c912ad131f13b5893d6dc2dbb0dc42af9d86ea4afac5"
                ),
                Arguments.of(
                        "fb7f881d78d5c6a54a7a76e21cfe038eb4fc2f542beb5ef95a75053b2ccfc86b07b62bbb4a03fa6d78359c9dfdc4465f47306ee7ace50d0322a859017569e151",
                        "dzYLPyYveacCMDKMBE23inpTPuMXebmcQ98ajc3rXwwYSuKGpS",
                        "07b62bbb4a03fa6d78359c9dfdc4465f47306ee7ace50d0322a859017569e151"
                )
        );
    }

    private static Stream<Arguments> createSeedRecoveryPhrase() {
        return Stream.of(
                Arguments.of(
                        SeedTwelve.fromEncodedSeed(
                                "9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH"),
                        "during kingdom crew atom practice brisk weird document eager artwork ride then"
                ),
                Arguments.of(
                        SeedTwelve.fromEncodedSeed(
                                "9J876mP7wDJ6g5P41eNMN8N3jo9fycDs2"),
                        "depend crime cricket castle fun purse announce nephew profit cloth trim deliver"
                ),
                Arguments.of(
                        SeedTwentyFour.fromEncodedSeed(
                                "5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD"),
                        "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat " +
                                "birth library combine access camera organ trial crazy jeans lizard science"
                )
        );
    }

    @Test
    public void testNewAccount_NoCondition_ValidAccountIsCreated() {
        final Account account = new Account();
        assertEquals(
                account.getAuthKeyPair().privateKey().size(),
                Ed25519.PRIVATE_KEY_LENGTH
        );
        assertEquals(
                account.getAuthKeyPair().publicKey().size(),
                Ed25519.PUBLIC_KEY_LENGTH
        );
        assertNotNull(account.getAccountNumber());
        assertTrue(Account.isValidAccountNumber(account.getAccountNumber()));
    }

    @ParameterizedTest
    @MethodSource("createSeedAccountNumberPubKeyString")
    public void testNewAccountFromSeed_ValidSeed_ValidAccountIsCreated(
            Seed seed,
            String accountNumber,
            String publicKey
    ) {
        final Account account = Account.fromSeed(seed);
        assertEquals(account.getAccountNumber(), accountNumber);
        assertEquals(
                HEX.encode(account.getAuthKeyPair().publicKey().toBytes()),
                publicKey
        );
    }

    @ParameterizedTest
    @MethodSource("createEncodedSeedAccountNumberPubKeyString")
    public void testNewAccountFromEncodedSeed_ValidSeed_ValidAccountIsCreated(
            String seed,
            String accountNumber,
            String publicKey
    ) {
        final Account account = Account.fromSeed(seed);
        assertEquals(account.getAccountNumber(), accountNumber);
        assertEquals(
                HEX.encode(account.getAuthKeyPair().publicKey().toBytes()),
                publicKey
        );
    }

    @ParameterizedTest
    @MethodSource("createPrivateKeyAccountNumberPubKeyString")
    public void testNewAccountFromPrivateKey_ValidSeed_ValidAccountIsCreated(
            String privateKeyHex,
            String accountNumber,
            String publicKey
    ) {
        PrivateKey privateKey = PrivateKey.from(privateKeyHex);
        final Account account = new Account(privateKey);
        assertEquals(account.getAccountNumber(), accountNumber);
        assertEquals(
                HEX.encode(account.getAuthKeyPair().publicKey().toBytes()),
                publicKey
        );
    }

    @ParameterizedTest
    @MethodSource("createInvalidSeedBecauseOfNetwork")
    public void testNewAccountFromSeed_InvalidSeed_ErrorIsThrow(Seed seed) {
        ValidateException exception = assertThrows(
                ValidateException.class,
                () -> Account.fromSeed(seed)
        );
        assertEquals("Incorrect network from Seed", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("createRecoveryPhraseAccountNumberPublicKey")
    public void testNewAccountFromRecoveryPhrase_ValidRecoveryPhrase_ValidAccountIsCreated(
            String recoveryPhrase, String accountNumber, String publicKey
    ) {
        final Account account = Account.fromRecoveryPhrase(recoveryPhrase.split(
                " "));
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(
                publicKey,
                HEX.encode(account.getAuthKeyPair().publicKey().toBytes())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "% ^",
            "crime cricket castle fun purse announce nephew profit cloth trim delivery",
            "",
            "this is the recovery phrase that has 24 words but not contains the words from bip39 list so it will throw exception $ ^"
    })
    public void testNewAccountFromRecoveryPhrase_InvalidRecoveryPhrase_ErrorIsThrow(
            String recoveryPhrase
    ) {
        assertThrows(
                ValidateException.class,
                () -> Account.fromRecoveryPhrase(recoveryPhrase.split(" "))
        );
    }

    @ParameterizedTest
    @CsvSource({
                       "file earn crack fever crack differ wreck crazy salon imitate swamp sample, 9J878SbnM2GFqAELkkiZbqHJDkAj57fYK",
                       "during kingdom crew atom practice brisk weird document eager artwork ride then, 9J877LVjhr3Xxd2nGzRVRVNUZpSKJF4TH",
                       "accident syrup inquiry you clutch liquid fame upset joke glow best school repeat birth library combine access camera organ trial crazy jeans lizard science, 5XEECt18HGBGNET1PpxLhy5CsCLG9jnmM6Q8QGF4U2yGb1DABXZsVeD",
                       "abuse tooth riot whale dance dawn armor patch tube sugar edit clean guilt person lake height tilt wall prosper episode produce spy artist account, 5XEECsXPYA9wDVXMtRMAVrtaWx7WSc5tG2hqj6b8iiz9rARjg2BgA9w"
               })
    public void testGetSeedFromExistedAccount_ValidAccount_CorrectSeedIsReturn(
            String recoveryPhrase, String expectedSeed
    ) {
        final Account account = Account.fromRecoveryPhrase(recoveryPhrase.split(
                " "));
        final String encodedSeed = account.getSeed().getEncodedSeed();
        assertEquals(expectedSeed, encodedSeed);
    }

    @ParameterizedTest
    @MethodSource("createSeedRecoveryPhrase")
    public void testGetRecoveryPhrase_NoCondition_CorrectRecoveryPhraseIsReturn(
            Seed seed,
            String expectedRecoveryPhrase
    ) {
        final Account account = Account.fromSeed(seed);
        assertTrue(Arrays.equals(
                expectedRecoveryPhrase.split(" "),
                account.getRecoveryPhrase().getMnemonicWords()
        ));
    }

    @ParameterizedTest
    @MethodSource("createValidEncodedSeedLocaleRecoveryPhrase")
    public void testGetRecoveryPhraseWithLocale_NoCondition_CorrectRecoveryPhraseIsReturn(
            Seed seed, Locale locale, String expectedRecoveryPhrase
    ) {
        final Account account = Account.fromSeed(seed);
        assertTrue(Arrays.equals(
                expectedRecoveryPhrase.split(" "),
                account.getRecoveryPhrase(locale).getMnemonicWords()
        ));
    }

    @ParameterizedTest
    @CsvSource({
                       "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva, true",
                       "ec6yMcJATX6gjNwvqp8rbc4jNEasoUgbfBBGGyV5NvoJ54NXva, true",
                       "ec6yMcJATX6gjNwvqpBNbc4jNEasoUgbfBBGGMM5NvoJ54NXva, false"
               })
    public void testCheckValidAccountNumber_NoCondition_CorrectResultIsReturn(
            String accountNumber,
            boolean expectedResult
    ) {
        assertEquals(
                Account.isValidAccountNumber(accountNumber),
                expectedResult
        );
    }

    @RepeatedTest(3)
    public void testSignVerify() {
        final byte[] message = Random.randomBytes(32);
        final Account account1 = new Account();
        final Account account2 = new Account();

        // test sign
        byte[] signature = assertDoesNotThrow(() -> account1.sign(message));
        assertNotNull(signature);
        assertTrue(signature.length > 0);

        // test verify
        boolean verified = assertDoesNotThrow(() -> Account.verify(
                account1.getAccountNumber(),
                signature,
                message
        ));
        assertTrue(verified);

        verified = assertDoesNotThrow(() -> Account.verify(
                account2.getAccountNumber(),
                signature,
                message
        ));
        assertFalse(verified);

    }

}
