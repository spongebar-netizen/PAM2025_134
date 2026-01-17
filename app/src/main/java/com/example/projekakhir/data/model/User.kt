package com.example.projekakhir.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val nim: String = "",
    val role: String = "student" // "admin" atau "student"
)