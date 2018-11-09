package com.bitmark.sdk.utils;

import com.bitmark.cryptography.error.ValidateException;

import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;

/**
 * @author Hieu Pham
 * @since 11/2/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public enum Version {

    TWELVE, TWENTY_FOUR;

    public static Version fromCore(byte[] core) throws ValidateException {
        int length = core.length;
        Version version = length == SdkUtils.CORE_LENGTH ? TWELVE : length == 32 ? TWENTY_FOUR :
                null;
        if (version == null) throw new ValidateException("Invalid core length " + length);
        return version;
    }

    public static Version fromMnemonicWords(String... words) throws ValidateException {
        int length = words.length;
        Version version = length == 12 ? TWELVE : length == 24 ? TWENTY_FOUR : null;
        if (version == null) throw new ValidateException("Invalid mnemonic word length " + length);
        return version;
    }

    public static Version fromEncodedSeed(String encodedSeed) throws ValidateException {
        int length = BASE_58.decode(encodedSeed).length;
        Version version = length == 24 ? TWELVE : length == 40 ? TWENTY_FOUR : null;
        if (version == null) throw new ValidateException("Invalid encoded seed length" + length);
        return version;
    }

    public static Version fromEntropy(byte[] entropy) throws ValidateException {
        int length = entropy.length;
        Version version = length == SdkUtils.CORE_LENGTH ? TWELVE : length == 33 ? TWENTY_FOUR :
                null;
        if (version == null) throw new ValidateException("Invalid entropy length " + length);
        return version;
    }

    public static boolean matchesMnemonicWords(String... words) {
        try {
            fromMnemonicWords(words);
            return true;
        } catch (ValidateException e) {
            return false;
        }
    }

    public static boolean matchesCore(byte[] core) {
        try {
            fromCore(core);
            return true;
        } catch (ValidateException e) {
            return false;
        }
    }

    public static boolean matchesEntropy(byte[] entropy) {
        try {
            fromEntropy(entropy);
            return true;
        } catch (ValidateException e) {
            return false;
        }
    }

    public int getCoreLength() {
        return this == TWELVE ? SdkUtils.CORE_LENGTH : 32;
    }

    public int getMnemonicWordsLength() {
        return this == TWELVE ? 12 : 24;
    }

    public int getEncodedSeedLength() {
        return this == TWELVE ? 24 : 40;
    }

    public int getEntropyLength() {
        return this == TWELVE ? 17 : 33;
    }
}
