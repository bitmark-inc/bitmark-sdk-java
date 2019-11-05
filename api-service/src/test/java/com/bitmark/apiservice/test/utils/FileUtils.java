/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.test.utils;

import java.io.*;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bitmark.cryptography.crypto.encoder.Raw.RAW;

public class FileUtils {

    private FileUtils() {
    }

    public static String load(File file) throws IOException {
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
        throw new FileNotFoundException("File with path " + file.getAbsolutePath() + " is not found");
    }

    public static String loadResponse(String name) throws IOException {
        String json = load(new File(FileUtils.class.getResource("/response" + name)
                .getFile()));
        Matcher matcher = Pattern.compile("[^\\s\"]+|\"[^\"]*\"").matcher(json);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(matcher.group(0));
        }
        return builder.toString();
    }

    public static String loadRequest(String name) throws IOException {
        String json = load(new File(FileUtils.class.getResource("/request" + name)
                .getFile()));
        Matcher matcher = Pattern.compile("[^\\s\"]+|\"[^\"]*\"").matcher(json);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(matcher.group(0));
        }
        return builder.toString();
    }

    public static File getResourceFile(String name) {
        return new File(FileUtils.class.getResource("/" + name).getFile());
    }

    public static byte[] getResourceAsBytes(String name) throws IOException {
        return getBytes(getResourceFile(name));
    }

    public static File createFile(String absolutePath, String content)
            throws IOException {
        final byte[] data = RAW.decode(content);
        File file = new File(absolutePath);
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(data);
        stream.close();
        return file;
    }

    public static File createTempFile(
            String name,
            Path directory,
            String content
    )
            throws IOException {
        return createFile(
                new File(directory.toFile(), name).getAbsolutePath(),
                content
        );
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
