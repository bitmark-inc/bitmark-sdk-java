package service.params;

import utils.callback.Callback0;

import java.io.File;
import java.util.Map;

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

    public RegistrationParams(String name, Map<String, String> metadata) {
        this.name = name;
        this.metadata = metadata;
    }

    public void generateFingerprint(File file, Callback0 callback) {
        throw new UnsupportedOperationException("Unsupport right now");
    }

    @Override
    public String toJson() {
        throw new UnsupportedOperationException("Unsupport right now");
    }

    @Override
    byte[] pack() {
        return new byte[0];
    }
}
