package com.bitmark.sdk.features.internal;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.configuration.Network;
import com.bitmark.apiservice.utils.error.InvalidNetworkException;
import com.bitmark.cryptography.crypto.Box;
import com.bitmark.cryptography.crypto.Ed25519;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.error.ValidateException;
import com.bitmark.sdk.utils.SequenceIterateByteArray;
import com.bitmark.sdk.utils.error.InvalidChecksumException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bitmark.apiservice.utils.ArrayUtil.concat;
import static com.bitmark.apiservice.utils.ArrayUtil.slice;
import static com.bitmark.cryptography.crypto.Random.secureRandomBytes;
import static com.bitmark.cryptography.crypto.encoder.Base58.BASE_58;
import static com.bitmark.cryptography.utils.Validator.checkValid;
import static com.bitmark.cryptography.utils.Validator.checkValidLength;

/**
 * @author Hieu Pham
 * @since 1/4/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */
public class SeedTwelve extends AbsSeed {

    public static int SEED_BYTE_LENGTH = 17;

    public static int ENCODED_SEED_LENGTH = 24;

    public static byte[] HEADER = {0x5A, (byte) 0xFE, 0x02};

    public SeedTwelve() {
        this(randomEntropy(GlobalConfiguration.network()));
    }

    public SeedTwelve(byte[] seedBytes) throws ValidateException {
        super(seedBytes);
    }

    public static Seed fromEncodedSeed(String encodedSeed)
            throws ValidateException {
        final byte[] seedBytes = BASE_58.decode(encodedSeed);
        checkValidLength(seedBytes, ENCODED_SEED_LENGTH);

        final int seedLength = SEED_BYTE_LENGTH;

        // Checksum is last 4 bits
        final int length = seedBytes.length;
        final byte[] checksum = slice(
                seedBytes,
                length - CHECKSUM_LENGTH,
                length
        );

        // Bytes not contains checksum
        final byte[] data = Arrays.copyOfRange(
                seedBytes,
                0,
                length - CHECKSUM_LENGTH
        );

        // Verify checksum
        final byte[] dataHashed = Sha3256.hash(data);
        final byte[] checksumVerification = slice(
                dataHashed,
                0,
                CHECKSUM_LENGTH
        );
        checkValid(
                () -> Arrays.equals(checksum, checksumVerification),
                new InvalidChecksumException(checksumVerification, checksum)
        );

        // Get seed
        final byte[] seed = slice(data, HEADER.length, data.length);
        checkValidLength(seed, seedLength);
        return new SeedTwelve(seed);
    }

    private static Network extractNetwork(byte[] core)
            throws InvalidNetworkException {
        int mode =
                ((core[0] & 0x80) | (core[1] & 0x40) | (core[2] & 0x20) | (core[3] & 0x10));
        if (mode == (core[15] & 0xF0)) {
            return Network.LIVE_NET;
        } else if (mode == (core[15] & 0xF0 ^ 0xF0)) {
            return Network.TEST_NET;
        } else {
            throw new InvalidNetworkException("Cannot extract network from core");
        }

    }

    /**
     * generate a test/live key: 16.5 bytes <br>
     * 01..08: 76543210 76543210 76543210 76543210 76543210 76543210 76543210 76543210<br>
     * 09..16: 76543210 76543210 76543210 76543210 76543210 76543210 76543210 ffff3210<br>
     * 17:     7654xxxx<br>
     * where:<br>
     * <i>digits 0..7</i> = random data<br>
     * <i>ffff</i>        = the network flag<br>
     * <i>xxxx</i>        = unused and zero<br>
     * network flag derived as:<br>
     * <i>ffff</i>        = (b0.7 | b1.6 | b2.5 | b3.4) ^ (testnet &amp;&amp; 1111)<br>
     *
     * @param network The network value for random entropy
     * @return An array of byte of entropy
     */
    private static byte[] randomEntropy(Network network) {
        // Space for 128 bits random number
        byte[] seed = secureRandomBytes(SEED_BYTE_LENGTH - 1);

        // Extend to 132 bits
        seed = concat(
                seed,
                new byte[]{(byte) (seed[15] & 0xF0)}
        ); // bits 7654xxxx  where x=zero

        byte mode =
                (byte) (seed[0] & 0x80 | seed[1] & 0x40 | seed[2] & 0x20 | seed[3] & 0x10);
        if (network == Network.TEST_NET) {
            mode = (byte) (mode ^ 0xF0);
        }
        seed[15] = (byte) (mode | (seed[15] & 0x0F));
        return seed;
    }

    private static byte[] generateSeedKey(byte[] core)
            throws ValidateException {
        List<byte[]> keys = generateSeedKeys(core, 1);
        if (keys.isEmpty()) {
            throw new ValidateException("Generate seed key failed");
        }
        return keys.get(0);
    }

    private static List<byte[]> generateSeedKeys(byte[] core, int keyCount)
            throws ValidateException {
        checkValid(() -> core != null && core.length > 0 && keyCount > 0);

        // add the seed 4 times to hash value
        byte[] hash = Sha3256.shake(core, 4, keyCount);

        List<byte[]> keys = new ArrayList<>(keyCount);
        SequenceIterateByteArray array = new SequenceIterateByteArray(hash);

        for (int i = 0; i < keyCount; i++) {
            keys.add(array.next(Sha3256.HASH_BYTE_LENGTH));
        }
        return keys;
    }

    @Override
    public String getEncodedSeed() {
        final byte[] data = concat(HEADER, seedBytes);
        final byte[] checksum = slice(Sha3256.hash(data), 0, CHECKSUM_LENGTH);
        final byte[] seed = concat(data, checksum);
        return BASE_58.encode(seed);
    }

    @Override
    public KeyPair getAuthKeyPair() {
        return Ed25519.generateKeyPairFromSeed(generateSeedKey(seedBytes));
    }

    @Override
    public KeyPair getEncKeyPair() {
        final byte[] privateKey = generateSeedKeys(seedBytes, 2).get(1);
        return Box.generateKeyPair(privateKey);
    }

    @Override
    public Version getVersion() {
        return Version.TWELVE;
    }

    @Override
    public int length() {
        return SEED_BYTE_LENGTH;
    }

    @Override
    public Network getNetwork() {
        return extractNetwork(seedBytes);
    }
}
