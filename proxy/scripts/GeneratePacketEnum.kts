import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.StringReader
import java.io.File

println(generatePacketEnum())

data class Packet(val name: String, val id: Int)

fun generatePacketEnum(): String {
    val packetsDoc = readXml("packets")
    val builder = StringBuilder()
    val packets: org.w3c.dom.NodeList = packetsDoc.getElementsByTagName("Packet")
    val packetList = mutableListOf<Packet>()
    builder.appendLine("enum class Packets(val id: Int){")
    for (i in 0 until packets.length) {
        val packet = packets.item(i)
        if (packet is Element) {
            val name: String = getNodeValue("PacketName", packet)
            val id: Int = getNodeValue("PacketID", packet).toInt()
            packetList.add(Packet(name, id))
        }
    }
    packetList.sortBy { it.id }
    packetList.forEach {
        builder.appendLine("${it.name}(${it.id}),")
    }
    builder.appendLine("}")
    return builder.toString()
}

fun getNodeValue(tag: String, element: Element): String {
    val nodeList = element.getElementsByTagName(tag)
    val node = nodeList.item(0)
    if (node != null) {
        if (node.hasChildNodes()) {
            val child = node.firstChild
            while (child != null) {
                if (child.nodeType == Node.TEXT_NODE) {
                    return child.nodeValue
                }
            }
        }
    }
    return ""
}

fun readXml(fileName: String): Document {
    val xmlFile = File("../data/xml/$fileName.xml")
    println(xmlFile.absolutePath)

    val dbFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
    val dBuilder = dbFactory.newDocumentBuilder()
    val xmlInput = org.xml.sax.InputSource(StringReader(xmlFile.readText()))
    val doc = dBuilder.parse(xmlInput)

    return doc
}