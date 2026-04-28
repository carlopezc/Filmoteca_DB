package com.campusdigitalfp.filmotecav2.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmotecav2.model.Film
import com.campusdigitalfp.filmotecav2.R
import com.campusdigitalfp.filmotecav2.model.FilmDataSource.films

@Composable
fun Boton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier){
    Button(onClick = onClick, modifier = modifier) {
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
                Text(
                    "Filmoteca",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            if (!principal) {
                IconButton(onClick = {
                    if (editar) {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "key_result",
                            "RESULT_CANCELED"
                        )
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
                        films.removeAll(selectedFilms)
                        selectedFilms.clear()
                        onActionModeChange(false)
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Borrar seleccionados")
                    }
                }
                MenuDesplegable(navController)
            }
        }
    )
}



@Composable
fun MenuDesplegable(navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Localized description"
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(onClick = {
            val defaultFilm = Film().apply {
                id = films.size
                title = "Película por defecto"
                director = "Director Desconocido"
                imageResId = R.drawable.icono_pelicula
                comments = "Esta es una película de ejemplo para la aplicación."
                format = Film.FORMAT_DVD
                genre = Film.GENRE_ACTION
                imdbUrl = "http://www.imdb.com"
                year = 2024
            }

            films.add(defaultFilm)

            expanded = false
        }, text = { Text("Añadir película") })
        DropdownMenuItem(onClick = {
            navController.navigate("about")
        }, text = { Text("Acerca de") })
    }
}