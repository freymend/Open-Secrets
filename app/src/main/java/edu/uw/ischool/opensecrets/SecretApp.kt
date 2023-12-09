package edu.uw.ischool.opensecrets

import android.app.Application
import edu.uw.ischool.opensecrets.util.JournalManager
import edu.uw.ischool.opensecrets.util.OptionManager


class SecretApp : Application() {
    lateinit var journalManager: JournalManager
    lateinit var optionManager: OptionManager
    override fun onCreate() {
        journalManager = JournalManager(this)
        optionManager = OptionManager(this)
        super.onCreate()
    }

}