package com.campusdigitalfp.filmotecav2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmotecav2.screens.AboutScreen
import com.campusdigitalfp.filmotecav2.screens.FilmDataScreen
import com.campusdigitalfp.filmotecav2.screens.FilmEditScreen
import com.campusdigitalfp.filmotecav2.screens.FilmListScreen
import com.campusdigitalfp.filmotecav2.screens.LoginScreen
import com.campusdigitalfp.filmotecav2.screens.RegisterScreen
import com.campusdigitalfp.filmotecav2.viewmodel.AuthViewModel
import com.campusdigitalfp.filmotecav2.viewmodel.FilmViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    // Creamos las instancias de los ViewModels que compartiremos en toda la navegación
    val authViewModel: AuthViewModel = viewModel()
    val filmViewModel: FilmViewModel = viewModel()

    val startDest = if (authViewModel.isUserLoggedIn()) "list" else "login"

    NavHost(navController = navController, startDestination = startDest) {

        composable("login") {
            LoginScreen(navController, authViewModel)
        }

        composable("register") {
            RegisterScreen(navController, authViewModel)
        }

        composable("list") {
            if (!authViewModel.isUserLoggedIn()) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("list") { inclusive = true }
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    filmViewModel.refresh()
                }
                // CORREGIDO: Pasamos ambos ViewModels
                FilmListScreen(navController, filmViewModel, authViewModel)
            }
        }

        composable("data/{filmId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("filmId")
            if (!authViewModel.isUserLoggedIn()) {
                LaunchedEffect(Unit) { navController.navigate("login") }
            } else {
                id?.let {
                    // CORREGIDO: Pasamos ambos ViewModels
                    FilmDataScreen(navController, it, filmViewModel, authViewModel)
                }
            }
        }

        composable("edit/{filmId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("filmId")
            if (!authViewModel.isUserLoggedIn()) {
                LaunchedEffect(Unit) { navController.navigate("login") }
            } else {
                id?.let {
                    // CORREGIDO: Pasamos ambos ViewModels
                    FilmEditScreen(navController, it, filmViewModel, authViewModel)
                }
            }
        }

        composable("about") {
            // CORREGIDO: Pasamos el AuthViewModel para que la TopAppBar pueda cerrar sesión
            AboutScreen(navController, authViewModel)
        }
    }
}