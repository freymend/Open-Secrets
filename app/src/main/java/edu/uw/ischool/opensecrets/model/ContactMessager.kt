package edu.uw.ischool.opensecrets.model

import android.content.ContentResolver
import android.provider.ContactsContract
import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import kotlin.random.Random


class ContactMessager {
    var contactList : ArrayList<Contact> = ArrayList<Contact>()

    private val PROJECTION = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    fun getContactList(context : Context) : ArrayList<Contact> {
        val cr: ContentResolver = context.contentResolver
        val cursor = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null) {
            contactList.clear()
            val mobileNoSet = HashSet<String>()
            try {
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var name: String
                var number: String
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex)
                    number = cursor.getString(numberIndex)
                    number = number.replace(" ", "")
                    if (!mobileNoSet.contains(number)) {
                        contactList.add(Contact(name, number))
                        mobileNoSet.add(number)
                    }
                }
            } finally {
                cursor.close()
            }
        }
        return contactList
    }
    fun sendMessageToRandom(messageText : String, context : Context) : Contact?{
        if(contactList.size > 0){
            val contact : Contact = contactList[Random.nextInt(contactList.size)]
            sendMessageToContact(contact, messageText, context)
            return contact
        }
        return null
    }
    fun sendMessageToContact(contact : Contact, messageText : String, context : Context){
        val sms: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }
        sms.sendTextMessage(contact.phoneNumber, null, messageText, null, null)
    }
//    val messager : ContactMessager = ContactMessager()
//    messager.getContactList(this)
//    for(contact : Contact in messager.contactList){
//        Log.i("MAIN ACTIVITY", "${contact.name}: ${contact.phoneNumber}")
//    }
}

data class Contact(var name: String, var phoneNumber: String)