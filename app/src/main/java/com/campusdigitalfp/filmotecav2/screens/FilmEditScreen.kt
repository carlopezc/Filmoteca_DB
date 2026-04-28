package com.campusdigitalfp.filmotecav2.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.room.util.copy
import com.campusdigitalfp.filmotecav2.model.Film
import com.campusdigitalfp.filmotecav2.R
import com.campusdigitalfp.filmotecav2.common.Boton
import com.campusdigitalfp.filmotecav2.common.FilmTopAppBar
import com.campusdigitalfp.filmotecav2.viewmodel.AuthViewModel
import com.campusdigitalfp.filmotecav2.viewmodel.FilmViewModel

@Composable
fun FilmEditScreen(
    navController: NavHostController,
    filmId: String,
    filmViewModel: FilmViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    // Obtenemos la lista de películas de Firebase a través del ViewModel
    val films by filmViewModel.films.collectAsState()
    // Buscamos la película exacta por su ID único
    val film = films.find { it.id == filmId }

    BackHandler {
        navController.previousBackStackEntry?.savedStateHandle?.set("key_result", "RESULT_CANCELED")
        navController.popBackStack()
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        FilmTopAppBar(
            navController = navController,
            principal = false,
            editar = true,
            filmViewModel = filmViewModel,
            authViewModel = authViewModel
        )
    }) { innerPadding ->
        // Solo cargamos el editor si la película ha sido encontrada en la lista
        film?.let {
            EditorFilm(innerPadding, navController, it, filmViewModel)
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

    }
}

@Composable
fun EditorFilm(
    paddingValues: PaddingValues,
    navController: NavHostController,
    film: Film,
    viewModel: FilmViewModel
) {
    // Estados para los campos de texto
    var titulo by remember { mutableStateOf(film.title) }
    var director by remember { mutableStateOf(film.director) }
    var anyo by remember { mutableStateOf(film.year.toString()) }
    var url by remember { mutableStateOf(film.imdbUrl) }
    var comentarios by remember { mutableStateOf(film.comments) }
    val imagen = film.image

    // Estados para los menús desplegables
    var expandedGenero by remember { mutableStateOf(false) }
    var expandedFormato by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val generoList = context.resources.getStringArray(R.array.genero_list).toList()
    val formatoList = context.resources.getStringArray(R.array.formato_list).toList()

    // Buscamos los índices actuales basándonos en el String que viene de Firebase
    var generoIdx by remember {
        mutableIntStateOf(generoList.indexOf(film.genre).coerceAtLeast(0))
    }
    var formatoIdx by remember {
        mutableIntStateOf(formatoList.indexOf(film.format).coerceAtLeast(0))
    }

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
                painter = painterResource(getDrawableId(imagen)),
                contentDescription = "Icono película",
                modifier = Modifier
                    .padding(4.dp)
                    .size(70.dp),
            )
            Button(onClick = { /* Capturar foto - No implementado */ }, modifier = Modifier.weight(1f).padding(1.dp)) {
                Text("Capturar")
            }
            Button(onClick = { /* Seleccionar imagen - No implementado */ }, modifier = Modifier.weight(1f).padding(1.dp)) {
                Text("Galería")
            }
        }

        Box(modifier = Modifier.padding(5.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                TextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                TextField(value = director, onValueChange = { director = it }, label = { Text("Director") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                TextField(
                    value = anyo,
                    onValueChange = { anyo = it },
                    label = { Text("Año") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(value = url, onValueChange = { url = it }, label = { Text("Enlace a IMDB") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                // Selector de Género
                Column {
                    Text("Género: ${generoList[generoIdx]}",
                        modifier = Modifier.padding(16.dp).clickable { expandedGenero = true })
                    DropdownMenu(expanded = expandedGenero, onDismissRequest = { expandedGenero = false }) {
                        generoList.forEachIndexed { index, option ->
                            DropdownMenuItem(onClick = {
                                generoIdx = index
                                expandedGenero = false
                            }, text = { Text(option) })
                        }
                    }
                }

                // Selector de Formato
                Column {
                    Text("Formato: ${formatoList[formatoIdx]}",
                        modifier = Modifier.padding(16.dp).clickable { expandedFormato = true })
                    DropdownMenu(expanded = expandedFormato, onDismissRequest = { expandedFormato = false }) {
                        formatoList.forEachIndexed { index, option ->
                            DropdownMenuItem(onClick = {
                                formatoIdx = index
                                expandedFormato = false
                            }, text = { Text(option) })
                        }
                    }
                }

                TextField(value = comentarios, onValueChange = { comentarios = it }, label = { Text("Comentarios") }, modifier = Modifier.fillMaxWidth(), singleLine = false)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Boton(
                onClick = {
                    val generoSeleccionado = generoList[generoIdx]
                    val formatoSeleccionado = formatoList[formatoIdx]

                    guardarCambios(
                        navController,
                        film,
                        titulo,
                        director,
                        anyo.toIntOrNull() ?: film.year,
                        url,
                        generoSeleccionado,
                        formatoSeleccionado,
                        comentarios,
                        viewModel
                    )
                }, text = "Guardar", modifier = Modifier.weight(1f).padding(1.dp)
            )
            Boton(
                onClick = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("key_result", "RESULT_CANCELED")
                    navController.popBackStack()
                }, text = "Cancelar", modifier = Modifier.weight(1f).padding(1.dp)
            )
        }
    }
}

fun guardarCambios(
    navController: NavHostController,
    film: Film,
    titulo: String,
    director: String,
    anyo: Int,
    url: String,
    genero: String,
    formato: String,
    comentarios: String,
    viewModel: FilmViewModel
) {
    // Creamos una copia con los nuevos datos, manteniendo el mismo ID de Firebase
    val filmActualizada = film.copy(
        title = titulo,
        director = director,
        year = anyo,
        imdbUrl = url,
        genre = genero,
        format = formato,
        comments = comentarios
    )

    // Subimos la actualización a Firestore
    viewModel.addFilm(filmActualizada)

    navController.previousBackStackEntry?.savedStateHandle?.set("key_result", "RESULT_OK")
    navController.popBackStack()
}
