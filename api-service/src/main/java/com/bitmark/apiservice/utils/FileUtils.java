/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils {

    private FileUtils() {
    }

    public static byte[] getBytes(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File with path " + file.getAbsolutePath() + " is not found");
        }
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream stream = new FileInputStream(file);
        try {
            stream.read(bytes);
        } finally {
            stream.close();
        }
        return bytes;
    }
}
