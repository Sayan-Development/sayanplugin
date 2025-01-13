package org.sayandev.sayanplugin.connection

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.net.HttpURLConnection
import java.net.URI

class JsonConnection(
    val url: String
) {
    fun fetch(): JsonElement {
        val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().readText()
            return JsonParser.parseString(response)
        } else {
            throw Exception("Failed to fetch from url `${url}` versions")
        }
    }
}