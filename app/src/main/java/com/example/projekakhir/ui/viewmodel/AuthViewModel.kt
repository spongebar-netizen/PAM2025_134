package com.example.projekakhir.ui.viewmodel


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.example.projekakhir.data.model.User
import com.example.projekakhir.utils.Resource

class AuthViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _registerState = MutableStateFlow<Resource<Boolean>?>(null)
    val registerState = _registerState.asStateFlow()

    fun resetRegisterState() {
        _registerState.value = null
    }

    private val _loginState = MutableStateFlow<Resource<Boolean>?>(null)
    val loginState = _loginState.asStateFlow()

    fun resetLoginState() {
        _loginState.value = null
    }

    fun login(name: String, nim: String, role: String) {
        // Special case for Admin login (Hardcoded Bypass)
        if (name.equals("admin", ignoreCase = true) && nim == "1234") {
            if (role != "admin") {
                _loginState.value = Resource.Error("Admin tidak bisa login sebagai Mahasiswa")
                return
            }
            _currentUser.value = User(
                uid = "admin-hardcoded-id",
                name = "Admin",
                nim = "1234",
                email = "admin@attengo.com",
                role = "admin"
            )
            _loginState.value = Resource.Success(true)
            return
        }

        _loginState.value = Resource.Loading()
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("name", name)
            .whereEqualTo("nim", nim)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Ambil user pertama yang cocok
                    val document = documents.documents[0]
                    val user = document.toObject(User::class.java)
                    
                    if (user != null) {
                        if (user.role != role) {
                            _loginState.value = Resource.Error("Akun ini tidak terdaftar sebagai ${if(role == "admin") "Admin" else "Mahasiswa"}")
                        } else {
                            _currentUser.value = user
                            _loginState.value = Resource.Success(true)
                        }
                    } else {
                         _loginState.value = Resource.Error("Gagal memuat data user")
                    }
                } else {
                    _loginState.value = Resource.Error("User tidak ditemukan/salah password")
                }
            }
            .addOnFailureListener { e ->
                _loginState.value = Resource.Error(e.message ?: "Login gagal")
            }
    }

    fun register(name: String, nim: String, email: String) {
        _registerState.value = Resource.Loading()
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        // Cek nama kembar
        db.collection("users")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    _registerState.value = Resource.Error("Nama sudah terdaftar, gunakan nama lain.")
                } else {
                    val newUser = User(
                        uid = java.util.UUID.randomUUID().toString(),
                        name = name,
                        nim = nim,
                        email = email,
                        role = "student"
                    )

                    // Simpan ke Firestore
                    db.collection("users").document(newUser.uid).set(newUser)
                        .addOnSuccessListener {
                            _currentUser.value = newUser
                            _registerState.value = Resource.Success(true)
                        }
                        .addOnFailureListener { e ->
                            _registerState.value = Resource.Error(e.message ?: "Registrasi gagal")
                        }
                }
            }
            .addOnFailureListener { e ->
                _registerState.value = Resource.Error(e.message ?: "Gagal mengecek nama")
            }
    }

    fun logout() {
        _currentUser.value = null
    }
}