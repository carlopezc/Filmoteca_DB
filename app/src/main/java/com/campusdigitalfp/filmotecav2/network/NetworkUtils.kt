package com.campusdigitalfp.filmotecav2.network

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun saveImageToAppFolder(context: Context, imageUri: Uri): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val directory = File(context.filesDir, "FilmotecaImages")

    if (!directory.exists() && !directory.mkdirs()) {
        Log.e("saveImageToAppFolder", "No se pudo crear el directorio")
        return null
    }

    val file = File(directory, "IMG_$timeStamp.jpg")

    return try {
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Uri.fromFile(file)
    } catch (e: IOException) {
        Log.e("saveImageToAppFolder", "Error al guardar la imagen", e)
        null
    }
}

