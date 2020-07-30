package com.abysl.chaos.proxy.packets

import io.ktor.utils.io.core.*
import io.ktor.utils.io.streams.writerUTF8

class FailurePacket(data: ByteArray): Packet(Packets.FAILURE.id, data) {
    lateinit var errorId_: Integer
    lateinit var errorDescription: String
    lateinit var errorPlace_: String
    lateinit var errorConnectionId: String

    init {
        readData(ByteReadPacket(data))
    }

    override fun readData(packet: ByteReadPacket) {
        errorId_ = packet.readInt() as Integer
        errorDescription = packet.readTextExact(packet.readUShort().toInt())
        errorPlace_ = packet.readTextExact(packet.readUShort().toInt())
        errorConnectionId = packet.readTextExact(packet.readUShort().toInt())
    }

    override fun toByteReadPacket(): ByteReadPacket {
        val packet = BytePacketBuilder()
        packet.writeInt(errorId_.toInt())
        packet.writeUShort(errorDescription.toUShort())
        packet.writerUTF8().write(errorDescription)
        packet.writeUShort(errorPlace_.toUShort())
        packet.writerUTF8().write(errorPlace_)
        packet.writeUShort(errorConnectionId.toUShort())
        packet.writerUTF8().write(errorConnectionId)
        return packet.build();
    }
}