package edu.uw.ischool.opensecrets

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.widget.Toast
import edu.uw.ischool.opensecrets.model.ContactMessager
import edu.uw.ischool.opensecrets.model.Entry
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import edu.uw.ischool.opensecrets.model.Contact
import edu.uw.ischool.opensecrets.model.TimedMessageQueue
import java.util.Date

const val CHANNEL_ID = "OpenSecretNotifications"
class SecretApp : Application() {

    private lateinit var journal: File
    private val contactMessager : ContactMessager = ContactMessager()
    lateinit var messageQueue : TimedMessageQueue
    lateinit var alarmManager: AlarmManager
    private var messageID = 1
    private val sampleEntry : Entry = Entry("My Title", "blue", "Call me Ishmael. Some years ago...", Date())
    override fun onCreate() {
        journal = File(filesDir, "journal.json")
        super.onCreate()
        createNotificationChannel()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        messageQueue = TimedMessageQueue(this, alarmManager, ::sendEntryToRandomContact)
        updateContactList()
//        sendNotification("Notification title", "Hello there!")
//        queueSendEntry(sampleEntry)
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
    fun updateContactList() {
        Log.i("SecretApp", "updating contacts list!")
        val contacts : ArrayList<Contact> = contactMessager.getContactList(this)
        for(contact : Contact in contacts){
            Log.i("SecretApp", "name:${contact.name}\nnumber:${contact.phoneNumber}")
        }
    }
    fun sendEntryToRandomContact(entry : Entry) : Unit{
        val contact : Contact? = contactMessager.sendMessageToRandom("${entry.title}\n${entry.text}", this)
        if(contact != null){
            sendNotification("Sent entry to ${contact.name}", entry.title)
        }
        messageID++
    }
    fun sendNotification(title : String, textContent : String){
        val context : Context = this
        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.round_home_24)
            .setContentTitle(title)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(messageID, builder.build())
        }
    }
    fun queueSendEntry(entry : Entry){
        messageQueue.queueSendEntry(entry, 1.toLong())
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "opensecretname"
            val descriptionText = "notif channel for our journaling app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}