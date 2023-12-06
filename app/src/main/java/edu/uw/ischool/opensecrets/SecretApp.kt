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
}