package com.abysl.chaos.proxy.plugins

import com.abysl.chaos.proxy.packets.Packets
import com.abysl.chaos.proxy.plugins.data.Plugin

class HelloPlugin: Plugin() {
    init {
        subscribeClient(Packets.HELLO){
        }
    }
}