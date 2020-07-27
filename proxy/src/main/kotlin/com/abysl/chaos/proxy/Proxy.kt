package com.abysl.chaos.proxy

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

class Proxy(){

}

fun main() {
    embeddedServer(Netty, 443) {
        routing {
            get("/char/list") {
                println("Received")
                call.respondText("My Example Blog", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}
