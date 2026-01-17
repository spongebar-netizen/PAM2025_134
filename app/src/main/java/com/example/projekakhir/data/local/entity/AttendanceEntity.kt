package com.example.projekakhir.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_table")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nim: String,
    val name: String, // Added field
    val location: String, // Added field
    val status: String,
    val timestamp: Long,
    val isSynced: Boolean = false
)