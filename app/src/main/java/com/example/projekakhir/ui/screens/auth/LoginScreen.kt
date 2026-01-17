package com.example.projekakhir.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.projekakhir.ui.components.CustomButton
import com.example.projekakhir.ui.viewmodel.AuthViewModel
import com.example.projekakhir.utils.Resource

@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: (String) -> Unit, onRegisterClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()
    
    // Kita simpan role sementara yang dipilih user agar tahu harus navigate kemana setelah sukses
    var selectedRole by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                viewModel.resetLoginState()
                selectedRole?.let { onLoginSuccess(it) }
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetLoginState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (Header text)
        Text("AttenGo Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Lengkap") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it },
            label = { Text("NIM / ID Admin") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (loginState is Resource.Loading) {
            CircularProgressIndicator()
        } else {
            CustomButton(text = "Masuk sbg MAHASISWA") {
                if (name.isNotEmpty() && nim.isNotEmpty()) {
                    selectedRole = "student"
                    viewModel.login(name, nim, "student")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(text = "Masuk sbg ADMIN") {
                if (name.isNotEmpty() && nim.isNotEmpty()) {
                    selectedRole = "admin"
                    viewModel.login(name, nim, "admin")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.TextButton(onClick = { onRegisterClick() }) {
            Text("Belum punya akun? Daftar disini")
        }
    }
}