package com.example.projekakhir.data.remote


import com.example.projekakhir.data.model.Attendance
import com.example.projekakhir.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {
    private val db = FirebaseFirestore.getInstance().collection("attendance")

    suspend fun uploadAttendance(attendance: Attendance): Resource<Boolean> {
        return try {
            kotlinx.coroutines.withTimeout(5000L) {
                // Firestore ID generation
                val document = db.document() 
                val key = document.id
                val dataWithId = attendance.copy(id = key)
                
                // Upload
                document.set(dataWithId).await()
                Resource.Success(true)
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
             Resource.Error("Koneksi timeout. Cek internet/server.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Terjadi kesalahan server")
        }
    }
}