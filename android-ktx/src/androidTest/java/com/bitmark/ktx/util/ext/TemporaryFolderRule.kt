/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.ktx.util.ext

import com.bitmark.cryptography.crypto.encoder.Raw.RAW
import com.bitmark.ktx.util.randomString
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TemporaryFolderRule : TemporaryFolder() {

    @Throws(IOException::class)
    override fun newFile(content: String): File {
        val file = File.createTempFile("Android_KTX_Test", ".txt", root)
        writeFile(file, content)
        return file
    }

    @Throws(IOException::class)
    override fun newFile(): File {
        val file = File.createTempFile("Android_KTX_Test", ".txt", root)
        writeFile(file, randomString(1024))
        return file
    }

    @Throws(IOException::class)
    private fun writeFile(file: File, content: String) {
        val data = RAW.decode(content)
        val stream = FileOutputStream(file)
        stream.use { s ->
            s.write(data)
        }
    }
}