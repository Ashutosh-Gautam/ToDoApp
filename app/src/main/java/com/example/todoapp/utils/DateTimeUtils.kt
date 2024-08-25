package com.example.todoapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.toDateTime(): CharSequence {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val utcDate = sdf.parse(this)

    val newSdf = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())
    newSdf.timeZone = TimeZone.getDefault()
    return utcDate?.let { newSdf.format(it) } ?: "Date not available"
}

fun getCurrentUTCTimestamp(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}