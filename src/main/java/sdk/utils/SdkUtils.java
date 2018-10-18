package sdk.utils;

import apiservice.configuration.Network;
import apiservice.utils.error.InvalidNetworkException;
import cryptography.error.ValidateException;

import java.util.ArrayList;
import java.util.List;

import static apiservice.utils.ArrayUtil.concat;
import static cryptography.crypto.Random.secureRandomBytes;
import static cryptography.crypto.Sha3256.shake;
import static cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 10/17/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class SdkUtils {

    private static final int CORE_LENGTH = 17;

    private SdkUtils() {

    }

    /**
     * generate a test/live key: 16.5 bytes <br>
     * 01..08: 76543210 76543210 76543210 76543210 76543210 76543210 76543210 76543210<br>
     * 09..16: 76543210 76543210 76543210 76543210 76543210 76543210 76543210 ffff3210<br>
     * 17:     7654xxxx<br>
     * where:<br>
     * digits 0..7 = random data<br>
     * ffff        = the network flag<br>
     * xxxx        = unused and zero<br>
     * network flag derived as:<br>
     * ffff        = (b0.7 | b1.6 | b2.5 | b3.4) ^ (testnet & 1111)<br>
     *
     * @return
     */
    public static byte[] randomEntropy(Network network) {
        // Space for 128 bits random number
        byte[] seed = secureRandomBytes(CORE_LENGTH - 1);

        // Extend to 132 bits
        seed = concat(seed, new byte[]{(byte) (seed[15] & 0xF0)}); // bits 7654xxxx  where x=zero

        byte mode =
                (byte) (seed[0] & 0x80 | seed[1] & 0x40 | seed[2] & 0x20 | seed[3] & 0x10);
        if (network == Network.TEST_NET) mode = (byte) (mode ^ 0xF0);
        seed[15] = (byte) (mode | (seed[15] & 0x0F));
        return seed;
    }

    public static Network extractNetwork(byte[] core) throws InvalidNetworkException {
        int mode =
                ((core[0] & 0x80) | (core[1] & 0x40) | (core[2] & 0x20) | (core[3] & 0x10));
        if (mode == (core[15] & 0xF0)) {
            return Network.LIVE_NET;
        } else if (mode == (core[15] & 0xF0 ^ 0xF0)) {
            return Network.TEST_NET;
        } else throw new InvalidNetworkException("Cannot extract network from core");
    }

    public static byte[] generateSeedKey(byte[] core, int keySize) throws ValidateException {
        List<byte[]> keys = generateSeedKeys(core, 1, keySize);
        if (keys.isEmpty()) throw new ValidateException("Generate seed key failed");
        return keys.get(0);
    }

    public static List<byte[]> generateSeedKeys(byte[] core, int keyCount, int keySize) throws ValidateException {
        checkValid(() -> core != null && core.length > 0 && keyCount > 0 && keySize > 0);

        // add the seed 4 times to hash value
        final int length = keySize * 8 * keyCount; // Length in bit count of all keys
        byte[] hash = shake(core, length, 4);

        List<byte[]> keys = new ArrayList<>(keyCount);
        SequenceIterateByteArray array = new SequenceIterateByteArray(hash);

        for (int i = 0; i < keyCount; i++) {
            keys.add(array.next(keySize));
        }
        return keys;
    }
}
