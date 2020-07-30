package com.abysl.chaos.proxy

import io.ktor.network.sockets.*
import io.ktor.utils.io.*

data class Client(val socket: Socket, val input: ByteReadChannel, val output: ByteWriteChannel){
    constructor(socket: Socket): this(socket, socket.openReadChannel(), socket.openWriteChannel(autoFlush = true))

    fun isConnected(): Boolean{
        return !socket.isClosed
    }

    fun dispose(){
        socket.close()
    }
}