package com.bitmark.sdk.test.integrationtest.features;

import com.bitmark.apiservice.utils.Pair;
import com.bitmark.apiservice.utils.callback.Callable1;
import com.bitmark.cryptography.error.ValidateException;
import org.junit.Test;
import com.bitmark.sdk.features.Account;
import com.bitmark.sdk.features.Migration;

import java.util.ArrayList;
import java.util.List;

import static com.bitmark.apiservice.utils.Awaitility.await;
import static org.junit.Assert.*;
import static com.bitmark.sdk.utils.Version.TWELVE;

/**
 * @author Hieu Pham
 * @since 11/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class MigrationTest extends BaseFeatureTest {

    private static final long TIMEOUT = 50000;

    @Test
    public void testMigration_Valid24Words_CorrectValuesReturn() throws Throwable {
        List<String[]> twentyFourWords = new ArrayList<String[]>() {{
            add(("abuse tooth riot whale dance dawn armor patch tube sugar edit clean guilt " +
                    "person" +
                    " lake height tilt wall prosper episode produce spy artist account").split(" "
            ));
            add(("accident syrup inquiry you clutch liquid fame upset joke glow best school " +
                    "repeat" +
                    " birth library combine access camera organ trial crazy jeans lizard science").split(" "));
        }};

        for (String[] words : twentyFourWords) {
            Pair<Account, List<String>> pair = await(callback -> Migration.migrate(words,
                    callback), TIMEOUT);
            Account account = pair.first();
            List<String> bitmarksId = pair.second();
            assertTrue(Account.isValidAccountNumber(account.getAccountNumber()));
            assertEquals(TWELVE.getMnemonicWordsLength(),
                    account.getRecoveryPhrase().getMnemonicWords().length);
            assertNotNull(bitmarksId);
            assertFalse(bitmarksId.isEmpty());
        }

    }

    @Test
    public void testMigration_Invalid24Words_ErrorIsThrow() throws Throwable {

        List<String[]> twentyFourWords = new ArrayList<String[]>() {{
            add(null);
            add(new String[]{});
            add(new String[24]);
            add(("during kingdom crew atom practice brisk weird document " +
                    "eager artwork ride then").split(" "));
        }};
        for (String[] words : twentyFourWords) {
            expectedException.expect(ValidateException.class);
            await((Callable1<Pair<Account, List<String>>>) callback -> Migration.migrate(words,
                    callback), TIMEOUT);
        }

    }


}
