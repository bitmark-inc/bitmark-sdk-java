package cryptography.utils;

import java.util.regex.Pattern;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Matcher {

    private static final String HEX_REGEX = "^[a-fA-F0-9]+$";

    public static boolean isHex(String input) {
        return Pattern.compile(HEX_REGEX).matcher(input).matches() && input.length() % 2 == 0;
    }
}
