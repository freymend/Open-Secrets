package edu.uw.ischool.opensecrets.model

import kotlinx.coroutines.selects.SelectInstance
import java.util.Date

data class Entry(
    val title: String,
    val color: String,
    val text: String,
    val dataCreated: Date
)
