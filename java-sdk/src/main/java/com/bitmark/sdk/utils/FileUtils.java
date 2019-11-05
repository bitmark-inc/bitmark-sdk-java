/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils;

import com.bitmark.sdk.features.internal.RecoveryPhrase;

import java.io.*;

public class FileUtils {

    private FileUtils() {
    }

    public static InputStream getResourceAsStream(String name)
            throws IOException {
        ClassLoader[] loaders = new ClassLoader[]{
                ClassLoader.getSystemClassLoader(),
                RecoveryPhrase.class.getClassLoader(),
                Thread.currentThread().getContextClassLoader()
        };
        String[] paths = new String[]{name, "/" + name};
        for (ClassLoader loader : loaders) {
            InputStream stream = null;
            for (String path : paths) {
                stream = loader.getResourceAsStream(path);
                if (stream != null) {
                    break;
                }
            }
            if (stream != null) {
                return stream;
            }
        }
        throw new IOException("File with path " + name + " is not found");
    }

    /**
     * Get resource as a {@link File} by a file name from temporary folder
     *
     * @param name the name of the resource file with format path/to/file/name.extension
     * @return a {@link File} associate with resource name
     * @throws IOException if file not found
     */
    public static File getResourceAsFile(String name) throws IOException {
        final String suffix = name.substring(name.lastIndexOf("."));
        final String prefix = name.substring(
                name.lastIndexOf("/") + 1,
                name.lastIndexOf(".")
        );
        final String fileName = prefix + suffix;

        File tmpFile = new File(getTmpDir() + "/" + fileName);
        if (!tmpFile.exists()) {
            OutputStream os = null;
            try {
                InputStream is = getResourceAsStream(name);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                os = new FileOutputStream(tmpFile);
                os.write(buffer);
            } catch (IOException e) {
                throw e;
            } finally {
                if (os != null) {
                    os.close();
                }
            }

        }
        return tmpFile;
    }

    private static String getTmpDir() {
        return System.getProperty("java.io.tmpdir");
    }
}
