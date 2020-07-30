package com.abysl.chaos.proxy.plugins.data

import com.abysl.chaos.proxy.packets.Packet
import java.util.*

class PluginManager(vararg initPlugins: Plugin) {
    val plugins: MutableList<Plugin> = mutableListOf()

    val clientToServer: Queue<Packet> = LinkedList<Packet>()
    val serverToClient: Queue<Packet> = LinkedList<Packet>()

    init {
        plugins.addAll(initPlugins)
    }

    fun triggerClient(packet: Packet){
        plugins.forEach {
            it.triggerClient(packet)
        }
        if(!packet.block){
            clientToServer.add(packet)
        }
    }

    fun triggerServer(packet: Packet){
        plugins.forEach {
            it.triggerServer(packet)
        }
        if(!packet.block){
            serverToClient.add(packet)
        }
    }
}