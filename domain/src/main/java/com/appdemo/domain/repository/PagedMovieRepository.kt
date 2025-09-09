package com.appdemo.domain.repository

import androidx.paging.PagingData
import com.appdemo.core.error.Failure
import com.appdemo.core.functional.Either
import com.appdemo.domain.model.MovieListItemModel
import kotlinx.coroutines.flow.Flow

interface PagedMovieRepository {

    fun getPagedMovies(pageSize: Int,genre:String?): Flow<PagingData<MovieListItemModel>>
}