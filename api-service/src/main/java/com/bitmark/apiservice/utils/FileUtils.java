package com.bitmark.apiservice.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Hieu Pham
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class FileUtils {

    private FileUtils() {
    }

    public static byte[] getBytes(File file) throws IOException {
        if (!file.exists())
            throw new FileNotFoundException("File with path " + file.getAbsolutePath() + " is not" +
                    " found");
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
