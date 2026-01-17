package com.example.projekakhir.ui.navigation


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object StudentDashboard : Screen("student_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object StudentList : Screen("student_list")
    object Register : Screen("register")
}