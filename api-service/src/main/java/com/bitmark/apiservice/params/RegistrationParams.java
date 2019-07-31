package com.bitmark.apiservice.params;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.apiservice.utils.FileUtils;
import com.bitmark.cryptography.crypto.Sha3512;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.KeyPair;
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

    private static final int ASSET_NAME_MAX_LENGTH = 64;

    private static final int METADATA_MAX_LENGTH = 2048;

    private String name;

    private Map<String, String> metadata;

    private String fingerprint;

    private Address registrant;

    public RegistrationParams(String name, Map<String, String> metadata)
            throws ValidateException {
        checkValid(
                () -> name == null || name.length() <= ASSET_NAME_MAX_LENGTH,
                String.format("asset name is invalid, must be maximum of %s or abort",
                              ASSET_NAME_MAX_LENGTH));
        checkValid(
                () -> getJsonMetadata(metadata).length() <= METADATA_MAX_LENGTH,
                String.format("metadata is too long, must be maximum of %s", METADATA_MAX_LENGTH));

        this.name = name == null ? "" : name;
        this.metadata = metadata;
    }

    public String setFingerprintFromFile(File file) throws IOException {
        checkValid(() -> file != null && file.exists() && !file.isDirectory(), "invalid file");
        byte[] data = FileUtils.getBytes(file);
        return fingerprint = computeFingerprint(data);
    }

    public String setFingerprintFromData(byte[] data) {
        checkValid(() -> data != null, "invalid data");
        return fingerprint = computeFingerprint(data);
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
    public byte[] sign(KeyPair key) {
        registrant = Address.getDefault(key.publicKey(), GlobalConfiguration.network());
        checkValid(() -> null != fingerprint, "missing fingerprint");
        return super.sign(key);
    }

    @Override
    public String toJson() {
        checkSigned();
        return "{\"assets\":[{\"fingerprint\":\"" + fingerprint + "\",\"name\":\"" + name + "\"," +
               "\"metadata\":\"" + getJsonMetadata(metadata) +
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

    public static String computeFingerprint(byte[] data) {
        final byte[] hashedBytes = Sha3512.hash(data);
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
        if (metadata == null) return "";
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
