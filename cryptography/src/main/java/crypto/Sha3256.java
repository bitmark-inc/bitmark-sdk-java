package crypto;

import error.ValidateException;
import org.bouncycastle.jcajce.provider.digest.SHA3;

import static crypto.encoder.Hex.HEX;
import static utils.Validator.*;

/**
 * @author Hieu Pham
 * @since 8/16/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Sha3256 implements Comparable<Sha3256> {

    public static final int HASH_LENGTH = 32;

    private final byte[] bytes;

    private Sha3256(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Sha3256 from(byte[] bytes) throws ValidateException {
        checkValidLength(bytes, HASH_LENGTH);
        return new Sha3256(bytes);
    }

    public static Sha3256 from(String hexHash) throws ValidateException {
        checkValidHex(hexHash);
        return from(HEX.decode(hexHash));
    }

    public static byte[] hash(byte[] input) throws ValidateException {
        return hash(input, 0, input.length);
    }

    public static byte[] hash(byte[] input, int offset, int length) throws ValidateException {
        checkValid(() -> offset >= 0 && length > 0);
        SHA3.DigestSHA3 digest = new SHA3.Digest256();
        digest.update(input, offset, length);
        return digest.digest();
    }

    public static byte[] hash(String hexInput) throws ValidateException {
        checkValidHex(hexInput);
        final byte[] input = HEX.decode(hexInput);
        return hash(input);
    }

    public static byte[] hashTwice(byte[] input) throws ValidateException {
        return hashTwice(input, 0, input.length);
    }

    public static byte[] hashTwice(byte[] input, int offset, int length) throws ValidateException {
        checkValid(() -> offset >= 0 && length > 0);
        SHA3.DigestSHA3 digest = new SHA3.Digest256();
        digest.update(input, offset, length);
        return digest.digest(digest.digest());
    }

    public static byte[] hashTwice(String hexInput) throws ValidateException {
        checkValidHex(hexInput);
        final byte[] input = HEX.decode(hexInput);
        return hashTwice(input);
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return HEX.encode(bytes);
    }

    @Override
    public int compareTo(Sha3256 other) {
        for (int i = HASH_LENGTH - 1; i >= 0; i--) {
            final int thisByte = this.bytes[i] & 0xFF;
            final int otherByte = other.bytes[i] & 0xFF;
            if (thisByte > otherByte)
                return 1;
            if (thisByte < otherByte)
                return -1;
        }
        return 0;
    }
}
