package com.example.projekakhir.ui.screens.student


import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.projekakhir.ui.components.CustomButton
import com.example.projekakhir.ui.components.LoadingDialog
import com.example.projekakhir.ui.viewmodel.StudentViewModel
import com.example.projekakhir.utils.Resource
import com.example.projekakhir.utils.DateUtils // Assuming this exists or will use simple formatter

import com.example.projekakhir.data.model.User

@Composable
fun StudentDashboardScreen(user: User, viewModel: StudentViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current
    val checkInState by viewModel.checkInState.collectAsState()
    val historyList by viewModel.historyList.collectAsState()
    var statusText by remember { mutableStateOf("Siap Presensi") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.performCheckIn(user.nim, user.name)
            } else {
                Toast.makeText(context, "Izin lokasi diperlukan untuk absensi", Toast.LENGTH_LONG).show()
                statusText = "Gagal: Izin Lokasi Ditolak"
            }
        }
    )

    LaunchedEffect(checkInState) {
        when(val state = checkInState) {
            is Resource.Success -> {
                statusText = "Presensi Tercatat"
                dialogMessage = state.data ?: "Presensi Berhasil"
                showDialog = true
                viewModel.resetCheckInState()
            }
            is Resource.Error -> {
                statusText = "Gagal: ${state.message}"
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetCheckInState()
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchHistory(user.nim)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Status Lokasi") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (checkInState is Resource.Loading) {
        LoadingDialog()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Halo, ${user.name}", style = MaterialTheme.typography.headlineMedium)
        Text("NIM: ${user.nim}", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(statusText, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))

        CustomButton(text = "CHECK-IN SEKARANG") {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                 viewModel.performCheckIn(user.nim, user.name)
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout) { Text("Logout") }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Riwayat Absensi Anda", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(300.dp), // Limit height
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(historyList) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = com.example.projekakhir.utils.DateUtils.formatDate(item.timestamp), style = MaterialTheme.typography.bodySmall)
                         Text(text = item.status, style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }
    }
}