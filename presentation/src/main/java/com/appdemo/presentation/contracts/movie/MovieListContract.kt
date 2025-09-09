package com.appdemo.presentation.contracts.movie

import androidx.paging.PagingData
import com.appdemo.core.error.Failure
import com.appdemo.coreui.mvi.AppContract
import com.appdemo.domain.model.Genre
import com.appdemo.domain.model.MovieListItemModel
import kotlinx.coroutines.flow.Flow

interface MovieListContract :
    AppContract<MovieListContract.MovieListState, MovieListContract.MovieListEffect, MovieListContract.MovieListEvent> {

    sealed class MovieListEvent {
        data object LoadMovieList : MovieListEvent()

        data class MovieClicked(val model: MovieListItemModel) : MovieListEvent()

        object LoadGenres : MovieListEvent()

        data class GenreSelected(val genre: Genre?) : MovieListEvent()
    }

    sealed class MovieListState {
        data object Loading : MovieListState()

        data class Success(
            val movieList: Flow<PagingData<MovieListItemModel>>,
            val genres: List<Genre> = emptyList(),
            val selectedGenre: Genre? = null,
            val isLoading: Boolean = false
        ) : MovieListState()
        data class Error(
            val error: Failure
        ) : MovieListState()
        /*data class GenresLoaded(
            val genres: List<Genre>,
            val selectedGenre: Genre? = null,
            val movieList: Flow<PagingData<MovieListItemModel>>? = null
        ) : MovieListState()*/
    }

    sealed class MovieListEffect {
        data class NavigateToMovieDetails(val model: MovieListItemModel) : MovieListEffect()
    }
}
