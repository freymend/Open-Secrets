package edu.uw.ischool.opensecrets.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log

const val ALARM_ACTION = "OpenSecretMessageAlarm"
class TimedMessageQueue(val context : Context, val alarmManager: AlarmManager, val sendEntryLambda : (Entry) -> Unit) {
    val queuedEntries : MutableMap<Int, Entry> = mutableMapOf<Int, Entry>()
//    val queuedPendings : ArrayList<PendingIntent> = ArrayList<PendingIntent>()
    var receiver : BroadcastReceiver? = null
    var requestCode : Int = 0

    fun queueSendEntry(entry : Entry, minutesToSend : Long){
        requestCode = System.currentTimeMillis().toInt()
        if(receiver == null){
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Log.i("Time", "Request code ${intent?.extras?.getInt("requestCode")}")
                    sendNextEntryInQueue(intent?.extras?.getInt("requestCode"))
                }
            }

            val filter = IntentFilter(ALARM_ACTION)
            context.registerReceiver(receiver, filter)
        }
        val intent = Intent(ALARM_ACTION).putExtra("requestCode", requestCode)

        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
//        queuedPendings.add(pendingIntent)
        queuedEntries[requestCode] = entry
        if(Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()){
            Log.i("TimedMessageQueue", "sending entry ${entry.title} in $minutesToSend minutes")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 60000 * minutesToSend,
                pendingIntent)
        }
        else{
            Log.i("TimedMessageQueue", "Message Queue failed you, sorry")
        }
    }
    fun sendNextEntryInQueue(requestCode: Int?) {
//        if(queuedPendings.size > 0){
//            queuedPendings.removeFirstOrNull()
//        }
        if(queuedEntries.isNotEmpty()){
            val entryToSend : Entry? = queuedEntries.remove(requestCode)
            if (entryToSend != null) {
                sendEntryLambda(entryToSend)
            }
        }
    }
    fun sendAllEntries(){
        for((key, entry : Entry) in queuedEntries){
            sendEntryLambda(entry)
        }
//        for(pendingIntent : PendingIntent in queuedPendings){
//            alarmManager.cancel(pendingIntent)
//        }
        if(receiver != null){
            context.unregisterReceiver(receiver)
        }

    }
}