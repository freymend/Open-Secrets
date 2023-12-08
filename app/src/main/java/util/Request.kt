package util

import org.json.JSONObject
import java.io.BufferedOutputStream
import java.net.HttpURLConnection
import java.net.URL

object Request {
    fun post(url: String, body: String): JSONObject {
        with(URL(url).openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            connectTimeout = 5000

            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")

            doOutput = true
            setChunkedStreamingMode(0)
            BufferedOutputStream(outputStream).use {
                it.write(body.toByteArray())
                it.flush()
            }

            return inputStream.bufferedReader().use {
                JSONObject(it.readText())
            }
        }
    }

    fun get(url: String): JSONObject {
        with(URL(url).openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            connectTimeout = 5000

            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")

            return inputStream.bufferedReader().use {
                JSONObject(it.readText())
            }
        }
    }
}