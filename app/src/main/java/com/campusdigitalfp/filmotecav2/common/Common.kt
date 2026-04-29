package com.campusdigitalfp.filmotecav2.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmotecav2.model.Film
import com.campusdigitalfp.filmotecav2.viewmodel.AuthViewModel
import com.campusdigitalfp.filmotecav2.viewmodel.FilmViewModel

@Composable
fun Boton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier, enabled: Boolean = true){
    Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilmTopAppBar(
    navController: NavHostController,
    principal: Boolean = false,
    editar: Boolean = false,
    selectedFilms: MutableList<Film> = mutableListOf(),
    isActionMode: Boolean = false,
    filmViewModel: FilmViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onActionModeChange: (Boolean) -> Unit = {}
) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Box(
                modifier = Modifier.clickable(onClick = {
                    navController.navigate("list") {
                        popUpTo("list") { inclusive = true }
                    }
                })
            ) {
                Text("Filmoteca", maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        navigationIcon = {
            if (!principal) {
                IconButton(onClick = {
                    if (editar) {
                        navController.previousBackStackEntry?.savedStateHandle?.set("key_result", "RESULT_CANCELED")
                    }
                    navController.popBackStack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                }
            }
        },
        actions = {
            if (principal) {
                if (isActionMode) {
                    IconButton(onClick = {
                        selectedFilms.forEach { film ->
                            filmViewModel.deleteFilm(film.id) // Usamos filmViewModel para borrar
                        }
                        selectedFilms.clear()
                        onActionModeChange(false)
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Borrar seleccionados")
                    }
                }
                MenuDesplegable(navController, filmViewModel, authViewModel)
            }
        }
    )
}

@Composable
fun MenuDesplegable(
    navController: NavHostController,
    filmViewModel: FilmViewModel,
    authViewModel: AuthViewModel
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menú opciones")
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Añadir película") },
            onClick = {
                val defaultFilm = Film().apply {
                    title = "Nueva Película"
                    director = "Director"
                    image = "icono_pelicula"
                    year = 2024
                }
                filmViewModel.addFilm(defaultFilm)
                expanded = false
            }
        )

        DropdownMenuItem(
            text = { Text("Acerca de") },
            onClick = {
                expanded = false
                navController.navigate("about")
            }
        )

        HorizontalDivider()

        DropdownMenuItem(
            text = { Text("Cerrar Sesión") },
            onClick = {
                expanded = false
                filmViewModel.clear()
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}