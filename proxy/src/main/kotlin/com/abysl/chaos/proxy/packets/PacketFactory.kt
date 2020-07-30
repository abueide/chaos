package com.abysl.chaos.proxy.packets

import io.ktor.utils.io.core.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

object PacketFactory {

    fun createPacket(id: Int, data: ByteArray): Packet{
        if(id == Packets.HELLO.id){
            return HelloPacket(data)
        }
        return UnknownPacket(id, data)
    }

    fun createPacket(id: Int, data: ByteReadPacket): Packet{
        return createPacket(id, data.readBytes(data.remaining.toInt()))
    }
}