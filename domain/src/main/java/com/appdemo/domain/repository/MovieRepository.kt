package com.appdemo.domain.repository

import com.appdemo.core.error.Failure
import com.appdemo.core.functional.Either
import com.appdemo.domain.model.Genre
import com.appdemo.domain.model.MovieListItemModel

interface MovieRepository {

    suspend fun getMovieLList(from: Int, limit: Int, genre:String?): Either<Failure, List<MovieListItemModel>>
    suspend fun getMovieGenres(): Either<Failure, List<Genre>>
}