package com.abysl.chaos.proxy.packets

import io.ktor.utils.io.core.*

abstract class Packet(val id: Int = -1, dataArray: ByteArray){
    var block = false

    abstract fun readData(packet: ByteReadPacket)
    abstract fun toByteReadPacket(): ByteReadPacket

    override fun toString(): String {
        val packet = Packets.values().firstOrNull { it.id == this.id }
        val name = packet?.name ?: "Unknown"
        return "Name:$name, id: $id"
    }
}