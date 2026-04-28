package com.campusdigitalfp.filmotecav2.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmotecav2.model.Film
import com.campusdigitalfp.filmotecav2.model.FilmDataSource.films
import com.campusdigitalfp.filmotecav2.R
import com.campusdigitalfp.filmotecav2.common.Boton
import com.campusdigitalfp.filmotecav2.common.FilmTopAppBar

@Composable
fun FilmEditScreen(navController: NavHostController, filmIndex: Int) {
    val film = films[filmIndex]

    BackHandler {
        navController.previousBackStackEntry?.savedStateHandle?.set("key_result", "RESULT_CANCELED")
        navController.popBackStack()
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        FilmTopAppBar(navController, principal = false, editar = true)
    }) { innerPadding ->
        EditorFilm(innerPadding, navController, film, filmIndex)
    }
}

@Composable
fun EditorFilm(
    paddingValues: PaddingValues, navController: NavHostController, film: Film, filmIndex: Int
) {
    var titulo by remember { mutableStateOf(film.title ?: "") }
    var director by remember { mutableStateOf(film.director ?: "") }
    var anyo by remember { mutableStateOf(film.year.toString()) }
    var url by remember { mutableStateOf(film.imdbUrl ?: "") }
    val imagen = film.imageResId
    var comentarios by remember { mutableStateOf(film.comments ?: "") }

    var expandedGenero by remember { mutableStateOf(false) }
    var expandedFormato by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val generoList = context.resources.getStringArray(R.array.genero_list).toList()
    val formatoList = context.resources.getStringArray(R.array.formato_list).toList()


    var genero by remember { mutableIntStateOf(film.genre) }
    var formato by remember { mutableIntStateOf(film.format) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(imagen),
                contentDescription = "Icono película",
                modifier = Modifier
                    .padding(4.dp)
                    .size(70.dp),
            )
            Button(
                onClick = { /*No implementado*/ },
                modifier = Modifier
                    .weight(1f)
                    .padding(1.dp)
            ) {
                Text("Capturar fotografía")
            }
            Button(
                onClick = { /*No implementado*/ },
                modifier = Modifier
                    .weight(1f)
                    .padding(1.dp)
            ) {
                Text("Seleccionar imagen")
            }
        }
        Box(modifier = Modifier.padding(5.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                TextField(
                    value = director,
                    onValueChange = { director = it },
                    label = { Text("Director") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                TextField(
                    value = anyo,
                    onValueChange = { anyo = it },
                    label = { Text("Año") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Enlace a IMDB") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Column {
                    Text("Género: ${generoList[genero]}",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { expandedGenero = true })

                    DropdownMenu(expanded = expandedGenero,
                        onDismissRequest = { expandedGenero = false }) {
                        generoList.forEachIndexed { index, option ->
                            DropdownMenuItem(onClick = {
                                genero = index
                                expandedGenero = false
                            }, text = { Text(option) })
                        }
                    }
                }
                Column {
                    Text("Formato: ${formatoList[formato]}",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { expandedFormato = true })

                    DropdownMenu(expanded = expandedFormato,
                        onDismissRequest = { expandedFormato = false }) {
                        formatoList.forEachIndexed { index, option ->
                            DropdownMenuItem(onClick = {
                                formato = index
                                expandedFormato = false
                            }, text = { Text(option) })
                        }
                    }
                }
                TextField(
                    value = comentarios,
                    onValueChange = { comentarios = it },
                    label = { Text("Comentarios") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false
                )
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Boton(
                onClick = {
                    Log.i("Filmoteca", "Edición finalizada. Guardando cambios...")
                    guardarCambios(navController, film, filmIndex, titulo, director, anyo.toIntOrNull() ?: film.year, url, genero, formato, comentarios)
                }, text = "Guardar", modifier = Modifier
                    .weight(1f)
                    .padding(1.dp)
            )
            Boton(
                onClick = {
                    Log.w("Filmoteca", "Edición cancelada.")
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "key_result", "RESULT_CANCELED"
                    )
                    navController.popBackStack()
                }, text = "Cancelar", modifier = Modifier
                    .weight(1f)
                    .padding(1.dp)
            )
        }
    }
}

fun guardarCambios(
    navController: NavHostController,
    film: Film,
    filmIndex: Int,
    titulo: String,
    director: String,
    anyo: Int,
    url: String,
    genero: Int,
    formato: Int,
    comentarios: String
) {
    films[filmIndex] = film.copy(
        title = titulo,
        director = director,
        year = anyo,
        imdbUrl = url,
        genre = genero,
        format = formato,
        comments = comentarios
    )
    navController.previousBackStackEntry?.savedStateHandle?.set("key_result", "RESULT_OK")
    navController.popBackStack()
}
