package com.abysl.chaos.proxy

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

class Test {

}

fun main(){
    runBlocking {
        val proxySocket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2050))
        val server_input = proxySocket.openReadChannel()
        val server_output = proxySocket.openWriteChannel(autoFlush = true)
        server_output.writeStringUtf8("hello, good friend")
        while (true){
            val buffer = server_input.toByteArray()
            if(buffer.isNotEmpty()){
                println("hello");
            }
        }
    }
}