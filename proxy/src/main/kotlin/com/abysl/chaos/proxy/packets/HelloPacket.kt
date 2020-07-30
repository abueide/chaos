package com.abysl.chaos.proxy.packets

import com.abysl.chaos.proxy.util.PacketUtil.readUTF8String
import com.abysl.chaos.proxy.util.PacketUtil.writeUTF8String
import io.ktor.utils.io.core.*
import io.ktor.utils.io.streams.*
import kotlin.properties.Delegates

class HelloPacket(data: ByteArray) : Packet(Packets.HELLO.id, data) {

    lateinit var buildVersion_: String
    var gameId_ by Delegates.notNull<Int>()
    lateinit var guid_: String
    var random1 by Delegates.notNull<Int>()
    lateinit var password_: String
    var random2 by Delegates.notNull<Int>()
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
    lateinit var obf1: String
    lateinit var previousConnectionGuid: String

    init {
        readData(ByteReadPacket(data))
    }

    override fun readData(packet: ByteReadPacket) {
        buildVersion_ = readUTF8String(packet)
        //printData(packet)
        gameId_ = packet.readInt()
        //printData(packet)
        guid_ = readUTF8String(packet)
        //printData(packet)
        random1 = packet.readInt()
        //printData(packet)
        password_ = readUTF8String(packet)
        //printData(packet)
        random2 = packet.readInt()
        //printData(packet)
        secret_ = readUTF8String(packet)
        //printData(packet)
        keyTime_ = packet.readInt()
        //printData(packet)
        key_ = packet.readBytes(packet.readShort().toInt())
        //printData(packet)
        mapJSON_ = packet.readTextExact(packet.readInt())
        //printData(packet)
        entrytag_ = readUTF8String(packet)
        //printData(packet)
        gameNet = readUTF8String(packet)
        //printData(packet)
        gameNetUserId = readUTF8String(packet)
        //printData(packet)
        playPlatform = readUTF8String(packet)
        //printData(packet)
        platformToken = readUTF8String(packet)
        //printData(packet)
        userToken = readUTF8String(packet)
        obf1 = readUTF8String(packet)
        //printData(packet)
        previousConnectionGuid = readUTF8String(packet)
    }

    override fun toByteReadPacket(): ByteReadPacket {
        val packet =  BytePacketBuilder()
        writeUTF8String(packet, buildVersion_)
        packet.writeInt(gameId_)
        writeUTF8String(packet,guid_)
        packet.writeInt(random1)
        writeUTF8String(packet,password_)
        packet.writeInt(random2)
        writeUTF8String(packet,secret_)
        packet.writeInt(keyTime_)
        packet.writeShort(key_.size.toShort())
        packet.writePacket(ByteReadPacket(key_))
        packet.writeInt(mapJSON_.length)
        packet.writerUTF8().write(mapJSON_)
        writeUTF8String(packet,entrytag_)
        writeUTF8String(packet,gameNet)
        writeUTF8String(packet,gameNetUserId)
        writeUTF8String(packet,playPlatform)
        writeUTF8String(packet,platformToken)
        writeUTF8String(packet, userToken)
        writeUTF8String(packet, obf1)
        writeUTF8String(packet, previousConnectionGuid)
        return packet.build()
    }


    private fun printData(packet: ByteReadPacket){
        val copy = packet.copy()
        print("Size: ${copy.remaining}, data: ")
        for(b in copy.readBytes(copy.remaining.toInt())){
            print("$b ")
        }
        print("\n")
    }
}