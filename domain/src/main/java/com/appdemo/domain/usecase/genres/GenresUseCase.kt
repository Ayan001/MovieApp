package com.appdemo.domain.usecase.genres

import androidx.paging.PagingData
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.domain.repository.MovieRepository
import com.appdemo.domain.repository.PagedMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenresUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke() = repository.getMovieGenres()

}