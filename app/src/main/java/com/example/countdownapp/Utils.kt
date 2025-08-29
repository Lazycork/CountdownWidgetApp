package com.example.countdownapp
import java.text.SimpleDateFormat
import java.util.*
object Utils { fun formatDateTime(millis: Long): String { val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()); return sdf.format(Date(millis)) } }
