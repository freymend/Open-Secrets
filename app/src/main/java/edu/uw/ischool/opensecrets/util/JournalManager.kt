package edu.uw.ischool.opensecrets.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import edu.uw.ischool.opensecrets.model.Entry
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import util.Request
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat

class JournalManager(private val context: Context) {

    private var journal: File = File(context.filesDir, "journal.json")

    fun backupJournal(username: String): Boolean {
        if (journalExist()) {
            val journal = loadData().toString()
            try {
                val response = Request.post("https://not-open-secrets.fly.dev/backup", """
                    {
                        "username": "$username",
                        "journal": $journal
                    }
                """.trimIndent())
                if (response.has("backedUp")) {
                    return true
                }
            } catch (e: Exception) {
                Log.e("backup", e.toString())
            }
        }
        return false
    }

    fun journalExist(): Boolean {
        return journal.exists()
    }

    fun createJournal() : Boolean{
        return journal.createNewFile()
    }

    fun appendEntry(entry: Entry): Boolean {
        // check if journal exist
        if (journalExist()) {
            // load og data
            val data: JSONArray = loadData()
            // create new data obj
            val jsonEntry = JSONObject()
            jsonEntry.put("title", entry.title)
            jsonEntry.put("text", entry.text)
            jsonEntry.put("color", entry.color)
            jsonEntry.put("dateCreated", entry.dateCreated.toString())
            data.put(jsonEntry)
            // create new file directory
            val fileOutput = writeNewFile(data)
            // check file data
            updateOldFile(fileOutput, data)
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
            val data: JSONArray = loadData()
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

    fun deleteEntry(pos: Int): Boolean {
        // check if journal exist
        if (journalExist()) {
            // load og data
            val data: JSONArray = loadData()
            val oldValue = data.get(pos)
            // remove entry from data and verify removal finish
            return if (oldValue == data.remove(pos)) {
                // create new file directory
                val fileOutput = writeNewFile(data)

                // check file data
                updateOldFile(fileOutput, data)
                true
            } else {
                Log.d("delete", "wrong pos")
                false
            }
        } else {
            Log.d("delete", "no journal exist")
            return false
        }
    }

    fun updateEntry(pos: Int, entry: Entry): Boolean {
        // check if journal exist
        if (journalExist()) {
            // load og data
            val data: JSONArray = loadData()

            // create new data obj
            val jsonEntry = JSONObject()
            jsonEntry.put("title", entry.title)
            jsonEntry.put("text", entry.text)
            jsonEntry.put("color", entry.color)
            jsonEntry.put("dateCreated", entry.dateCreated.toString())
            data.put(pos, jsonEntry)
            // create new file directory
            val fileOutput = writeNewFile(data)
            // check file data
            updateOldFile(fileOutput, data)
            return true
        } else {
            return false
        }
    }

    private fun loadData(): JSONArray {
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
        return data
    }

    private fun writeNewFile(data: JSONArray): File {
        val fileOutput = File(context.filesDir, "new_journal.json")
        fileOutput.createNewFile()
        // write to new file
        val outputStream = FileWriter(fileOutput)
        outputStream.use {
            it.write(data.toString())
            it.flush()
        }
        outputStream.close()
        return fileOutput
    }

    private fun updateOldFile(fileOutput: File, data: JSONArray) {

        val newData = FileReader(fileOutput).use {
            JSONArray(it.readText())
        }
        if (verifyJSON(data, newData)) {
            journal.delete()
            fileOutput.renameTo(File(context.filesDir, "journal.json"))
        } else {
            Toast.makeText(context, "data not the same", Toast.LENGTH_SHORT).show()
        }
    }
}