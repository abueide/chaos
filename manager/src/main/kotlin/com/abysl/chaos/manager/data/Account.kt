package com.abysl.chaos.manager.data

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Account(val nickname: String, val email: String, val password: String) {

    constructor(): this("", "", "")
    constructor(email: String, password: String): this(email.split("@")[0], email, password)


    fun getBase64Email(): String {
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(email.toByteArray())
    }
    fun getBase64Password(): String {
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(password.toByteArray())
    }

    override fun toString(): String {
        return nickname;
    }

}