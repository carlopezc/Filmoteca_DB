package com.campusdigitalfp.filmotecav2.repository

import com.campusdigitalfp.filmotecav2.model.Film
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
class FilmRepository {
    private val db = FirebaseFirestore.getInstance()
    private val filmsCollection = db.collection("films")

    fun listenToFilms(onUpdate: (List<Film>) -> Unit) {
        filmsCollection.addSnapshotListener { snapshot, _ ->
            val items = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Film::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            onUpdate(items)
        }
    }

    suspend fun addFilm(film: Film) {
        if (film.id.isEmpty() || film.id.all { it.isDigit() }) {
            filmsCollection.add(film).await()
        } else {
            filmsCollection.document(film.id).set(film).await()
        }
    }

    suspend fun deleteFilm(id: String) {
        filmsCollection.document(id).delete().await()
    }
}