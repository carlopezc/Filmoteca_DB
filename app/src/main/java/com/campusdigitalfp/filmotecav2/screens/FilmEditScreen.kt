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
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.campusdigitalfp.filmotecav2.network.saveImageToAppFolder
import com.campusdigitalfp.filmotecav2.network.uploadImageToServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FilmEditScreen(
    navController: NavHostController,
    filmId: String,
    filmViewModel: FilmViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val films by filmViewModel.films.collectAsState()
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
    val context = LocalContext.current
    var titulo by remember { mutableStateOf(film.title) }
    var director by remember { mutableStateOf(film.director) }
    var anyo by remember { mutableStateOf(film.year.toString()) }
    var url by remember { mutableStateOf(film.imdbUrl) }
    var comentarios by remember { mutableStateOf(film.comments) }
    var imagen by remember { mutableStateOf(film.image) }

    val scope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            saveImageToAppFolder(context, imageUri!!)?.let { savedUri ->
                isUploading = true
                scope.launch(Dispatchers.IO) {
                    val serverUrl = uploadImageToServer(savedUri, context)
                    if (serverUrl != null) {
                        imagen = serverUrl.toString()
                    }
                    isUploading = false
                }
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploading = true
            scope.launch(Dispatchers.IO) {
                val serverUrl = uploadImageToServer(it, context)
                if (serverUrl != null) {
                    imagen = serverUrl.toString()
                }
                isUploading = false
            }
        }
    }

    val createImageFile: () -> Uri? = {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            null
        }
    }

    var expandedGenero by remember { mutableStateOf(false) }
    var expandedFormato by remember { mutableStateOf(false) }

    val generoList = context.resources.getStringArray(R.array.genero_list).toList()
    val formatoList = context.resources.getStringArray(R.array.formato_list).toList()

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
            Box(contentAlignment = Alignment.Center) {
                if (imagen.startsWith("http")) {
                    AsyncImage(
                        model = imagen,
                        contentDescription = "Icono película",
                        modifier = Modifier.padding(4.dp).size(70.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(getDrawableId(imagen)),
                        contentDescription = "Icono película",
                        modifier = Modifier.padding(4.dp).size(70.dp),
                    )
                }
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(40.dp))
                }
            }
            Button(
                onClick = {
                    if (!hasCameraPermission) {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        createImageFile()?.let { uri ->
                            imageUri = uri
                            cameraLauncher.launch(uri)
                        }
                    }
                },
                modifier = Modifier.weight(1f).padding(1.dp),
                enabled = !isUploading
            ) {
                Text("Capturar")
            }
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f).padding(1.dp),
                enabled = !isUploading
            ) {
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
                        imagen,
                        viewModel
                    )
                }, text = "Guardar", modifier = Modifier.weight(1f).padding(1.dp), enabled = !isUploading
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
    imagen: String,
    viewModel: FilmViewModel
) {
    val filmActualizada = film.copy(
        title = titulo,
        director = director,
        year = anyo,
        imdbUrl = url,
        genre = genero,
        format = formato,
        comments = comentarios,
        image = imagen
    )

    viewModel.addFilm(filmActualizada)

    navController.previousBackStackEntry?.savedStateHandle?.set("key_result", "RESULT_OK")
    navController.popBackStack()
}
