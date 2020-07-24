package com.abysl.chaos.proxy

class App {
    val greeting: String
        get() {
            return "Hello world."
        }
}

fun main() {
    println(App().greeting)
}
