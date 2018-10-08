package apiservice.params;

import apiservice.utils.Address;
import apiservice.utils.error.UnexpectedException;
import cryptography.crypto.Sha3512;
import cryptography.crypto.encoder.VarInt;
import cryptography.error.ValidateException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static apiservice.utils.BinaryPacking.concat;
import static apiservice.utils.FileUtils.getBytes;
import static cryptography.crypto.encoder.Hex.HEX;
import static cryptography.utils.Validator.checkValid;

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

    public RegistrationParams(String name, Map<String, String> metadata, Address registrant) throws ValidateException {
        checkValid(() -> name != null && metadata != null && registrant != null && !name.isEmpty() && metadata.size() > 0 && registrant.isValid(), "Invalid RegistrationParams");
        this.name = name;
        this.metadata = metadata;
        this.registrant = registrant;
    }

    public String generateFingerprint(File file) {
        checkValid(() -> file != null, "Invalid file. File is null");
        CompletableFuture<String> task = CompletableFuture.supplyAsync(() -> {
            try {
                return fingerprint = computeFingerprint(file);
            } catch (IOException e) {
                throw new CompletionException(new UnexpectedException("Error when trying compute " +
                        "fingerprint from file " + file.getAbsolutePath()));
            }
        });
        return task.join();
    }

    @Override
    public String toJson() {
        checkSigned();
        return "{\"assets\":[{\"fingerprint\":\"" + fingerprint + "\",\"name\":\"" + name + "\"," +
                "\"metadata\":\"" + getJsonMetadata(metadata) + "\",\"registrant\":\"" + registrant.getAddress() +
                "\",\"signature\":\"" + HEX.encode(signature) + "\"}]}";
    }

    @Override
    byte[] pack() {
        byte[] data = VarInt.writeUnsignedVarInt(0x02);
        data = concat(name, data);
        data = concat(fingerprint, data);
        data = concat(getPackedMetadata(metadata), data);
        data = concat(registrant.pack(), data);
        return data;
    }

    private String computeFingerprint(File file) throws IOException {
        final byte[] bytes = getBytes(file);
        final byte[] hashedBytes = Sha3512.hash(bytes);
        return "01" + HEX.encode(hashedBytes);
    }

    private String getPackedMetadata(Map<String, String> metadata) {
        StringBuilder builder = new StringBuilder();
        int iteration = 0;
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            iteration++;
            builder.append(entry.getKey()).append('\u0000').append(entry.getValue());
            if (iteration < metadata.size()) builder.append('\u0000');

        }
        return builder.toString();
    }

    private String getJsonMetadata(Map<String, String> metadata) {
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
