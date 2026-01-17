package com.example.projekakhir.ui.screens.admin


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.projekakhir.data.model.Attendance
import com.example.projekakhir.ui.components.AttendanceCard
import com.example.projekakhir.ui.viewmodel.AdminViewModel

@Composable
fun StudentListScreen(viewModel: AdminViewModel) {
    val attendanceList by viewModel.attendanceList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var currentAttendance by remember { mutableStateOf<Attendance?>(null) } // Null means creating new
    
    // Map State
    var showMapDialog by remember { mutableStateOf(false) }
    var mapCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var mapStudentName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchLiveAttendance()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                currentAttendance = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Attendance")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    "Manajemen Absensi",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    placeholder = { Text("Cari Nama atau NIM...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true
                )
            }
            items(attendanceList) { item ->
                AttendanceCard(
                    item = item,
                    onEdit = {
                        currentAttendance = item
                        showDialog = true
                    },
                    onDelete = {
                         viewModel.deleteAttendance(item.id)
                    },
                    onViewMap = {
                        val parts = item.location.split(",")
                        if (parts.size == 2) {
                            val lat = parts[0].trim().toDoubleOrNull()
                            val lng = parts[1].trim().toDoubleOrNull()
                            if (lat != null && lng != null) {
                                mapCoordinates = lat to lng
                                mapStudentName = item.name
                                showMapDialog = true
                            }
                        }
                    }
                )
            }
        }

        if (showMapDialog && mapCoordinates != null) {
             com.example.projekakhir.ui.components.LocationMapDialog(
                 latitude = mapCoordinates!!.first,
                 longitude = mapCoordinates!!.second,
                 studentName = mapStudentName,
                 onDismiss = { showMapDialog = false }
             )
        }

        if (showDialog) {
            AttendanceDialog(
                attendance = currentAttendance,
                onDismiss = { showDialog = false },
                onConfirm = { attendance ->
                    if (currentAttendance == null) {
                        viewModel.addAttendance(attendance)
                    } else {
                        viewModel.updateAttendance(attendance)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AttendanceDialog(
    attendance: Attendance?,
    onDismiss: () -> Unit,
    onConfirm: (Attendance) -> Unit
) {
    var name by remember { mutableStateOf(attendance?.name ?: "") }
    var nim by remember { mutableStateOf(attendance?.nim ?: "") }
    var status by remember { mutableStateOf(attendance?.status ?: "Hadir") }
    var location by remember { mutableStateOf(attendance?.location ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = if (attendance == null) "Tambah Data" else "Edit Data", style = MaterialTheme.typography.titleLarge)
                
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") })
                OutlinedTextField(value = nim, onValueChange = { nim = it }, label = { Text("NIM") })
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") })
                // Location and others can be added similarly

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Button(onClick = {
                        val newAttendance = attendance?.copy(
                            name = name,
                            nim = nim,
                            status = status,
                            // Keep existing or default
                        ) ?: Attendance(
                            id = "", // Will be set by ViewModel for new
                            name = name,
                            nim = nim,
                            status = status,
                            location = location,
                            timestamp = System.currentTimeMillis(),
                            isVerified = true
                        )
                        onConfirm(newAttendance)
                    }) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}