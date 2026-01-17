package com.example.projekakhir.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projekakhir.data.local.entity.AttendanceEntity

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance_table ORDER BY timestamp DESC")
    suspend fun getAll(): List<AttendanceEntity>
}