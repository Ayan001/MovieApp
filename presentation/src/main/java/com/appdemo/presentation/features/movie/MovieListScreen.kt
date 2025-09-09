package com.appdemo.presentation.features.movie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import com.appdemo.domain.model.Genre
// Paging Compose imports
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.appdemo.core.error.getErrorMessage
import com.appdemo.coreui.component.AppLoadingScreen
import com.appdemo.coreui.component.ErrorScreen
import com.appdemo.coreui.component.MovieItem
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.presentation.contracts.movie.MovieListContract

@Composable
fun MovieListScreen(state: MovieListContract.MovieListState, dispatch: (MovieListContract.MovieListEvent) -> Unit) {

    Column (modifier = Modifier.padding(16.dp)){

        when (state) {
            is MovieListContract.MovieListState.Error -> ErrorScreen(
                errorMessage = state.error.getErrorMessage()
            )


            MovieListContract.MovieListState.Loading -> {
                Box {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }/*AppLoadingScreen(
                modifier = Modifier.semantics {
                    contentDescription = "Loading"
                }
            )*/

            is MovieListContract.MovieListState.Success ->{
                MovieTopBar(
                    selectedGenreName = state.selectedGenre?.name,
                    genres = state.genres,

                onGenreClick = { dispatch(MovieListContract.MovieListEvent.LoadGenres) },
                onGenreSelected = {
                        selected ->
                    if (selected != null) {
                        if (selected.name == "All") {
                            dispatch(MovieListContract.MovieListEvent.GenreSelected(null))
                        } else {
                            //val genre = state.genres.find { it.name == selected }
                            dispatch(MovieListContract.MovieListEvent.GenreSelected(selected))
                        }
                    }
                }
            )
                MovieListUi(state, dispatch)
            }

            else -> {}
        }
    }
}
    @Composable
    fun MovieListUi(
        states: MovieListContract.MovieListState.Success,
        dispatch: (MovieListContract.MovieListEvent) -> Unit
    ) {
        val movies: LazyPagingItems<MovieListItemModel> =
            states.movieList.collectAsLazyPagingItems()
        val refreshState = movies.loadState.refresh

        val showOverlayLoading = when (refreshState) {
            is LoadState.Loading -> true
            else -> states.isLoading
        }
        Box{
            LazyColumn(modifier = Modifier.padding(5.dp)) {

                items(movies.itemCount) { index ->
                    val movie = movies[index] ?: return@items
                    val genreString = movie.genres.joinToString(", ")
                    MovieItem(modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            dispatch(MovieListContract.MovieListEvent.MovieClicked(movie))
                        },
                        title = movie.title,
                        year = movie.year,
                        overview = movie.overview,
                        genre = genreString
                    )
                }
            }
            if (showOverlayLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
        }

    }

