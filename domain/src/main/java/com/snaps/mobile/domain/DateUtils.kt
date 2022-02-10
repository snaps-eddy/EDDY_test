package com.snaps.mobile.domain

import java.text.SimpleDateFormat
import java.util.*

fun Date.getStringDate(): String {
    val date = Date(System.currentTimeMillis())
    val strCurYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
    val strCurMonth = SimpleDateFormat("MM", Locale.getDefault()).format(date)
    val strCurDay = SimpleDateFormat("dd", Locale.getDefault()).format(date)
    return strCurYear + strCurMonth + strCurDay
}