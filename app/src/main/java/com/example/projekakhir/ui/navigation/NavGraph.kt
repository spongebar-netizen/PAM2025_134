package com.example.projekakhir.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projekakhir.data.model.User
import com.example.projekakhir.ui.screens.admin.AdminDashboardScreen
import com.example.projekakhir.ui.screens.admin.StudentListScreen
import com.example.projekakhir.ui.screens.auth.LoginScreen
import com.example.projekakhir.ui.screens.auth.RegisterScreen
import com.example.projekakhir.ui.screens.student.StudentDashboardScreen
import com.example.projekakhir.ui.viewmodel.AdminViewModel
import com.example.projekakhir.ui.viewmodel.AuthViewModel
import com.example.projekakhir.ui.viewmodel.StudentViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    studentViewModel: StudentViewModel,
    adminViewModel: AdminViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    if (role == "student") navController.navigate(Screen.StudentDashboard.route)
                    else navController.navigate(Screen.AdminDashboard.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.StudentDashboard.route) {
            val user by authViewModel.currentUser.collectAsState()
            
            if (user != null) {
                StudentDashboardScreen(user = user!!, viewModel = studentViewModel) {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                }
            } else {
                // Redirect ke login jika user null (safety)
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                }
            }
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController = navController) {
                authViewModel.logout()
                navController.navigate(Screen.Login.route) { popUpTo(0) }
            }
        }

        composable(Screen.StudentList.route) {
            StudentListScreen(viewModel = adminViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    // Masuk ke dashboard setelah register sukses
                    navController.navigate(Screen.StudentDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}