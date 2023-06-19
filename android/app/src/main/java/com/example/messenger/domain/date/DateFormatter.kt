package com.example.messenger.domain.date

import android.text.format.DateUtils
import android.util.Log
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date


const val DD_MM_YYYY_HH_MM = "dd-MM-yyyy HH:mm"
const val HH_MM = "HH:mm"
const val YYY_DD_MM = "yyyy-dd-MM"
const val YYYY_MM_DD_T_HH_MM_SS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSX"

fun String.relativeFormatDate():String{
    if (this.isBlank()) return ""
    val zonedDateTime = toZonedDateTime() ?: return ""
    val time = Date.from(zonedDateTime.toInstant()).time
    val now = System.currentTimeMillis()
    val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
    return ago.toString()
}

fun String.formatDate(pattern:String):String{
    if (this.isBlank()) return ""
    val zonedDateTime = toZonedDateTime() ?: return ""
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return zonedDateTime.format(formatter)
}

fun String.toZonedDateTime():ZonedDateTime?{
    if (this.isBlank()) return null
    return try {
        val instant = Instant.parse(this)
        instant.atZone(ZoneId.systemDefault())
    }catch (e: DateTimeParseException){
        Log.e("toZonedDateTime", e.stackTraceToString())
        null
    }
}

fun dateNowInUTC():String{
    val instant = Instant.now()
    val date = instant.atZone(ZoneId.of("UTC"))
    val formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_Z)
    return date.format(formatter)
}