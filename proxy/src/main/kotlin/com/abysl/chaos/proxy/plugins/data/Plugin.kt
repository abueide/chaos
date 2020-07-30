package com.abysl.chaos.proxy.plugins.data

import com.abysl.chaos.proxy.packets.Packet
import com.abysl.chaos.proxy.packets.Packets


open class Plugin {
    var enabled = true

    val clientHandlers: MutableMap<Int, MutableList<(packet: Packet) -> Unit>> = mutableMapOf()
    val serverHandlers: MutableMap<Int, MutableList<(packet: Packet) -> Unit>> = mutableMapOf()
    val clientAllHandlers: MutableList<(packet: Packet) -> Unit> = mutableListOf()
    val serverAllHandlers: MutableList<(packet: Packet) -> Unit> = mutableListOf()

    fun subscribeClient(vararg ids: Int, handler: (packet: Packet) -> Unit) {
        ids.forEach {
            if (clientHandlers[it] == null) {
                clientHandlers[it] = mutableListOf()
            }
            clientHandlers[it]!!.add(handler)
        }
    }

    fun subscribeClient(vararg packets: Packets, handler: (packet: Packet) -> Unit) {
        val idArray = packets.map { it.id }.toIntArray()
        subscribeClient(ids = *idArray, handler = handler)
    }

    fun subscribeAllClient(handler: (packet: Packet) -> Unit) {
        clientAllHandlers.add(handler)
    }


    fun subscribeServer(vararg ids: Int, handler: (packet: Packet) -> Unit) {
        ids.forEach {
            if (serverHandlers[it] == null) {
                serverHandlers[it] = mutableListOf()
            }
            serverHandlers[it]!!.add(handler)
        }
    }

    fun subscribeServer(vararg packets: Packets, handler: (packet: Packet) -> Unit) {
        val idArray = packets.map { it.id }.toIntArray()
        subscribeServer(ids = *idArray, handler = handler)
    }

    fun subscribeAllServer(handler: (packet: Packet) -> Unit) {
        serverAllHandlers.add(handler)
    }


    fun triggerClient(packet: Packet) {
        clientHandlers[packet.id]?.forEach {
            it.invoke(packet)
        }
        clientAllHandlers.forEach {
            it.invoke(packet)
        }
    }

    fun triggerServer(packet: Packet) {
        serverHandlers[packet.id]?.forEach {
            it.invoke(packet)
        }
        serverAllHandlers.forEach {
            it.invoke(packet)
        }
    }
}