package com.abysl.chaos.proxy.util

import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.ByteReadPacket
import io.ktor.utils.io.core.readUShort
import io.ktor.utils.io.core.writeUShort
import io.ktor.utils.io.streams.readerUTF8
import io.ktor.utils.io.streams.writerUTF8

object PacketUtil {
    fun readUTF8String(packet: ByteReadPacket): String{
        return packet.readTextExact(packet.readUShort().toInt())
    }

    fun writeUTF8String(packet: BytePacketBuilder, string: String): Unit{
        packet.writeUShort(string.length.toUShort())
        packet.writerUTF8().write(string)
    }
}