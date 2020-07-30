package com.abysl.chaos.proxy.packets

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*

class UnknownPacket(id: Int = -1, packet: ByteArray): Packet(id, packet) {

    var data: ByteArray = byteArrayOf()

    init {
        readData(ByteReadPacket(packet))
    }

    override fun readData(packet: ByteReadPacket) {
        data = packet.readBytes(packet.remaining.toInt())
    }

    override fun toByteReadPacket(): ByteReadPacket {
        return ByteReadPacket(data)
    }

    override fun toString(): String {
        val packet = Packets.values().firstOrNull { it.id == this.id }
        val name = packet?.name ?: "Unknown"
        return "Name:$name, id: $id, length:${data.size}"
    }
}