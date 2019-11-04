/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.test.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;

public class FileUtils {

    private FileUtils() {
    }

    public static File createFile(String absolutePath, String content) throws IOException {
        final byte[] data = RAW.decode(content);
        File file = new File(absolutePath);
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(data);
        stream.close();
        return file;
    }

    public static File createTempFile(String name, Path directory, String content) throws IOException {
        return createFile(new File(directory.toFile(), name).getAbsolutePath(), content);
    }
}
