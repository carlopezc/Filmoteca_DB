package com.campusdigitalfp.filmotecav2.model

import androidx.compose.runtime.mutableStateListOf
import com.campusdigitalfp.filmotecav2.R

object FilmDataSource {
    val films = mutableStateListOf<Film>()

    init {
        // Primera película: Harry Potter y la piedra filosofal
        val f1 = Film()
        f1.id = films.size.toString()
        f1.title = "Harry Potter y la piedra filosofal"
        f1.director = "Chris Columbus"
        f1.image = "harry_potter_y_la_piedra_filosofal"
        f1.comments = "Una aventura mágica en Hogwarts."
        f1.format ="DVD"
        f1.genre = "Accion"// Cambia según corresponda
        f1.imdbUrl = "http://www.imdb.com/title/tt0241527"
        f1.year = 2001
        films.add(f1)

        // Segunda película: Regreso al futuro
        val f2 = Film()
        f2.id = films.size.toString()
        f2.title = "Regreso al futuro"
        f2.director = "Robert Zemeckis"
        f2.image= "regreso_al_futuro"
        f2.comments = ""
        f2.format ="Digital"
        f2.genre = "Ciencia Ficcion"
        f2.imdbUrl = "http://www.imdb.com/title/tt0088763"
        f2.year = 1985
        films.add(f2)

        // Tercera película: El rey león
        val f3 = Film()
        f3.id = films.size.toString()
        f3.title = "El rey león"
        f3.director = "Roger Allers, Rob Minkoff"
        f3.image = "el_rey_leon"
        f3.comments = "Una historia de crecimiento y responsabilidad."
        f3.format = "Blu-ray"
        f3.genre = "Animación" // Cambia según corresponda
        f3.imdbUrl = "http://www.imdb.com/title/tt0110357"
        f3.year = 1994
        films.add(f3)

        // Cuarta película: Matrix
        val f4 = Film()
        f4.id = films.size.toString()
        f4.title = "Matrix"
        f4.director = "Lana Wachowski, Lilly Wachowski"
        f4.image= "matrix"
        f4.comments = "Revolucionaria película de ciencia ficción."
        f4.format = "Blu-ray"
        f4.genre = "Ciencia ficción"
        f4.imdbUrl = "http://www.imdb.com/title/tt0133093"
        f4.year = 1999
        films.add(f4)

        // Quinta película: Titanic
        val f5 = Film()
        f5.id = films.size.toString()
        f5.title = "Titanic"
        f5.director = "James Cameron"
        f5.image = "titanic"
        f5.comments = "Un clásico romántico y dramático."
        f5.format = "DVD"
        f5.genre = "Drama"
        f5.imdbUrl = "http://www.imdb.com/title/tt0120338"
        f5.year = 1997
        films.add(f5)

        // Sexta película: Inception
        val f6 = Film()
        f6.id = films.size.toString()
        f6.title = "Inception"
        f6.director = "Christopher Nolan"
        f6.image = "inception"
        f6.comments = "Un thriller psicológico con capas de realidad."
        f6.format = "Blu-ray"
        f6.genre = "Ciencia ficción"
        f6.imdbUrl = "http://www.imdb.com/title/tt1375666"
        f6.year = 2010
        films.add(f6)
    }
}