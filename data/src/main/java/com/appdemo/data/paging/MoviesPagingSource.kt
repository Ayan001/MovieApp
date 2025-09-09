package com.appdemo.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.appdemo.core.error.Failure
import com.appdemo.core.error.getErrorMessage
import com.appdemo.core.error.toThrowable
import com.appdemo.core.functional.Either
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.domain.repository.MovieRepository

class MoviesPagingSource(
    private val repository: MovieRepository,
    private val pageSize: Int,
    private val genre : String?=null,
) : PagingSource<Int, MovieListItemModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieListItemModel> {
            val from = params.key ?: 0

            return try {
                when (val result = repository.getMovieLList(from, pageSize,genre)) {
                    is Either.Right -> {
                        val movies = result.value
                        val nextKey = if (movies.size < pageSize) null else from + pageSize
                        val prevKey = if (from == 0) null else maxOf(from - pageSize, 0)

                        LoadResult.Page(
                            data = movies,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                    is Either.Left -> {
                        LoadResult.Error(result.value.toThrowable())
                    }
                }
            }catch (t: Throwable) {
                // map to Failure and notify ViewModel via callback
                val failure = when (t) {
                    is java.net.SocketTimeoutException -> Failure.NetworkError(t)
                    is java.io.IOException -> Failure.NetworkError(t)
                    is retrofit2.HttpException -> Failure.ServerError(t.code(), t.message())
                    else -> Failure.UnknownError(t)
                }

                // return paging error so LoadState will also reflect the error
                LoadResult.Error(t)
            }
        }

    override fun getRefreshKey(state: PagingState<Int, MovieListItemModel>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(pageSize) ?: page?.nextKey?.minus(pageSize)
        }
    }
}