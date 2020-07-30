package com.abysl.chaos.proxy.packets

import com.abysl.chaos.proxy.util.PacketUtil.readUTF8String
import com.abysl.chaos.proxy.util.PacketUtil.writeUTF8String
import io.ktor.utils.io.core.*
import io.ktor.utils.io.streams.*
import kotlin.properties.Delegates

class Pong(data: ByteArray) : Packet(Packets.PONG.id, data) {

    var serial_ by Delegates.notNull<Int>()

    var time_ by Delegates.notNull<Int>()

    init {
        readData(ByteReadPacket(data))
    }

    override fun readData(packet: ByteReadPacket) {
        serial_ = packet.readInt()
        time_ = packet.readInt()
    }

    override fun toByteReadPacket(): ByteReadPacket {
        val packet = BytePacketBuilder()

        packet.writeInt(serial_)
        packet.writeInt(time_)
        return packet.build()

    }

}