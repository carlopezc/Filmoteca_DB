package com.campusdigitalfp.filmotecav2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusdigitalfp.filmotecav2.model.Film
import com.campusdigitalfp.filmotecav2.repository.FilmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FilmViewModel : ViewModel()   {
    private val repository = FilmRepository()

    private val _films = MutableStateFlow<List<Film>>(emptyList())
    val films: StateFlow<List<Film>> get() = _films
    init {
        listenToFilms()
    }

    private fun listenToFilms() {
        repository.listenToFilms { updatedFilms ->
            _films.value = updatedFilms
        }
    }
    fun addFilm(film: Film) {
        viewModelScope.launch {
            repository.addFilm(film)
        }
    }
    fun deleteFilm(filmId: String) {
        viewModelScope.launch {
            repository.deleteFilm(filmId)
        }
    }
}