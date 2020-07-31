package com.abysl.chaos.proxy

import com.abysl.chaos.proxy.packets.Packet
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
                println("Socket accepted: ${socket.remoteAddress}")
                val user = User(Client(socket))
                users.add(user)
                launch {
                    user.start()
                }
            }
        }
    }
}


fun main() {
    Proxy().start()
}
