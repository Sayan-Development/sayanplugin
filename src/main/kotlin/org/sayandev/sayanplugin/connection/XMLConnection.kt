package org.sayandev.sayanplugin.connection

import org.w3c.dom.Document
import java.net.HttpURLConnection
import java.net.URI
import javax.xml.parsers.DocumentBuilderFactory

class XMLConnection(
    val url: String
) {
    fun fetch(): Document {
        val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().readText()
            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            return documentBuilder.parse(response.byteInputStream())
        } else {
            throw Exception("Failed to fetch from url `${url}` versions")
        }
    }
}