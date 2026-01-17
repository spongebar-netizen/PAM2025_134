package com.example.projekakhir.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekakhir.data.model.Attendance // ADD Import
import com.example.projekakhir.data.repository.AttendanceRepository
import com.example.projekakhir.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Menggunakan AndroidViewModel untuk mendapatkan akses aman ke Application Context.
 * Ini adalah cara yang direkomendasikan untuk menggunakan Context di dalam ViewModel.
 */
class StudentViewModel(
    private val repository: AttendanceRepository,
    private val application: Application // Inject Application context
) : AndroidViewModel(application) {

    private val _checkInState = MutableStateFlow<Resource<String>?>(null)
    val checkInState: StateFlow<Resource<String>?> = _checkInState.asStateFlow()

    private val _historyList = MutableStateFlow<List<Attendance>>(emptyList())
    val historyList: StateFlow<List<Attendance>> = _historyList.asStateFlow()

    /**
     * Fungsi ini tidak lagi memerlukan Context dari UI.
     * Semua logika yang membutuhkan context (seperti lokasi) sudah di-handle di repository.
     */
    fun performCheckIn(nim: String, name: String) {
        viewModelScope.launch {
            // Memulai proses dengan state Loading
            _checkInState.value = Resource.Loading()

            // Memanggil fungsi di repository yang sekarang bertanggung jawab atas seluruh proses
            val result = repository.performCheckIn(
                application.applicationContext, // Menggunakan application context yang aman
                nim = nim,
                name = name
            )
            _checkInState.value = result

            // Refresh history if success
            if (result is Resource.Success) {
                fetchHistory(nim)
            }
        }
    }

    fun fetchHistory(nim: String) {
        viewModelScope.launch {
            _historyList.value = repository.getHistoryByNim(nim)
        }
    }

    /**
     * Fungsi untuk mereset state setelah operasi selesai (misalnya setelah Snackbar ditampilkan).
     * Ini mencegah state (misalnya pesan error) ditampilkan berulang kali saat terjadi rekonfigurasi UI.
     */
    fun resetCheckInState() {
        _checkInState.value = null
    }
}