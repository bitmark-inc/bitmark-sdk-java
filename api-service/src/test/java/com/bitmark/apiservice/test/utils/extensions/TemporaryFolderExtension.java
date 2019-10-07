package com.bitmark.apiservice.test.utils.extensions;

import com.bitmark.apiservice.test.utils.extensions.annotations.TemporaryFile;
import org.junit.jupiter.api.extension.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static com.bitmark.apiservice.test.utils.CommonUtils.randomString;
import static com.bitmark.apiservice.test.utils.FileUtils.createTempFile;

/**
 * @author Hieu Pham
 * @since 3/21/19
 * Email: hieupham@bitmark.com
 * Copyright © 2019 Bitmark. All rights reserved.
 */

public class TemporaryFolderExtension
        implements AfterEachCallback, ParameterResolver {

    private static final String KEY = "tempDirectory";

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext
    ) {
        return parameterContext.getParameter().getType() == File.class;
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext context
    ) {
        String content = getFileContent(parameterContext.getParameter());
        return getLocalStore(context).getOrComputeIfAbsent(
                KEY,
                key -> {
                    Path path = createTempDirectory(context);
                    try {
                        return createTempFile(getFileName(), path, content);
                    } catch (IOException e) {
                        throw new RuntimeException(
                                "Error when create temporary file");
                    }
                }
        );
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        File tempDirectory = (File) getLocalStore(context).get(KEY);
        if (tempDirectory != null) {
            delete(tempDirectory);
        }
    }

    private String getFileContent(Parameter parameter) {
        TemporaryFile annotation = parameter.getAnnotation(TemporaryFile.class);
        return annotation != null ? annotation.value() : randomString(1024);
    }

    private String getFileName() {
        return "Java_Api_Service_Test_" + String.valueOf(System.currentTimeMillis()) + ".txt";
    }

    private ExtensionContext.Store getLocalStore(ExtensionContext context) {
        return context.getStore(localNamespace(context));
    }

    private ExtensionContext.Namespace localNamespace(ExtensionContext context) {
        return ExtensionContext.Namespace.create(
                TemporaryFolderExtension.class,
                context
        );
    }

    private Path createTempDirectory(ExtensionContext context) {
        try {
            String tempDirName;
            if (context.getTestMethod().isPresent()) {
                tempDirName = context.getTestMethod().get().getName();
            } else if (context.getTestClass().isPresent()) {
                tempDirName = context.getTestClass().get().getName();
            } else {
                tempDirName = context.getDisplayName();
            }

            return Files.createTempDirectory(tempDirName);
        } catch (IOException e) {
            throw new ParameterResolutionException(
                    "Could not create temp directory",
                    e
            );
        }
    }

    private void delete(File tempDirectory) throws IOException {
        Files.walkFileTree(
                tempDirectory.toPath(),
                new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(
                            Path file,
                            BasicFileAttributes attrs
                    ) throws IOException {
                        return deleteAndContinue(file);
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(
                            Path dir,
                            IOException exc
                    ) throws IOException {
                        return deleteAndContinue(dir);
                    }

                    private FileVisitResult deleteAndContinue(Path path)
                            throws IOException {
                        Files.delete(path);
                        return FileVisitResult.CONTINUE;
                    }
                }
        );
    }
}
