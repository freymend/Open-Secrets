package edu.uw.ischool.opensecrets.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.SystemClock
import android.util.Log
import java.lang.System.currentTimeMillis
import java.time.Instant

const val ALARM_ACTION = "OpenSecretMessageAlarm"
class TimedMessageQueue(val context : Context, val alarmManager: AlarmManager, val sendEntryLambda : (Entry) -> Unit) {
    val queuedEntries : ArrayList<Entry> = ArrayList<Entry>()
//    val queuedPendings : ArrayList<PendingIntent> = ArrayList<PendingIntent>()
    var receiver : BroadcastReceiver? = null
    var requestCode : Int = 0

    fun queueSendEntry(entry : Entry, minutesToSend : Long){
        requestCode += 1
        if(receiver == null){
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    sendNextEntryInQueue()
                }
            }

            val filter = IntentFilter(ALARM_ACTION)
            context.registerReceiver(receiver, filter)
        }
        val intent = Intent(ALARM_ACTION)

        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
//        queuedPendings.add(pendingIntent)
        queuedEntries.add(entry)
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
    fun sendNextEntryInQueue(){
//        if(queuedPendings.size > 0){
//            queuedPendings.removeFirstOrNull()
//        }
        if(queuedEntries.size > 0){
            val entryToSend : Entry = queuedEntries.removeFirst()
            sendEntryLambda(entryToSend)
        }
    }
    fun sendAllEntries(){
        for(entry : Entry in queuedEntries){
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