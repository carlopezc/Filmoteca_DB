package com.campusdigitalfp.filmotecav2.model

import androidx.compose.runtime.mutableStateListOf
import com.campusdigitalfp.filmotecav2.R

object FilmDataSource {
    val films = mutableStateListOf<Film>()

    init {
        // Primera película: Harry Potter y la piedra filosofal
        val f1 = Film()
        f1.id = films.size
        f1.title = "Harry Potter y la piedra filosofal"
        f1.director = "Chris Columbus"
        f1.imageResId = R.drawable.harry_potter_y_la_piedra_filosofal
        f1.comments = "Una aventura mágica en Hogwarts."
        f1.format = Film.FORMAT_DVD
        f1.genre = Film.GENRE_ACTION // Cambia según corresponda
        f1.imdbUrl = "http://www.imdb.com/title/tt0241527"
        f1.year = 2001
        films.add(f1)

        // Segunda película: Regreso al futuro
        val f2 = Film()
        f1.id = films.size
        f2.title = "Regreso al futuro"
        f2.director = "Robert Zemeckis"
        f2.imageResId = R.drawable.regreso_al_futuro
        f2.comments = ""
        f2.format = Film.FORMAT_DIGITAL
        f2.genre = Film.GENRE_SCIFI
        f2.imdbUrl = "http://www.imdb.com/title/tt0088763"
        f2.year = 1985
        films.add(f2)

        // Tercera película: El rey león
        val f3 = Film()
        f1.id = films.size
        f3.title = "El rey león"
        f3.director = "Roger Allers, Rob Minkoff"
        f3.imageResId = R.drawable.el_rey_leon
        f3.comments = "Una historia de crecimiento y responsabilidad."
        f3.format = Film.FORMAT_BLURAY
        f3.genre = Film.GENRE_ACTION // Cambia según corresponda
        f3.imdbUrl = "http://www.imdb.com/title/tt0110357"
        f3.year = 1994
        films.add(f3)

        // Cuarta película: Matrix
        val f4 = Film()
        f4.id = films.size
        f4.title = "Matrix"
        f4.director = "Lana Wachowski, Lilly Wachowski"
        f4.imageResId = R.drawable.matrix
        f4.comments = "Revolucionaria película de ciencia ficción."
        f4.format = Film.FORMAT_BLURAY
        f4.genre = Film.GENRE_SCIFI
        f4.imdbUrl = "http://www.imdb.com/title/tt0133093"
        f4.year = 1999
        films.add(f4)

        // Quinta película: Titanic
        val f5 = Film()
        f5.id = films.size
        f5.title = "Titanic"
        f5.director = "James Cameron"
        f5.imageResId = R.drawable.titanic
        f5.comments = "Un clásico romántico y dramático."
        f5.format = Film.FORMAT_DVD
        f5.genre = Film.GENRE_DRAMA
        f5.imdbUrl = "http://www.imdb.com/title/tt0120338"
        f5.year = 1997
        films.add(f5)

        // Sexta película: Inception
        val f6 = Film()
        f6.id = films.size
        f6.title = "Inception"
        f6.director = "Christopher Nolan"
        f6.imageResId = R.drawable.inception
        f6.comments = "Un thriller psicológico con capas de realidad."
        f6.format = Film.FORMAT_BLURAY
        f6.genre = Film.GENRE_SCIFI
        f6.imdbUrl = "http://www.imdb.com/title/tt1375666"
        f6.year = 2010
        films.add(f6)
    }
}