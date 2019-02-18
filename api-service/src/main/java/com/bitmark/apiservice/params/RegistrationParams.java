package com.bitmark.apiservice.params;

import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.Awaitility;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.apiservice.utils.FileUtils;
import com.bitmark.apiservice.utils.error.UnexpectedException;
import com.bitmark.cryptography.crypto.Sha3512;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.error.ValidateException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;

/**
 * @author Hieu Pham
 * @since 8/29/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class RegistrationParams extends AbsSingleParams {

    private String name;

    private Map<String, String> metadata;

    private String fingerprint;

    private Address registrant;

    public RegistrationParams(String name, Map<String, String> metadata, Address registrant)
            throws ValidateException {
        checkValid(
                () -> name != null && registrant != null && !name.isEmpty() && registrant.isValid(),
                "Invalid RegistrationParams");
        this.name = name;
        this.metadata = metadata;
        this.registrant = registrant;
    }

    public String generateFingerprint(File file) {
        checkValid(() -> file != null && !file.isDirectory(), "Invalid file");
        try {
            fingerprint = Awaitility.await(() -> {
                try {
                    return computeFingerprint(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new UnexpectedException(e);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new UnexpectedException(throwable);
        }
        if (fingerprint == null) throw new UnexpectedException("Cannot generate fingerprint");
        return fingerprint;
    }

    public Address getRegistrant() {
        return registrant;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Override
    public String toJson() {
        checkSigned();
        return "{\"assets\":[{\"fingerprint\":\"" + fingerprint + "\",\"name\":\"" + name + "\"," +
               "\"metadata\":\"" + (metadata != null ? getJsonMetadata(metadata) : "") +
               "\",\"registrant\":\"" +
               registrant.getAddress() +
               "\",\"signature\":\"" + HEX.encode(signature) + "\"}]}";
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(0x02);
        data = BinaryPacking.concat(name, data);
        data = BinaryPacking.concat(fingerprint, data);
        data = BinaryPacking.concat(getPackedMetadata(metadata), data);
        data = BinaryPacking.concat(registrant.pack(), data);
        return data;
    }

    public static String computeFingerprint(File file) throws IOException {
        final byte[] bytes = FileUtils.getBytes(file);
        final byte[] hashedBytes = Sha3512.hash(bytes);
        return "01" + HEX.encode(hashedBytes);
    }

    public static String getPackedMetadata(Map<String, String> metadata) {
        if (metadata == null) return "";
        StringBuilder builder = new StringBuilder();
        int iteration = 0;
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            iteration++;
            builder.append(entry.getKey()).append('\u0000').append(entry.getValue());
            if (iteration < metadata.size()) builder.append('\u0000');

        }
        return builder.toString();
    }

    public static String getJsonMetadata(Map<String, String> metadata) {
        StringBuilder builder = new StringBuilder();
        int iteration = 0;
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            iteration++;
            builder.append(entry.getKey()).append("\\u0000").append(entry.getValue());
            if (iteration < metadata.size()) builder.append("\\u0000");

        }
        return builder.toString();
    }
}
