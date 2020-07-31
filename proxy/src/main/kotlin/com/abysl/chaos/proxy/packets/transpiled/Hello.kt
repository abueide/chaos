package com.abysl.chaos.proxy.packets.transpiled

import com.abysl.chaos.proxy.packets.Packet
import com.abysl.chaos.proxy.packets.Packets
import com.abysl.chaos.proxy.util.PacketUtil.readUTF8String
import com.abysl.chaos.proxy.util.PacketUtil.writeUTF8String
import io.ktor.utils.io.core.*
import kotlin.properties.Delegates

class Hello(data: ByteArray) : Packet(Packets.HELLO.id, data) {

    lateinit var buildVersion_: String
    var gameId_ by Delegates.notNull<Int>()
    lateinit var guid_: String
    lateinit var password_: String
    lateinit var secret_: String
    var keyTime_ by Delegates.notNull<Int>()
    lateinit var key_: ByteArray
    lateinit var mapJSON_: String
    lateinit var entrytag_: String
    lateinit var gameNet: String
    lateinit var gameNetUserId: String
    lateinit var playPlatform: String
    lateinit var platformToken: String
    lateinit var userToken: String
    lateinit var previousConnectionGuid: String

    init {
        readData(ByteReadPacket(data))
    }

    override fun readData(packet: ByteReadPacket) {

        buildVersion_ = readUTF8String(packet)
        gameId_ = packet.readInt()
        guid_ = readUTF8String(packet)
        password_ = readUTF8String(packet)
        secret_ = readUTF8String(packet)
        keyTime_ = packet.readInt()
        key_ = packet.readBytes(packet.readShort().toInt())
        mapJSON_ = readUTF8String(packet)
        entrytag_ = readUTF8String(packet)
        gameNet = readUTF8String(packet)
        gameNetUserId = readUTF8String(packet)
        playPlatform = readUTF8String(packet)
        platformToken = readUTF8String(packet)
        userToken = readUTF8String(packet)
        previousConnectionGuid = readUTF8String(packet)
    }

    override fun toByteReadPacket(): ByteReadPacket {
        val packet = BytePacketBuilder()

        writeUTF8String(packet, buildVersion_)
        packet.writeInt(gameId_)
        writeUTF8String(packet, guid_)
        writeUTF8String(packet, password_)
        writeUTF8String(packet, secret_)
        packet.writeInt(keyTime_)
        packet.writePacket(ByteReadPacket(key_))
        writeUTF8String(packet, mapJSON_)
        writeUTF8String(packet, entrytag_)
        writeUTF8String(packet, gameNet)
        writeUTF8String(packet, gameNetUserId)
        writeUTF8String(packet, playPlatform)
        writeUTF8String(packet, platformToken)
        writeUTF8String(packet, userToken)
        writeUTF8String(packet, previousConnectionGuid)
        return packet.build()
    }

}


