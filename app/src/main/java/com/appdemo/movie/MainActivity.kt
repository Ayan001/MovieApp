package com.appdemo.movie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appdemo.presentation.contracts.movie.MovieListContract
import com.appdemo.presentation.features.movie.MovieListScreen
import com.appdemo.presentation.features.movie.MovieListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity  : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Box(Modifier.safeDrawingPadding().fillMaxWidth().fillMaxHeight().background(Color.White)) {
                val viewModel: MovieListViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                val dispatch: (MovieListContract.MovieListEvent) -> Unit = { event ->
                    viewModel.event(event)
                }
                MovieListScreen(
                    state = state,
                    dispatch = dispatch
                )
            }
        }
    }
}




