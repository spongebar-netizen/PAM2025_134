package com.example.projekakhir.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdminDashboardScreen(navController: NavController, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Menu Admin")
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("student_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lihat Daftar Kehadiran")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogout) { Text("Logout") }
    }
}