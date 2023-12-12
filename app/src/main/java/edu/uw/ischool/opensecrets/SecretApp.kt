package edu.uw.ischool.opensecrets

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import edu.uw.ischool.opensecrets.model.ContactMessager
import edu.uw.ischool.opensecrets.model.Entry
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import edu.uw.ischool.opensecrets.model.Contact
import edu.uw.ischool.opensecrets.model.TimedMessageQueue
import java.util.Date
import edu.uw.ischool.opensecrets.util.JournalManager
import edu.uw.ischool.opensecrets.util.OptionManager


const val CHANNEL_ID = "OpenSecretNotifications"
class SecretApp : Application() {

    private val contactMessager : ContactMessager = ContactMessager()
    lateinit var messageQueue : TimedMessageQueue
    lateinit var alarmManager: AlarmManager
    private var messageID = 1
    private val sampleEntry : Entry = Entry("My Title", "blue", "Call me Ishmael. Some years ago...", Date())
    lateinit var journalManager: JournalManager
    lateinit var optionManager: OptionManager
    override fun onCreate() {
        super.onCreate()
    }

    fun loadRepository() {
        journalManager = JournalManager(this)
        optionManager = OptionManager(this)
        createNotificationChannel()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        messageQueue = TimedMessageQueue(this, alarmManager, ::sendEntryToRandomContact)
        updateContactList()
//        sendNotification("Notification title", "Hello there!")
//        queueSendEntry(sampleEntry)
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