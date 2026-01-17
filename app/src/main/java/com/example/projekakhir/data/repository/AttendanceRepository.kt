package com.example.projekakhir.data.repository


import com.example.projekakhir.data.local.AttendanceDao
import com.example.projekakhir.data.local.entity.AttendanceEntity
import com.example.projekakhir.data.remote.FirebaseDataSource
import com.example.projekakhir.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.example.projekakhir.data.model.Attendance // Ensure this is imported if used in list
import kotlinx.coroutines.tasks.await

class AttendanceRepository(
    private val dao: AttendanceDao,
    private val remote: FirebaseDataSource
) {
    // Fungsi gabungan: Simpan ke Firebase, lalu backup ke Room
    suspend fun checkIn(attendance: Attendance): Resource<Boolean> {
        val remoteResult = remote.uploadAttendance(attendance)

        if (remoteResult is Resource.Success) {
            // Simpan backup lokal
            dao.insert(
                AttendanceEntity(
                    nim = attendance.nim,
                    name = attendance.name, // Added mapping
                    location = attendance.location, // Added mapping
                    status = attendance.status,
                    timestamp = attendance.timestamp,
                    isSynced = true
                )
            )
        }
        return remoteResult
    }


    // Helper to get location as suspend
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private suspend fun awaitLocation(context: android.content.Context): android.location.Location? {
        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
             com.example.projekakhir.utils.LocationUtils.getCurrentLocation(context) { loc ->
                 cont.resume(loc, null)
             }
        }
    }

    // REAL Implementation combining the above
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    suspend fun performCheckIn(context: android.content.Context, nim: String, name: String): Resource<String> {
        val location = awaitLocation(context) ?: return Resource.Error("Gagal dapat lokasi: Pastikan GPS aktif")
        
        val isInside = com.example.projekakhir.utils.LocationUtils.isWithinCampus(location.latitude, location.longitude)
        val statusText = if (isInside) "Hadir" else "Hadir (Diluar Kampus)"
        val userMessage = if (isInside) "anda sedang berada didalam kampus" else "anda sedang berada diluar kampus"
        
        val attendance = Attendance(
            nim = nim,
            name = name,
            status = statusText,
            location = "${location.latitude}, ${location.longitude}",
            timestamp = System.currentTimeMillis(),
            isVerified = isInside
        )

        val result = checkIn(attendance)
        
        return if (result is Resource.Success) {
            Resource.Success(userMessage) 
        } else {
            Resource.Error(result.message ?: "Gagal menyimpan data absensi")
        }
    }

    suspend fun getLocalHistory() = dao.getAll()

    suspend fun getHistoryByNim(nim: String): List<Attendance> {
        return try {
            val snapshot = FirebaseFirestore.getInstance().collection("attendance")
                .whereEqualTo("nim", nim)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                // Basic mapping, similar to AdminViewModel but simplified check
                try {
                     val timestampObj = doc.get("timestamp")
                     val finalTimestamp: Long = if (timestampObj is com.google.firebase.Timestamp) timestampObj.toDate().time else (timestampObj as? Long) ?: System.currentTimeMillis()
                    
                    Attendance(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        nim = doc.getString("nim") ?: "",
                        status = doc.getString("status") ?: "",
                        location = doc.getString("location") ?: "",
                        timestamp = finalTimestamp,
                        isVerified = doc.getBoolean("isVerified") ?: false
                    )
                } catch (e: Exception) { null }
            }.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }
}