package com.abysl.chaos.proxy.tools

import kotlin.text.StringBuilder
import java.io.File


class Transpiler {

    fun transpile(lines: Array<String>): Array<String> {
        return readClass(lines).toString().split("\n").toTypedArray()
    }

    fun readToken(line: String): Token {
        var temp: String = line.split("var")[1].removeSuffix(";")
        var tempsplit = temp.split(":").toTypedArray()
        if (tempsplit.size != 2) {
            return Token("parsing failure", "parsing failure")
        }
        var type = tempsplit[1].substring(0, 1).toUpperCase() + tempsplit[1].substring(1)
        return Token(tempsplit[0], type)
    }

    fun readClass(lines: Array<String>): ClassData {
        var className = "parse failure"
        var superType = "parse failure"
        val tokens: MutableList<Token> = mutableListOf()
        for (line in lines) {
            if (line.contains("class")) {
                className = line.split("class")[1].split(" extends")[0].trim()
            }
            if (line.contains("public var")) {
                tokens.add(readToken(line))
            }
        }
        return ClassData(className, superType, tokens.toTypedArray())
    }


    data class Token(val name: String, val type: String) {
        fun toRead(token: Token): String {
            when (token.type) {
                "ByteArray" -> return "${token.name} = packet.readBytes(packet.readShort().toInt())"
                "Short" -> return "${token.name} = packet.readShort()"
                "Int" -> return "${token.name} = packet.readInt()"
                "String" -> return "${token.name} = readUTF8String(packet)"
                else -> return "Unimplemented Type: ${token.type}"
            }
        }

        fun toWrite(token: Token): String {
            when (token.type) {
                "ByteArray" -> return "packet.writePacket(ByteReadPacket(${token.name}))"
                "Short" -> return "packet.writeShort(${token.name})"
                "Int" -> return "packet.writeInt(${token.name})"
                "String" -> return "writeUTF8String(packet, ${token.name})"
                else -> return "Unimplemented Type: ${token.type}"
            }
        }
    }

    data class ClassData(val name: String, val superType: String, val members: Array<Token>) {
        val imports = """
        import com.abysl.chaos.proxy.util.PacketUtil.readUTF8String
        import com.abysl.chaos.proxy.util.PacketUtil.writeUTF8String
        import io.ktor.utils.io.core.*
        import io.ktor.utils.io.streams.*
        import kotlin.properties.Delegates
    """.trimIndent()
        val body = """
        class $name(data: ByteArray) : Packet(Packets.${name.toUpperCase()}.id, data){
    """.trimIndent()
        val initFunction = """
    init {
        readData(ByteReadPacket(data))
    }
    """.trimIndent()
        val readFunction = """
    override fun readData(packet: ByteReadPacket) {
    """.trimIndent()
        val writeFunction = """
        override fun toByteReadPacket(): ByteReadPacket {
        val packet =  BytePacketBuilder()
    """.trimIndent()

        override fun toString(): String {
            val builder: StringBuilder = StringBuilder()
            builder.appendln(imports)
            builder.appendln(body)
            for (token in members) {
                if(token.type.contains("Int")){
                    builder.appendln("var  ${token.name} by Delegates.notNull<Int>()")
                } else {
                    builder.appendln("lateinit var ${token.name}: ${token.type}")
                }
            }
            builder.appendln(initFunction)
            builder.appendln(readFunction)
            for (token in members) {
                builder.appendln(token.toRead(token))
            }
            builder.appendln("}")
            builder.appendln(writeFunction)
            for (token in members) {
                builder.appendln(token.toWrite(token))
            }
            builder.appendln("return packet.build()")
            builder.appendln("}")
            builder.appendln("}")
            return builder.toString()
        }
    }


}

fun main() {
    val packetsDir: File = File("data/export/scripts/kabam/rotmg/messaging/impl")
    val outputDir = "data/transpiled/"
    val transpiler = Transpiler()
//val packetsDir: File = File("..")

    packetsDir.walk().filter { it.isFile && it.extension == "as" }.forEach { file ->
        val out = File(outputDir + file.path.split("impl")[1].removeSuffix(".as") + ".kt")

        val transpiledLines = transpiler.transpile(file.readLines().toTypedArray())
        out.parentFile.mkdirs()
        if (out.exists())
            out.delete()
        out.createNewFile()
        out.printWriter().use {
            for (line in transpiledLines) {
                it.println(line)
            }
        }
    }
}