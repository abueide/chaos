package com.abysl.chaos.proxy

import com.abysl.chaos.proxy.packets.Packet
import com.abysl.chaos.proxy.packets.PacketFactory
import com.abysl.chaos.proxy.plugins.data.PluginManager
import com.abysl.chaos.proxy.plugins.data.TrafficPlugin
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import java.net.InetSocketAddress

class User(val client: Client) {
    companion object {
        val clientInCipher = Cipher("6a39570cc9de4ec71d64821894")
        val clientOutCipher = Cipher("c79332b197f92ba85ed281a023")
        val serverInCipher = Cipher("c79332b197f92ba85ed281a023")
        val serverOutCipher = Cipher("6a39570cc9de4ec71d64821894")
    }

    lateinit var server: Server

    val pluginManager = PluginManager(TrafficPlugin())

    fun start() {
        runBlocking {
            serverConnected()
        }

        GlobalScope.launch {
            while (true) {
                if (!client.isConnected()) {
                    break
                }
                if (client.input.availableForRead > 0) {
                    processClient()
                }
            }
        }

        GlobalScope.launch {
            while (true) {
                if (!client.isConnected()) {
                    break
                }
                if (serverConnected() && server.input.availableForRead > 0) {
                    processServer()
                }
            }
        }
    }

    suspend fun processClient() {
        pluginManager.triggerClient(readPacket(client.input, clientInCipher))

        while (pluginManager.clientToServer.isNotEmpty()){
            sendPacket(pluginManager.clientToServer.poll(), server.output, serverOutCipher)
        }
    }

    suspend fun processServer() {
        pluginManager.triggerServer(readPacket(server.input, serverInCipher))

        while (pluginManager.serverToClient.isNotEmpty()){
            sendPacket(pluginManager.serverToClient.poll(), client.output, clientOutCipher)
        }
    }

    private suspend fun serverConnected(): Boolean {
        if (!this::server.isInitialized || !server.isConnected()) {
            val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("184.169.251.52", 2050))
            server = Server(socket)
        }
        return server.isConnected()
    }

    private fun cipher(packet: ByteReadPacket, cipher: Cipher): ByteArray {
        val byteArray: ByteArray = packet.readBytes(packet.remaining.toInt())
        cipher.cipher(byteArray)
        return byteArray
    }

    private suspend fun readPacket(input: ByteReadChannel, cipher: Cipher): Packet {
        val packetSize = input.readInt()
        val packetId: Byte = input.readByte()
        val packet = input.readPacket(packetSize - 5)
        val decryptedPacket = cipher(packet, cipher)
        return PacketFactory.createPacket(packetId.toInt(), decryptedPacket)
    }

    private suspend fun sendPacket(packet: Packet, output: ByteWriteChannel, cipher: Cipher){
        val packetData = cipher(packet.toByteReadPacket(), cipher)
        output.writeInt(packetData.size + 5)
        output.writeByte(packet.id.toByte())
        output.writePacket(ByteReadPacket(packetData))
    }

    private suspend fun testClientPacket(packet: ByteReadChannel){

        val packetSize: Int = packet.readInt();
        val packetId: Byte = packet.readByte()
        val data: ByteArray = packet.readPacket(packetSize - 5).readBytes(packetSize - 5)
        val backup: ByteArray = data.copyOf()
        println()
        clientInCipher.cipher(data)
        for(b in data){
            print("$b, ")
        }
        print("\n")
        serverOutCipher.cipher(data)
        for(i in data.indices){
            print("${data[i]}:${backup[i]}, ")
        }
        print("\n")

    }
}