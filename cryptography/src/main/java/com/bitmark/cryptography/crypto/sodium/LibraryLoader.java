/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.cryptography.crypto.sodium;

import android.annotation.SuppressLint;
import com.bitmark.cryptography.error.LibraryLoaderException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class LibraryLoader {

    private static final String SODIUM_LIBRARY = "sodiumjni";

    public enum CPU {
        /**
         * 32 bit legacy Intel
         */
        X86,

        /**
         * 64 bit AMD (aka EM64T/X64)
         */
        X86_64,

        /**
         * 32 bit ARM
         */
        ARM,

        /**
         * 64 bit ARM
         */
        AARCH64,

        /**
         * Unknown CPU architecture.  A best effort will be made to infer architecture
         * specific values such as address and long size.
         */
        UNKNOWN;

        /**
         * Returns a {@code String} object representing this {@code CPU} object.
         *
         * @return the name of the cpu architecture as a lower case {@code String}.
         */
        @Override
        public String toString() {
            return name().toLowerCase();
        }

        static CPU getCPU() {
            String name = System.getProperty("os.arch");
            if (name == null) {
                throw new LibraryLoaderException("Un-support CPU");
            }
            if (name.equalsIgnoreCase("x86") || name.equalsIgnoreCase("i386") || name
                    .equalsIgnoreCase("i486") || name.equalsIgnoreCase("i586") || name
                    .equalsIgnoreCase("i686") || name.equalsIgnoreCase("i786") || name
                    .equalsIgnoreCase("i86pc")) {
                return X86;
            } else if (name.equalsIgnoreCase("x86_64") || name.equalsIgnoreCase(
                    "amd64")) {
                return X86_64;
            } else if (name.equalsIgnoreCase("aarch64") || name.contains("armv8")) {
                return AARCH64;
            } else if (name.contains("arm")) {
                return ARM;
            } else {
                return UNKNOWN;
            }
        }
    }

    public enum OS {
        /**
         * MacOSX
         */
        DARWIN,
        /**
         * Linux
         */
        LINUX,
        /**
         * Android
         */
        ANDROID,
        /**
         * The evil borg operating system
         */
        WINDOWS,
        /**
         * No idea what the operating system is
         */
        UNKNOWN;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        static OS getOS() {
            String osName = System.getProperty("os.name").split(" ")[0];

            if (osName.equalsIgnoreCase("mac") || osName.equalsIgnoreCase(
                    "darwin")) {
                return OS.DARWIN;
            } else if (osName.equalsIgnoreCase("linux")) {
                String javaVM = System.getProperty("java.vm.name");
                String javaVendor = System.getProperty("java.vendor");

                if (javaVendor.contains("Android") || javaVM.equalsIgnoreCase(
                        "dalvik")
                        || javaVM.equalsIgnoreCase("art")) {
                    return OS.ANDROID;
                } else {
                    return OS.LINUX;
                }
            } else if (osName.equalsIgnoreCase("windows")) {
                return OS.WINDOWS;
            } else {
                return OS.UNKNOWN;
            }
        }
    }

    static void load() {
        try {
            loadNativeLib();
        } catch (IOException e) {
            throw new LibraryLoaderException("Not support platform " + e.getMessage());
        }
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private static void loadNativeLib() throws IOException {
        InputStream inputStream = getLibStream();
        if (inputStream == null) {
            throw new LibraryLoaderException("Cannot get native library");
        }
        FileOutputStream outputStream = null;
        File tempFile = null;
        try {
            tempFile = File.createTempFile(
                    "native-lib",
                    "." + getLibExtension()
            );
            tempFile.deleteOnExit();
            outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024 * 8];
            int byteRead;
            while ((byteRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }

            outputStream.close();
            outputStream = null;

            try {
                System.load(tempFile.getAbsolutePath());
                tempFile.delete();
            } catch (Throwable e) {
                System.loadLibrary(SODIUM_LIBRARY);
            }
        } catch (IOException e) {
            throw new IOException("Failed to create temporary file with " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                inputStream.close();
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static InputStream getLibStream() {
        String stubPath = getLibPath() + "/" + getLibName();
        String[] paths = {stubPath, "/" + stubPath};

        for (String path : paths) {
            InputStream stream = getResourceAsStream(path);
            if (stream != null) {
                return stream;
            }
        }

        throw new LibraryLoaderException(
                "Couldn't load native library with path " + Arrays.deepToString(
                        paths));
    }

    private static InputStream getResourceAsStream(String name) {
        ClassLoader[] loaders = new ClassLoader[]{
                ClassLoader.getSystemClassLoader(),
                LibraryLoader.class.getClassLoader(),
                Thread.currentThread().getContextClassLoader()
        };
        for (ClassLoader loader : loaders) {
            InputStream stream = loader.getResourceAsStream(name);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

    private static String getLibPath() {
        CPU cpu = CPU.getCPU();
        OS os = OS.getOS();
        if (cpu == CPU.UNKNOWN || os == OS.UNKNOWN) {
            throw new LibraryLoaderException("Not support platform");
        }
        if (os == OS.DARWIN || os == OS.LINUX) {
            return "lib";
        } else if (os == OS.ANDROID) {
            switch (cpu) {
                case ARM:
                    return "lib/armeabi-v7a";
                case AARCH64:
                    return "lib/arm64-v8a";
                case X86:
                    return "lib/x86";
                case X86_64:
                    return "lib/x86_64";
                default:
                    throw new LibraryLoaderException(
                            "Unknown or does not support CPU architecture");
            }
        } else {
            throw new LibraryLoaderException("Unknown or does not support OS");
        }
    }

    private static String getLibName() {
        switch (OS.getOS()) {
            case WINDOWS:
                return "sodiumjni.dll";
            case DARWIN:
                return "libsodiumjni.dylib";
            case LINUX:
                return "libsodiumjni.so";
            case ANDROID:
                return "libsodiumjni.so";
            default:
                throw new LibraryLoaderException(
                        "Unknown or does not support OS");
        }
    }

    private static String getLibExtension() {
        switch (OS.getOS()) {
            case WINDOWS:
                return "dll";
            case ANDROID:
                return "so";
            case LINUX:
                return "so";
            case DARWIN:
                return "dylib";
            default:
                throw new LibraryLoaderException(
                        "Unknown or does not support OS");
        }
    }

}
