package com.campusdigitalfp.filmotecav2.repository

import com.campusdigitalfp.filmotecav2.model.Film
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FilmRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Guardamos la referencia del listener para poder cerrarlo si es necesario
    private var snapshotListener: ListenerRegistration? = null

    /**
     * Obtiene la referencia a la subcolección de películas del usuario actual.
     * Estructura: users -> {userId} -> films
     */
    private fun getUserCollection() = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid).collection("films")
    }

    fun listenToFilms(onUpdate: (List<Film>) -> Unit) {
        // Limpiamos cualquier listener previo para evitar duplicados
        snapshotListener?.remove()

        val collection = getUserCollection()

        if (collection == null) {
            onUpdate(emptyList())
            return
        }

        // Iniciamos la escucha en tiempo real
        snapshotListener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Si hay un error (ej. permisos), devolvemos lista vacía
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
        // Intentamos obtener la colección. Si es null, reintentamos brevemente
        // por si el objeto Auth aún está cargando el perfil
        val collection = getUserCollection() ?: return

        try {
            if (film.id.isEmpty() || film.id.all { it.isDigit() }) {
                // Crea un documento nuevo con ID automático
                collection.add(film).await()
            } else {
                // Actualiza el documento existente con su ID de Firestore
                collection.document(film.id).set(film).await()
            }
        } catch (e: Exception) {
            // Log de error opcional
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

    // Función para limpiar recursos cuando el usuario cierra sesión
    fun clearListener() {
        snapshotListener?.remove()
        snapshotListener = null
    }
}