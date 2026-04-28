package com.campusdigitalfp.filmotecav2.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmotecav2.screens.AboutScreen
import com.campusdigitalfp.filmotecav2.screens.FilmDataScreen
import com.campusdigitalfp.filmotecav2.screens.FilmEditScreen
import com.campusdigitalfp.filmotecav2.screens.FilmListScreen
import com.campusdigitalfp.filmotecav2.viewmodel.FilmViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: FilmViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") { FilmListScreen(navController) }
        composable("data/{filmId}") { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("filmId")
            filmId?.let {
                FilmDataScreen(navController, filmId = it, viewModel = viewModel)
            }
        }
        composable("edit/{filmId}") { backStackEntry ->
            val filmId = backStackEntry.arguments?.getString("filmId")
            filmId?.let {
                FilmEditScreen(navController, filmId = it, viewModel = viewModel)
            }
        }
        composable("about") { AboutScreen(navController, viewModel = viewModel) }
    }
}
