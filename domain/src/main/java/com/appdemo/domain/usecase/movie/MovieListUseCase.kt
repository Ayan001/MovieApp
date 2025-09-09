package com.appdemo.domain.usecase.movie

import androidx.paging.PagingData
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.domain.repository.PagedMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MovieListUseCase @Inject constructor(
    private val repository: PagedMovieRepository
) {
    operator fun invoke(pageSize: Int,genre:String?): Flow<PagingData<MovieListItemModel>> {
        return repository.getPagedMovies(pageSize,genre)
    }
}
