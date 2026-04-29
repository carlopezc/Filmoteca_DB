package com.campusdigitalfp.filmotecav2.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.campusdigitalfp.filmotecav2.R
import com.campusdigitalfp.filmotecav2.common.Boton
import com.campusdigitalfp.filmotecav2.common.FilmTopAppBar
import com.campusdigitalfp.filmotecav2.viewmodel.AuthViewModel
import com.campusdigitalfp.filmotecav2.viewmodel.FilmViewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.DisposableEffect

@Composable
fun AboutScreen(navController: NavHostController, viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            FilmTopAppBar(
                navController = navController,
                principal = false,
                authViewModel = viewModel
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.creado_por),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(5.dp))
            Image(
                painter = painterResource(
                    id = R.drawable.perfil
                ),
                contentDescription = stringResource(R.string.avatar_del_creador),
                Modifier.height(100.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Guía de uso (How-to)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            VideoItem(videoUrl = "http://10.0.2.2/guia.mp4")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Boton(
                    onClick = { abrirPaginaWeb("https://www.fpvirtualaragon.es", context) },
                    text = stringResource(R.string.ir_al_sitio_web),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Boton(
                    onClick = {
                        mandarEmail(
                            context, "eagullof@fpvirtualaragon.es",
                            context.getString(
                                R.string.incidencia_con_filmoteca
                            )
                        )
                    },
                    text = stringResource(R.string.obtener_soporte),
                    modifier = Modifier.weight(1f)
                )
            }
            Boton(
                onClick = { navController.popBackStack() },
                text = stringResource(R.string.volver)
            )
        }
    }
}

@Composable
fun VideoItem(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
            prepare()
            playWhenReady = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(8.dp)
    )
}

fun abrirPaginaWeb(url: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    context.startActivity(intent)
}

fun mandarEmail(context: Context, email: String, asunto: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$email")
        putExtra(Intent.EXTRA_SUBJECT, asunto)
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se encontró una aplicación de correo", Toast.LENGTH_LONG).show()
    }
}


