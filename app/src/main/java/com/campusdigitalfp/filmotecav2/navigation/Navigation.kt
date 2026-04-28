package com.campusdigitalfp.filmotecav2.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campusdigitalfp.filmotecav2.screens.AboutScreen
import com.campusdigitalfp.filmotecav2.screens.FilmDataScreen
import com.campusdigitalfp.filmotecav2.screens.FilmEditScreen
import com.campusdigitalfp.filmotecav2.screens.FilmListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") { FilmListScreen(navController) }
        composable("data/{filmIndex}") { backStackEntry ->
            val filmIndex = backStackEntry.arguments?.getString("filmIndex")?.toIntOrNull()
            filmIndex?.let {
                FilmDataScreen(navController, filmIndex = it)
            }
        }
        composable("edit/{filmIndex}") { backStackEntry ->
            val filmIndex = backStackEntry.arguments?.getString("filmIndex")?.toIntOrNull()
            filmIndex?.let {
                FilmEditScreen(navController, filmIndex = it)
            }
        }
        composable("about") { AboutScreen(navController) }
    }
}
