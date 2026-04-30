package com.campusdigitalfp.filmotecav2.repository

import com.campusdigitalfp.filmotecav2.model.Film
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration
import android.net.Uri
import com.campusdigitalfp.filmotecav2.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class FilmRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var snapshotListener: ListenerRegistration? = null

    private fun getUserCollection() = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid).collection("films")
    }

    fun listenToFilms(onUpdate: (List<Film>) -> Unit) {
        snapshotListener?.remove()

        val collection = getUserCollection()

        if (collection == null) {
            onUpdate(emptyList())
            return
        }

        snapshotListener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onUpdate(emptyList())
                return@addSnapshotListener
            }

            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Film::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            onUpdate(items)
        }
    }

    suspend fun addFilm(film: Film) {
        val collection = getUserCollection() ?: return

        try {
            if (film.id.isEmpty() || film.id.all { it.isDigit() }) {
                collection.add(film).await()
            } else {
                collection.document(film.id).set(film).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteFilm(id: String) {
        val collection = getUserCollection() ?: return
        try {
            collection.document(id).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearListener() {
        snapshotListener?.remove()
        snapshotListener = null
    }

    suspend fun uploadImage(imageUri: Uri): String? {
        val file = File(imageUri.path ?: return null)
        return try {
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)
            val response = apiService.uploadImage(multipartBody)

            if (response.isSuccessful) {
                val jsonResponse = response.body()?.string()
                val jsonObject = JSONObject(jsonResponse ?: "")
                jsonObject.optString("url", null)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}