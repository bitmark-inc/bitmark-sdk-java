/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.params;

import com.bitmark.apiservice.configuration.GlobalConfiguration;
import com.bitmark.apiservice.utils.Address;
import com.bitmark.apiservice.utils.ArrayUtil;
import com.bitmark.apiservice.utils.BinaryPacking;
import com.bitmark.apiservice.utils.FileUtils;
import com.bitmark.cryptography.crypto.MerkleTree;
import com.bitmark.cryptography.crypto.Sha3256;
import com.bitmark.cryptography.crypto.Sha3512;
import com.bitmark.cryptography.crypto.encoder.VarInt;
import com.bitmark.cryptography.crypto.key.KeyPair;
import com.bitmark.cryptography.utils.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import static com.bitmark.cryptography.crypto.encoder.Hex.HEX;
import static com.bitmark.cryptography.utils.Validator.checkValid;

public class RegistrationParams extends AbsSingleParams {

    private static final int ASSET_NAME_MAX_LENGTH = 64;

    private static final int METADATA_MAX_LENGTH = 2048;

    private String name;

    private Map<String, String> metadata;

    private String fingerprint;

    private Address registrant;

    public RegistrationParams(String name, Map<String, String> metadata) {
        checkValid(
                () -> name == null || name.length() <= ASSET_NAME_MAX_LENGTH,
                String.format(
                        "asset name is invalid, must be maximum of %s or abort",
                        ASSET_NAME_MAX_LENGTH
                )
        );
        checkValid(
                () -> getJsonMetadata(metadata).length() <= METADATA_MAX_LENGTH,
                String.format(
                        "metadata is too long, must be maximum of %s",
                        METADATA_MAX_LENGTH
                )
        );

        this.name = name == null ? "" : name;
        this.metadata = metadata;
    }

    public static String computeFingerprint(byte[] data) {
        checkValid(() -> data != null, "invalid data");
        final byte[] hashedBytes = Sha3512.hash(data);
        return "01" + HEX.encode(hashedBytes);
    }

    // should be bring out of main thread due to the heavy tasks
    public static String computeFingerprint(byte[][] data) {
        checkValid(
                () -> data != null && data.length > 0,
                "data must be non-null and not empty"
        );
        int length = data.length;
        byte[][] hashes = new byte[length][];
        for (int i = 0; i < length; i++) {
            hashes[i] = Sha3512.hash(data[i]);
        }

        // sort hash array in ascending order
        Arrays.sort(hashes, ArrayUtil::lexicographicallyCompare);

        byte[][] merkleTree = MerkleTree.buildTree(
                hashes,
                (b1, b2) -> {
                    byte[] b = ArrayUtils.concat(b1, b2);
                    return length == 256 ? Sha3256.hash(b) : Sha3512.hash(b);
                }
        );

        if (merkleTree.length == 0) {
            throw new IllegalStateException("could not build merkle root");
        }

        byte[] root = merkleTree[merkleTree.length - 1];
        return "02" + Base64.getEncoder().encodeToString(root);
    }

    public static String getPackedMetadata(Map<String, String> metadata) {
        if (metadata == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int iteration = 0;
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            iteration++;
            builder.append(entry.getKey())
                    .append('\u0000')
                    .append(entry.getValue());
            if (iteration < metadata.size()) {
                builder.append('\u0000');
            }

        }
        return builder.toString();
    }

    public static String getJsonMetadata(Map<String, String> metadata) {
        if (metadata == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int iteration = 0;
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            iteration++;
            builder.append(entry.getKey())
                    .append("\\u0000")
                    .append(entry.getValue());
            if (iteration < metadata.size()) {
                builder.append("\\u0000");
            }

        }
        return builder.toString();
    }

    // should be bring out of main thread due to the heavy tasks
    public String setFingerprintFromFile(File file) throws IOException {
        checkValid(
                () -> file != null && file.exists() && !file.isDirectory(),
                "invalid file"
        );
        byte[] data = FileUtils.getBytes(file);
        return fingerprint = computeFingerprint(data);
    }

    // should be bring out of main thread due to the heavy tasks
    public String setFingerprintFromFiles(File[] files) throws IOException {
        checkValid(
                () -> files != null && files.length > 0,
                "files must be non-null and not empty"
        );
        for (File f : files) {
            checkValid(
                    () -> f != null && f.exists() && !f.isDirectory(),
                    "invalid file at " + f.getAbsolutePath()
            );
        }
        int length = files.length;
        byte[][] data = new byte[length][];
        for (int i = 0; i < files.length; i++) {
            data[i] = FileUtils.getBytes(files[i]);
        }

        return setFingerprintFromData(data);
    }

    // should be bring out of main thread due to the heavy tasks
    public String setFingerprintFromData(byte[][] data) {
        return fingerprint = computeFingerprint(data);
    }

    public String setFingerprintFromData(byte[] data) {
        return fingerprint = computeFingerprint(data);
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
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

    public String getFingerprint() {
        return fingerprint;
    }

    @Override
    public byte[] sign(KeyPair key) {
        registrant = Address.getDefault(
                key.publicKey(),
                GlobalConfiguration.network()
        );
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
}
