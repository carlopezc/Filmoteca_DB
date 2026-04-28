package com.campusdigitalfp.filmotecav2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.campusdigitalfp.filmotecav2.navigation.Navigation
import com.campusdigitalfp.filmotecav2.ui.theme.FilmotecaV2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmotecaV2Theme {
                Navigation()
            }
        }
    }
}
