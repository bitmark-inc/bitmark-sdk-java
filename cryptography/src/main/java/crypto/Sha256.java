package crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static crypto.encoder.Hex.HEX;
import static utils.Validator.checkValidHex;
import static utils.Validator.checkValidLength;

/**
 * @author Hieu Pham
 * @since 8/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Sha256 implements Comparable<Sha256> {

    public static final int HASH_LENGTH = 32;

    private final byte[] bytes;

    private Sha256(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Sha256 from(byte[] bytes) {
        checkValidLength(bytes, HASH_LENGTH);
        return new Sha256(bytes);
    }

    public static Sha256 from(String hexHash) {
        checkValidHex(hexHash);
        return from(HEX.decode(hexHash));
    }

    public static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hash(String hexInput) {
        checkValidHex(hexInput);
        final byte[] rawBytes = HEX.decode(hexInput);
        return hash(rawBytes, 0, rawBytes.length);
    }

    public static byte[] hash(byte[] input) {
        return hash(input, 0, input.length);
    }

    public static byte[] hash(byte[] input, int offset, int length) {
        MessageDigest digest = newDigest();
        digest.update(input, offset, length);
        return digest.digest();
    }

    public static byte[] hashTwice(String hexString) {
        checkValidHex(hexString);
        return hashTwice(HEX.decode(hexString));
    }

    public static byte[] hashTwice(byte[] input) {
        return hashTwice(input, 0, input.length);
    }

    public static byte[] hashTwice(byte[] input, int offset, int length) {
        MessageDigest digest = newDigest();
        digest.update(input, offset, length);
        return digest.digest(digest.digest());
    }

    @Override
    public String toString() {
        return HEX.encode(bytes);
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public int compareTo(Sha256 other) {
        for (int i = HASH_LENGTH - 1; i >= 0; i--) {
            final int thisByte = this.bytes[i] & 0xff;
            final int otherByte = other.bytes[i] & 0xff;
            if (thisByte > otherByte)
                return 1;
            if (thisByte < otherByte)
                return -1;
        }
        return 0;
    }
}
