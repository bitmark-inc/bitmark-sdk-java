package sdk.test.utils.extensions;

import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static cryptography.crypto.encoder.Raw.RAW;
import static sdk.test.utils.CommonUtils.randomString;

/**
 * @author Hieu Pham
 * @since 11/1/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class TemporaryFolderRule extends TemporaryFolder {

    public File newFile(String content) throws IOException {
        File file = File.createTempFile("AndroidSDK_Test", ".txt", getRoot());
        writeFile(file, content);
        return file;
    }

    @Override
    public File newFile() throws IOException {
        File file = File.createTempFile("AndroidSDK_Test", ".txt", getRoot());
        writeFile(file, randomString(1024));
        return file;
    }

    private void writeFile(File file, String content) throws IOException {
        final byte[] data = RAW.decode(content);
        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(data);
        } finally {
            stream.close();
        }
    }
}
