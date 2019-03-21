package com.bitmark.sdk.test.integrationtest.features;

import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.test.integrationtest.BaseTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Migration;

import java.util.List;
import java.util.stream.Stream;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static com.bitmark.sdk.features.internal.Version.TWELVE;

/**
 * @author Hieu Pham
 * @since 11/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class MigrationTest extends BaseTest {

    private static final long TIMEOUT = 50000;

    @ParameterizedTest
    @MethodSource("createValid24Words")
    public void testMigration_Valid24Words_CorrectValuesReturn(String[] twentyFourWords)
            throws Throwable {
        Pair<Account, List<String>> pair = await(callback -> Migration.migrate(twentyFourWords,
                                                                               callback), TIMEOUT);
        Account account = pair.first();
        List<String> bitmarksId = pair.second();
        assertTrue(Account.isValidAccountNumber(account.getAccountNumber()));
        assertEquals(TWELVE.getMnemonicWordsLength(),
                     account.getRecoveryPhrase().getMnemonicWords().length);
        assertNotNull(bitmarksId);
        assertFalse(bitmarksId.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("createInvalid24Words")
    public void testMigration_Invalid24Words_ErrorIsThrow(String[] words) {
        assertThrows(ValidateException.class,
                     () -> await((Callable1<Pair<Account, List<String>>>) callback -> Migration
                             .migrate(words, callback)));
    }

    private static Stream<Arguments> createValid24Words() {
        return Stream.of(Arguments.of((Object) ("abuse tooth riot whale dance dawn armor patch " +
                                                "tube sugar " +
                                                "edit clean guilt person lake height tilt wall prosper episode produce " +
                                                "spy artist account").split(" ")),
                         Arguments.of((Object) (
                                 "accident syrup inquiry you clutch liquid fame upset joke " +
                                 "glow best" +
                                 " school repeat birth library combine access camera organ trial crazy " +
                                 "jeans lizard science").split(" ")));
    }

    private static Stream<Arguments> createInvalid24Words() {
        return Stream.of(Arguments.of((Object) new String[]{}),
                         Arguments.of((Object) new String[24]),
                         Arguments.of((Object) ("abuse tooth riot " +
                                                "whale dance dawn " +
                                                "armor patch tube sugar edit clean guilt person lake height tilt wall " +
                                                "prosper episode produce spy artist accountant")
                                 .split(" ")),
                         Arguments.of((Object) (
                                 "during kingdom crew atom practice brisk weird document " +
                                 "eager artwork ride then").split(" ")));
    }


}
