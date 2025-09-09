package com.appdemo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.appdemo.core.error.Failure
import com.appdemo.core.functional.Either
import com.appdemo.data.mapper.GenreMapper
import com.appdemo.data.mapper.MovieListMapper
import com.appdemo.data.paging.MoviesPagingSource
import com.appdemo.data.remote.api.ApiService
import com.appdemo.data.remote.handler.safeApiCall
import com.appdemo.domain.model.Genre
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.domain.repository.MovieRepository
import com.appdemo.domain.repository.PagedMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val movieListMapper: MovieListMapper,
    private val genreMapper: GenreMapper,
) : MovieRepository {



    override suspend fun getMovieLList(
        from: Int,
        limit: Int,
        genre:String?
    ): Either<Failure, List<MovieListItemModel>> =
        safeApiCall(
            apiCall = { apiService.getPagedMovieList(from,genre,limit) },
            mapper = { movieListMapper.map(it) }
        )

    override suspend fun getMovieGenres(): Either<Failure, List<Genre>> =
        safeApiCall(
            apiCall = { apiService.getGenres() },
            mapper = { genreMapper.map(it) }
        )


}