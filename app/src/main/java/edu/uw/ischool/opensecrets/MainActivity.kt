package edu.uw.ischool.opensecrets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val messager : ContactMessager = ContactMessager()
        messager.getContactList(this)
        for(contact : Contact in messager.contactList){
            Log.i("MAIN ACTIVITY", "${contact.name}: ${contact.phoneNumber}")
        }
//        Log.i("MAIN ACTIVITY", "${messager.contactList}")
    }
}