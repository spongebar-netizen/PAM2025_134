package com.example.projekakhir.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.projekakhir.data.model.Attendance
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance().collection("attendance")
    
    // Raw list from Firestore
    private val _originalList = MutableStateFlow<List<Attendance>>(emptyList())
    
    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Filtered list exposed to UI
    private val _attendanceList = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceList = _attendanceList.asStateFlow()
    
    init {
        // Observe changes to query or list to update filtered list
        // Note: In a real app we might use combine, but for simplicity we'll trigger manually
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterList()
    }

    private fun filterList() {
        val query = _searchQuery.value.lowercase()
        val list = _originalList.value
        
        if (query.isEmpty()) {
            _attendanceList.value = list
        } else {
            _attendanceList.value = list.filter {
                it.name.lowercase().contains(query) || it.nim.contains(query)
            }
        }
    }

    fun fetchLiveAttendance() {
        db.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        val timestampObj = doc.get("timestamp")
                        val finalTimestamp: Long = when (timestampObj) {
                            is Timestamp -> timestampObj.toDate().time
                            is Long -> timestampObj
                            else -> System.currentTimeMillis()
                        }

                        Attendance(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            nim = doc.getString("nim") ?: "",
                            status = doc.getString("status") ?: "Hadir",
                            location = doc.getString("location") ?: "",
                            timestamp = finalTimestamp,
                            isVerified = doc.getBoolean("isVerified") ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                _originalList.value = list.sortedByDescending { it.timestamp }
                filterList() // Update filtered list
            }
        }
    }

    fun addAttendance(attendance: Attendance) {
        val newDoc = db.document()
        val data = attendance.copy(id = newDoc.id)
        newDoc.set(data)
    }

    fun updateAttendance(attendance: Attendance) {
        db.document(attendance.id).set(attendance)
    }

    fun deleteAttendance(attendanceId: String) {
        db.document(attendanceId).delete()
    }
}