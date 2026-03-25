package dev.wdona.burntout.shared.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

fun getCurrentDateString(): String {
    return LocalDate.now().toString()
}

fun getCurrentTimestampSeconds(): Long {
    return System.currentTimeMillis() / 1000
}

fun convertTimestampToStringDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val format = SimpleDateFormat("yyyy-MM-dd")
    return format.format(date)
}

