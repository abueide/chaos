package com.abysl.chaos.proxy

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import kotlin.concurrent.thread

class Proxy {
    private val users: MutableList<User> = mutableListOf()


    fun start() {
        runBlocking {
            val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 2050))
            println("Started proxy server at ${server.localAddress}")

            while (true) {
                val socket = server.accept()
                launch {
                    println("Socket accepted: ${socket.remoteAddress}")
                    val client_input: ByteReadChannel = socket.openReadChannel()
                    val client_output: ByteWriteChannel = socket.openWriteChannel(autoFlush = true)
                    while(true){
                        println("hello")
                        val line = client_input.readUTF8Line()
//                        val server_input_buffer = server_input.toByteArray()

//                        if (client_input_buffer.isNotEmpty()) {
//                            println("test")
//                            for (b: Byte in client_input_buffer) {
//                                println("New client packet: ")
//                                print("$b, ")
//                                print("\n")
//                            }
//                            client_output.writeFully(client_input_buffer)
//                        }

//                        if (server_input_buffer.isNotEmpty()) {
//                            for (b: Byte in server_input_buffer) {
//                                println("New server packet: ")
//                                print("$b, ")
//                                print("\n")
//                            }
//                            client_output.writeFully(server_input_buffer)
//                        }
                    }
                }
            }
        }
    }
}


fun main() {
    Proxy().start()
}
