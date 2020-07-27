package com.abysl.chaos.proxy


import org.bouncycastle.crypto.StreamCipher
import org.bouncycastle.crypto.engines.RC4Engine
import org.bouncycastle.crypto.params.KeyParameter


class Cipher(bytes: ByteArray?) {
    private val rc4: StreamCipher

    constructor(key: String) : this(hexStringToBytes(key)) {}

    /**
     * Cipher bytes and update cipher
     *
     * @param bytes
     */
    fun cipher(bytes: ByteArray) {
        rc4.processBytes(bytes, 0, bytes.size, bytes, 0)
    }

    companion object {
        private fun hexStringToBytes(key: String): ByteArray {
            require(key.length % 2 == 0) { "invalid hex string" }
            val bytes = ByteArray(key.length / 2)
            val c = key.toCharArray()
            var i = 0
            while (i < c.size) {
                val sb = StringBuilder(2).append(c[i]).append(c[i + 1])
                val j = sb.toString().toInt(16)
                bytes[i / 2] = j.toByte()
                i += 2
            }
            return bytes
        }
    }

    init {
        rc4 = RC4Engine()
        val keyParam: KeyParameter = KeyParameter(bytes)
        rc4.init(true, keyParam)
    }
}