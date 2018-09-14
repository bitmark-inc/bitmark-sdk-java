package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static crypto.encoder.Raw.RAW;

/**
 * @author Hieu Pham
 * @since 9/13/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TestFileUtils {

    private TestFileUtils() {
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
