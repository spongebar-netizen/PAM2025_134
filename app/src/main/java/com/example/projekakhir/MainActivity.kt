package com.example.projekakhir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.projekakhir.data.local.AppDatabase
import com.example.projekakhir.data.remote.FirebaseDataSource
import com.example.projekakhir.data.repository.AttendanceRepository
import com.example.projekakhir.ui.navigation.AppNavigation
import com.example.projekakhir.ui.theme.projekakhirTheme
import com.example.projekakhir.ui.viewmodel.AdminViewModel
import com.example.projekakhir.ui.viewmodel.AuthViewModel
import com.example.projekakhir.ui.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        // Manual Dependency Injection (Sederhana tanpa Hilt)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "projekakhir.db").build()
        val dao = db.attendanceDao()
        val remote = FirebaseDataSource()
        val repository = AttendanceRepository(dao, remote)

        val authViewModel = AuthViewModel()
        val adminViewModel = AdminViewModel() // Menggunakan Firebase langsung

        // Factory untuk StudentViewModel karena butuh Repository
        val studentViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StudentViewModel(repository, application) as T
            }
        })[StudentViewModel::class.java]

        setContent {
            projekakhirTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    authViewModel = authViewModel,
                    studentViewModel = studentViewModel,
                    adminViewModel = adminViewModel
                )
            }
        }
    }
}