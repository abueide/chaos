package com.abysl.chaos.proxy.plugins.data

import com.abysl.chaos.proxy.packets.Packets
import com.abysl.chaos.proxy.packets.UnknownPacket
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.streams.*

class TrafficPlugin: Plugin() {
    init {
        val exclude = listOf(Packets.MOVE.id, Packets.NEWTICK.id, Packets.PONG.id, Packets.PING.id)
        subscribeAllClient {packet ->
            if(exclude.none {it == packet.id}){
                println("Client - $packet")
            }
        }
        subscribeAllServer {packet ->
            if(exclude.none {it == packet.id}){
                println("Server - $packet")
            }
        }

        subscribeServer(Packets.FAILURE){
            it as UnknownPacket
            val packet = ByteReadPacket(it.data)
            println(packet.readerUTF8().readText())
        }
    }
}