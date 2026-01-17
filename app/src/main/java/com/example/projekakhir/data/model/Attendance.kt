package com.example.projekakhir.data.model

data class Attendance(
    val id: String = "",
    val nim: String = "",
    val name: String = "",
    val status: String = "Hadir",
    val location: String = "",
    val timestamp: Long = 0L,
    val isVerified: Boolean = false
)