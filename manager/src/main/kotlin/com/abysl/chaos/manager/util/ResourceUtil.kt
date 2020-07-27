package com.abysl.chaos.manager.util

import com.abysl.chaos.manager.Manager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


object ResourceUtil {
    @Throws(Exception::class)
    fun exportResource(resourceName: String): String? {
        var stream: InputStream? = null
        var resStreamOut: OutputStream? = null
        val jarFolder: String
        try {
            stream =
                Manager::class.java.getResourceAsStream(resourceName) //note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if (stream == null) {
                throw Exception("Cannot get resource \"$resourceName\" from Jar file.")
            }
            var readBytes: Int
            val buffer = ByteArray(4096)
            jarFolder = File(
                Manager::class.java.protectionDomain.codeSource.location.toURI().path + "\\"
            ).parentFile.path.replace('\\', '/')
            println(jarFolder + resourceName)
            resStreamOut = FileOutputStream(jarFolder + resourceName)
            while (stream.read(buffer).also { readBytes = it } > 0) {
                resStreamOut.write(buffer, 0, readBytes)
            }
        } catch (ex: Exception) {
            throw ex
        } finally {
            stream?.close()
            resStreamOut?.close()
        }
        return jarFolder + resourceName
    }
}