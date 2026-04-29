package com.campusdigitalfp.filmotecav2.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmotecav2.model.Film
import com.campusdigitalfp.filmotecav2.model.FilmDataSource
import com.campusdigitalfp.filmotecav2.R
import coil.compose.AsyncImage
import com.campusdigitalfp.filmotecav2.common.FilmTopAppBar
import com.campusdigitalfp.filmotecav2.model.FilmDataSource.films
import com.campusdigitalfp.filmotecav2.viewmodel.AuthViewModel
import com.campusdigitalfp.filmotecav2.viewmodel.FilmViewModel

@Composable
fun FilmListScreen(navController: NavHostController,
                   filmViewModel: FilmViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
                   authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var isActionMode by remember { mutableStateOf(false) }
    val selectedFilms = remember { mutableStateListOf<Film>() }

    val films by filmViewModel.films.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        FilmTopAppBar(
            navController = navController,
            principal = true,
            editar = false,
            selectedFilms = selectedFilms,
            isActionMode = isActionMode,
            filmViewModel = filmViewModel, // Para añadir o borrar películas
            authViewModel = authViewModel  // Para el cierre de sesión en el menú
        ) {
            isActionMode = it
        }
    }) { innerPadding ->
        FilmListContent(
            films = films.toMutableList(),
            selectedFilms = selectedFilms,
            isActionMode = isActionMode,
            navController = navController,
            innerPadding = innerPadding
        ) { isActionMode = it }
    }
}

@Composable
fun FilmListContent(
    films: List<Film>,
    selectedFilms: MutableList<Film>,
    isActionMode: Boolean,
    navController: NavHostController,
    innerPadding: PaddingValues,
    onActionModeChange: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        items(films) {film ->
            VistaFilm(film = film, onClick = {
                if (isActionMode) {
                    if (selectedFilms.contains(film)) {
                        selectedFilms.remove(film)
                        if (selectedFilms.isEmpty()) {
                            onActionModeChange(false)
                        }
                    } else {
                        selectedFilms.add(film)
                    }
                } else {
                    navController.navigate("data/${film.id}")
                }
            }, onLongClick = {
                selectedFilms.add(film)
                onActionModeChange(true)
            }, isSelected = selectedFilms.contains(film)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VistaFilm(film: Film, onClick: () -> Unit, onLongClick: () -> Unit, isSelected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (film.image.startsWith("http") && !isSelected) {
            AsyncImage(
                model = film.image,
                contentDescription = "Icono",
                modifier = Modifier
                    .padding(0.dp)
                    .size(80.dp)
            )
        } else {
            Image(
                painter = painterResource(if (isSelected) R.drawable.selected else getDrawableId(film.image)),
                contentDescription = "Icono",
                modifier = Modifier
                    .padding(if (isSelected) 15.dp else 0.dp)
                    .size(if (isSelected) 50.dp else 80.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = film.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = film.director, style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun getDrawableId(imageName: String): Int {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    return if (resourceId != 0) resourceId else R.drawable.ic_launcher_background
}

@Composable
fun FilmItem(film: Film) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la película
            Image(
                painter = painterResource(id = getDrawableId(film.image)),
                contentDescription = film.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Detalles del texto
            Column {
                Text(text = film.title, style = MaterialTheme.typography.titleLarge)
                Text(text = "${film.director} (${film.year})", style = MaterialTheme.typography.bodyMedium)
                Text(text = film.genre.toString(), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            }
        }
    }
}