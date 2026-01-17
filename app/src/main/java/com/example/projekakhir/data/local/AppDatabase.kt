package com.example.projekakhir.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.projekakhir.data.local.entity.AttendanceEntity

@Database(entities = [AttendanceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
}