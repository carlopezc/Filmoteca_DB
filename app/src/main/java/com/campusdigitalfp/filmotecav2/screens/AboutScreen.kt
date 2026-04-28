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

@Composable
fun AboutScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            FilmTopAppBar(
                navController = navController
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center, // Centrar verticalmente
            horizontalAlignment = Alignment.CenterHorizontally // Centrar horizontalmente
        ) {
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


