package com.abysl.chaos.proxy

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer

class User(val client_input: ByteReadChannel, val client_output: ByteWriteChannel) {

    lateinit var serverSocket: Socket
    lateinit var server_input: ByteReadChannel
    lateinit var server_output: ByteWriteChannel
    var connected = false


    suspend fun process() {
                val client_input_buffer = client_input.toByteArray()
                val server_input_buffer = server_input.toByteArray()

                if (client_input_buffer.isNotEmpty()) {
                    for (b: Byte in client_input_buffer) {
                        println("New client packet: ")
                        print("$b, ")
                        print("\n")
                    }
//                    server_output.writeFully(client_input_buffer)
                }

                if (server_input_buffer.isNotEmpty()) {
                    for (b: Byte in server_input_buffer) {
                        println("New server packet: ")
                        print("$b, ")
                        print("\n")
                    }
//                    client_output.writeFully(server_input_buffer)
            }
    }

    private suspend fun serverConnected(): Boolean {
        if (!this::serverSocket.isInitialized || serverSocket.isClosed) {
            serverSocket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("184.169.251.52", 2050))
            server_input = serverSocket.openReadChannel()
            server_output = serverSocket.openWriteChannel(autoFlush = true)
        }
        return !serverSocket.isClosed
    }
}