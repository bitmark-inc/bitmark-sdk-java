package crypto.encoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Hieu Pham
 * @since 8/23/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class Raw implements Encoder {

    public static final Raw RAW = new Raw();

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private Raw() {
    }

    @Override
    public byte[] decode(final String data) {
        return data != null ? data.getBytes(CHARSET) : null;
    }

    @Override
    public String encode(byte[] data) {
        return data != null ? new String(data, CHARSET) : null;
    }
}
