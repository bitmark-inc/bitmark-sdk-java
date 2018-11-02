package sdk.utils;

import apiservice.configuration.Network;
import cryptography.crypto.Sha3256;
import cryptography.error.ValidateException;
import sdk.utils.error.InvalidChecksumException;

import java.util.Arrays;

import static apiservice.utils.ArrayUtil.concat;
import static apiservice.utils.ArrayUtil.slice;
import static cryptography.crypto.encoder.Base58.BASE_58;
import static cryptography.utils.Validator.checkValid;
import static cryptography.utils.Validator.checkValidLength;
import static sdk.utils.SdkUtils.extractNetwork;

/**
 * @author Hieu Pham
 * @since 8/24/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Seed {

    public static final int SEED_LENGTH = 17;

    private static final byte[] HEADERS = new byte[]{0x5A, (byte) 0xFE, 0x02};

    private static final int CHECKSUM_LENGTH = 4;

    private static final int ENCODED_LENGTH = SEED_LENGTH + HEADERS.length + CHECKSUM_LENGTH;

    private byte[] seed;

    private Network network;

    public static Seed fromEncodedSeed(String encodedSeed) throws ValidateException {
        final byte[] seedBytes = BASE_58.decode(encodedSeed);
        checkValidLength(seedBytes, ENCODED_LENGTH);

        // Checksum is last 4 bits
        final int length = seedBytes.length;
        final byte[] checksum = slice(seedBytes,
                length - CHECKSUM_LENGTH, length);

        // Bytes not contains checksum
        final byte[] data = Arrays.copyOfRange(seedBytes, 0,
                length - CHECKSUM_LENGTH);

        // Verify checksum
        final byte[] dataHashed = Sha3256.hash(data);
        final byte[] checksumVerification = slice(dataHashed, 0, CHECKSUM_LENGTH);
        checkValid(() -> Arrays.equals(checksum, checksumVerification),
                new InvalidChecksumException(checksumVerification, checksum));

        // Get seed

        final byte[] seed = slice(data, HEADERS.length, data.length);
        checkValidLength(seed, SEED_LENGTH);
        return new Seed(seed);
    }

    public Seed(byte[] seed) throws ValidateException {
        checkValid(() -> seed != null && seed.length == SEED_LENGTH);
        this.seed = seed;
        this.network = extractNetwork(seed);
    }

    public Seed(byte[] seed, Network network) throws ValidateException {
        checkValid(() -> seed != null && seed.length == SEED_LENGTH && network != null);
        if (extractNetwork(seed) != network) throw new ValidateException("Invalid network");
        this.seed = seed;
        this.network = network;
    }

    public String getEncodedSeed() {
        final byte[] data = concat(HEADERS, seed);
        final byte[] checksum = slice(Sha3256.hash(data), 0, CHECKSUM_LENGTH);
        final byte[] seed = concat(data, checksum);
        return BASE_58.encode(seed);
    }

    public byte[] getSeed() {
        return seed;
    }

    public Network getNetwork() {
        return network;
    }
}
