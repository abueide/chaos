package com.abysl.chaos.proxy

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import java.net.InetSocketAddress

class User(val client_input: ByteReadChannel, val client_output: ByteWriteChannel) {
    companion object {
        val inCipher = Cipher("c79332b197f92ba85ed281a023")
        val outCipher = Cipher("6a39570cc9de4ec71d64821894")
    }

    lateinit var serverSocket: Socket
    lateinit var server_input: ByteReadChannel
    lateinit var server_output: ByteWriteChannel

    fun start() {
        runBlocking {
            serverConnected()
        }
        GlobalScope.launch {
            while (true) {
                if(client_input.availableForRead > 0) {
                    processClient()
                }
            }
        }

        GlobalScope.launch {
            while (true) {
                if (serverConnected() && server_input.availableForRead > 0) {
                    processServer()
                }
            }
        }
    }

    suspend fun processClient() {
        val packetSize = client_input.readInt()
        val packetId: Byte =client_input.readByte()
        println("Client sent packet with id $packetId and length $packetSize")
        val packet = client_input.readPacket(packetSize - 5)
        readPacket(packet, packetSize - 5, outCipher)

        if (serverConnected()) {
            server_output.writeInt(packetSize)
            server_output.writeByte(packetId)
            server_output.writePacket(packet)
        }
    }

    suspend fun processServer() {
        val packetSize: Int = server_input.readInt()
        val packetId: Byte = server_input.readByte()
        println("Server sent packet with id $packetId and length $packetSize")
        val packet: ByteReadPacket = server_input.readPacket(packetSize - 5)

        readPacket(packet, packetSize - 5, inCipher)

        client_output.writeInt(packetSize)
        client_output.writeByte(packetId)
        client_output.writePacket(packet)
    }

    private suspend fun serverConnected(): Boolean {
        if (!this::serverSocket.isInitialized || serverSocket.isClosed) {
            serverSocket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("184.169.251.52", 2050))
            server_input = serverSocket.openReadChannel()
            server_output = serverSocket.openWriteChannel(autoFlush = true)
        }
        return !serverSocket.isClosed
    }

    private fun readPacket(packet: ByteReadPacket, size: Int, cipher: Cipher) {
        val copy = packet.copy()
        var bytes = copy.readBytes(size)
        cipher.cipher(bytes)
        println(String(bytes))
    }
}