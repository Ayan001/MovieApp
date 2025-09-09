package com.appdemo.presentation.features.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingSource.LoadResult
import androidx.paging.cachedIn
import com.appdemo.core.error.Failure
import com.appdemo.core.error.toThrowable
import com.appdemo.core.functional.fold
import com.appdemo.coreui.functional.stateInWhileActive
import com.appdemo.domain.model.Genre
import com.appdemo.domain.usecase.genres.GenresUseCase
import com.appdemo.domain.usecase.movie.MovieListUseCase
import com.appdemo.presentation.contracts.movie.MovieListContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMovieUseCase: MovieListUseCase,
    private val getGenreUseCase : GenresUseCase
) : ViewModel(), MovieListContract {
    // A Paging Flow that can be cached and reused across UI configuration changes.
    private var pagingFlow= getMovieUseCase(pageSize = 10,null).cachedIn(viewModelScope)
    private var hasLoadedMovies = false
    //// The current UI state, exposed as a read-only StateFlow.
    private val mutableUIState: MutableStateFlow<MovieListContract.MovieListState> =
        MutableStateFlow(MovieListContract.MovieListState.Loading)

    // A SharedFlow for one-time effects like navigation and doesn't store state.
    private val mutableSharedFlow: MutableSharedFlow<MovieListContract.MovieListEffect> =
        MutableSharedFlow()
    override val state: StateFlow<MovieListContract.MovieListState>
        get() = mutableUIState
            .stateInWhileActive(viewModelScope, MovieListContract.MovieListState.Loading) {
                /*if (!hasLoadedMovies) {
                    hasLoadedMovies = true
                    event(MovieListContract.MovieListEvent.LoadMovieList)
                }*/
                // This block is used for lazy initialization or checks.
            }
    override val effect: SharedFlow<MovieListContract.MovieListEffect>
        get() = mutableSharedFlow.asSharedFlow()
    private var currentGenre: Genre? = null

    //Handles incoming events from the UI and updates the state or emits effects accordingly.
    override fun event(event: MovieListContract.MovieListEvent) {
        when (event) {
            is MovieListContract.MovieListEvent.LoadMovieList -> {
            }

            is MovieListContract.MovieListEvent.MovieClicked ->
                viewModelScope.launch {
                    mutableSharedFlow.emit(MovieListContract.MovieListEffect.NavigateToMovieDetails(event.model))
                }
            is MovieListContract.MovieListEvent.LoadGenres -> {
                fetchGenres()
            }

            is MovieListContract.MovieListEvent.GenreSelected -> {
                val current = state.value
                currentGenre = event.genre
                if (current is MovieListContract.MovieListState.Success) {
                    updateState(current.copy(selectedGenre = event.genre))
                }
                loadMoviesByGenre(currentGenre)
            }
        }
    }
init {
    // Initial data load when the ViewModel is created.
    loadMoviesByGenre(null)
}
    /**
     * Fetches the list of movie genres from the use case.
     * Handles both success and failure scenarios by updating the UI state.
     */
    private fun fetchGenres() {
        viewModelScope.launch {
            getGenreUseCase().fold(
                { failure ->
                    updateState(MovieListContract.MovieListState.Error(failure))
                },
                { genreList ->
                    when (val current = state.value) {
                        is MovieListContract.MovieListState.Success -> {
                            updateState(
                                current.copy(
                                    genres = genreList,
                                    isLoading = false
                                )
                            )
                        }

                        is MovieListContract.MovieListState.Loading -> {
                            updateState(
                                MovieListContract.MovieListState.Success(
                                    movieList = pagingFlow,
                                    selectedGenre = currentGenre,
                                    genres = genreList,
                                    isLoading = false

                                )
                            )
                        }

                        is MovieListContract.MovieListState.Error -> {

                        }
                    }
                }
            )
        }
    }
    //Loads a list of movies based on a selected genre.
    //Caches the Paging Flow and updates the UI state with the new list.
    private fun loadMoviesByGenre(genre: Genre?) {
        viewModelScope.launch {
            val current = state.value
            if (current is MovieListContract.MovieListState.Success) {
                // show loading spinner but keep old list
                updateState(current.copy(isLoading = true))
            }

            val flow = getMovieUseCase(pageSize = 10, genre = genre?.name)
            try {
                // attempt to collect first PagingData emission to detect immediate failures
                // This will actually start the paging load once.
                val firstPagingData = flow.first()
                val cached = flow.cachedIn(viewModelScope)
                pagingFlow = cached
                val genres = (current as? MovieListContract.MovieListState.Success)?.genres ?: emptyList()
                updateState(
                    MovieListContract.MovieListState.Success(
                        movieList = pagingFlow,
                        selectedGenre = genre,
                        genres = genres,
                        isLoading = false
                    )
                )
             }catch (t : Throwable){
                val failure = t.toFailure()
                updateState(
                    MovieListContract.MovieListState.Error(failure)
                )
             }
                //.cachedIn(viewModelScope)

           /* val genres = if (current is MovieListContract.MovieListState.Success) {
                current.genres
            } else emptyList()*/

            /*updateState(
                MovieListContract.MovieListState.Success(
                    movieList = pagingFlow,
                    selectedGenre = genre,
                    genres = genres,
                    isLoading = false
                )
            )*/
        }
    }
    //Convert Throwable into your domain Failure.
    private fun Throwable.toFailure(): Failure {
        return when (this) {
            is java.io.IOException -> Failure.NetworkError(this)     // pass the throwable
            is retrofit2.HttpException -> Failure.ServerError(this.code(), message = this.message())         // pass HTTP code
            else -> Failure.UnknownError(this)              // pass throwable to unknown
        }
    }
    private fun updateState(state: MovieListContract.MovieListState) {
        mutableUIState.update { state }
    }
    // inside MovieListViewModel
    fun onPagingError(failure: Failure) {
        // update state or forward to an error events flow
        updateState(MovieListContract.MovieListState.Error(failure))
    }
}