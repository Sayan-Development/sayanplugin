package org.sayandev.sayanplugin.connection

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.net.HttpURLConnection
import java.net.URI

class JsonConnection(
    val url: String
) {
    fun fetch(): JsonElement {
        return fetchNullable() ?: throw Exception("Failed to fetch from url `${url}` versions")
    }

    fun fetchNullable(): JsonElement? {
        val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
//        connection.setRequestProperty("User-Agent", "Sayan-Development/sayanplugin (.syrent at discord)")
        if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()
            return JsonParser.parseString(response)
        } else {
            connection.disconnect()
            return null
        }
    }
}