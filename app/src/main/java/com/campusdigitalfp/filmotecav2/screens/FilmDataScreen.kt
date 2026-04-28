package com.campusdigitalfp.filmotecav2.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmotecav2.model.Film
import com.campusdigitalfp.filmotecav2.model.FilmDataSource
import com.campusdigitalfp.filmotecav2.R
import com.campusdigitalfp.filmotecav2.common.FilmTopAppBar

@Composable
fun FilmDataScreen(navController: NavHostController, filmIndex: Int) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle ?: return
    ShowResultToast(savedStateHandle)
    val film = FilmDataSource.films[filmIndex]

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        FilmTopAppBar(
            navController = navController
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            VistaFilm(film)
            Box(modifier = Modifier.fillMaxWidth()) {
                Row {
                    Button(
                        onClick = { navController.popBackStack("list", false) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    ) {
                        Text("Volver")
                    }
                    Button(
                        onClick = { navController.navigate("edit/$filmIndex") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    ) {
                        Text("Editar")
                    }
                }
            }
        }
    }
}

@Composable
fun VistaFilm(film: Film) {
    val context = LocalContext.current
    val generoList = context.resources.getStringArray(R.array.genero_list).toList()
    val formatoList = context.resources.getStringArray(R.array.formato_list).toList()

    Column(modifier = Modifier.padding(8.dp)) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                modifier = Modifier
                    .padding(4.dp)
                    .size(150.dp),
                painter = painterResource(film.imageResId),
                contentDescription = "Icono de la película"
            )
            Column {
                Text(
                    text = film.title.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Director:", fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = film.director.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Año:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = film.year.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatoList[film.format] + ", " + generoList[film.genre],
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Button(
            onClick = { abrirPaginaWeb(film.imdbUrl.toString(), context = context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver en IMDB")
        }
        Text(
            text = film.comments.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ShowResultToast(savedStateHandle: SavedStateHandle) {
    val context = LocalContext.current
    val result by savedStateHandle.getLiveData<String>("key_result").observeAsState()

    result?.let {
        LaunchedEffect(it) {
            if (it == "RESULT_OK")
                Toast.makeText(context, "Película actualizada", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context, "Edición cancelada", Toast.LENGTH_SHORT).show()
        }
    }
}

