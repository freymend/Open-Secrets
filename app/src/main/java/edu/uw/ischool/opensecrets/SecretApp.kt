package edu.uw.ischool.opensecrets

import android.app.Application
import android.widget.Toast
import edu.uw.ischool.opensecrets.model.Entry
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat

class SecretApp : Application() {

    private lateinit var journal: File
    override fun onCreate() {
        journal = File(filesDir, "journal.json")
        super.onCreate()
    }

    fun journalExist(): Boolean {
        return journal.exists()
    }

    fun appendEntry(entry: Entry): Boolean {
        // check if journal exist
        if (journalExist()) {
            // load og data
            val inputStream = FileReader(journal)
            var data: JSONArray
            inputStream.use {
                data = try {
                    JSONArray(it.readText())

                } catch (e: JSONException) {
                    JSONArray()
                }
            }
            inputStream.close()
            // create new data obj
            val jsonEntry = JSONObject()
            jsonEntry.put("title", entry.title)
            jsonEntry.put("text", entry.text)
            jsonEntry.put("color", entry.color)
            jsonEntry.put("dateCreated", entry.dateCreated.toString())
            data.put(jsonEntry)
            // create new file directory
            val fileOutput = File(filesDir, "new_journal.json")
            fileOutput.createNewFile()
            // write to new file
            val outputStream = FileWriter(fileOutput)
            outputStream.use {
                it.write(data.toString())
                it.flush()
            }
            outputStream.close()
            // check file data
            val newData = FileReader(fileOutput).use {
                JSONArray(it.readText())
            }
            if (verifyJSON(data, newData)) {
                journal.delete()
                fileOutput.renameTo(File(filesDir, "journal.json"))
            } else {
                Toast.makeText(this, "data not the same", Toast.LENGTH_SHORT).show()
            }
            return true
        } else {
            return false
        }
    }

    private fun verifyJSON(ogData: JSONArray, newData: JSONArray): Boolean {
        return ogData.toString() == newData.toString()
    }

    fun loadEntry(): List<Entry>? {
        if (journalExist()) {
            val inputStream = FileReader(journal)
            var data: JSONArray
            inputStream.use {
                data = try {
                    JSONArray(it.readText())

                } catch (e: JSONException) {
                    JSONArray()
                }
            }
            inputStream.close()
            val tmpList = mutableListOf<Entry>()
            for (index in 0 until data.length()) {
                val value = data.getJSONObject(index)
                val title = value.getString("title")
                val text = value.getString("text")
                val color = value.getString("color")
                val dateCreatedString = value.getString("dateCreated")
                val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
                val dateCreated = dateFormat.parse(dateCreatedString)
                tmpList.add(
                    Entry(
                        title = title,
                        text = text,
                        color = color,
                        dateCreated = dateCreated
                    )
                )
            }
            return tmpList.toList()
        }
        return null
    }
}