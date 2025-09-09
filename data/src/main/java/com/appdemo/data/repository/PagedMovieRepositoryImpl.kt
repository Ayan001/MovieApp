package com.appdemo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.appdemo.data.paging.MoviesPagingSource
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.domain.repository.MovieRepository
import com.appdemo.domain.repository.PagedMovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PagedMovieRepositoryImpl @Inject constructor(
    private val repository: MovieRepository
) : PagedMovieRepository {

    override fun getPagedMovies(pageSize: Int,genre:String?): Flow<PagingData<MovieListItemModel>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { MoviesPagingSource(repository, pageSize,genre) }
        ).flow
    }
}