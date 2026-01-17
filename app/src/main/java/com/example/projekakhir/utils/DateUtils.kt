package com.example.projekakhir.utils


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
        return sdf.format(Date())
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("id-ID"))
        return sdf.format(Date(timestamp))
    }
    
    // Alias for formatDate to match usage in StudentDashboardScreen
    fun formatDate(timestamp: Long): String {
        return formatTimestamp(timestamp)
    }
}