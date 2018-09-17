package test.utils;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hieu Pham
 * @since 9/5/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

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
        throw new FileNotFoundException("File with path " + file.getAbsolutePath() + " is not " +
                "found");
    }

    public static String loadResponse(String name) throws IOException {
        String json = load(new File(FileUtils.class.getResource("/response" + name).getFile()));
        Matcher matcher = Pattern.compile("[^\\s\"]+|\"[^\"]*\"").matcher(json);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(matcher.group(0));
        }
        return builder.toString();
    }

    public static String loadRequest(String name) throws IOException {
        String json = load(new File(FileUtils.class.getResource("/request" + name).getFile()));
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
}
