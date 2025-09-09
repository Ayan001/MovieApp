package com.appdemo.presentation.features.movie

import androidx.paging.PagingData
import com.appdemo.core.error.Failure
import com.appdemo.core.functional.Either
import com.appdemo.domain.model.Genre
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.domain.usecase.genres.GenresUseCase
import com.appdemo.domain.usecase.movie.MovieListUseCase
import com.appdemo.presentation.contracts.movie.MovieListContract
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var movieListUseCase: MovieListUseCase
    private lateinit var genresUseCase: GenresUseCase
    private lateinit var viewModel: MovieListViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        movieListUseCase = mockk()
        genresUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun pagingDataFlow(items: List<MovieListItemModel>) = flowOf(PagingData.from(items))

    // -------------------------
    // Positive: init + LoadGenres success
    // -------------------------
    @Test
    fun `init and LoadGenres success yields Success state with genres`() = runTest {
        val sampleMovies = listOf(MovieListItemModel(title = "A"))
        every { movieListUseCase.invoke(10, null) } returns pagingDataFlow(sampleMovies)

        val genres = listOf(Genre("Action", 10), Genre("Drama", 5))
        coEvery { genresUseCase.invoke() } returns Either.Right(genres)

        viewModel = MovieListViewModel(movieListUseCase, genresUseCase)

        // Wait until state != Loading
        val nonLoading = viewModel.state.first { it !is MovieListContract.MovieListState.Loading }
        nonLoading.shouldBeInstanceOf<MovieListContract.MovieListState.Success>()

        // Fire LoadGenres event
        viewModel.event(MovieListContract.MovieListEvent.LoadGenres)

        val afterGenres = viewModel.state.first { it !is MovieListContract.MovieListState.Loading }
        afterGenres.shouldBeInstanceOf<MovieListContract.MovieListState.Success>()
        val success = afterGenres as MovieListContract.MovieListState.Success
        success.genres shouldBe genres

        // Verify: allow multiple calls, but require at least 1
        verify(atLeast = 1) { movieListUseCase.invoke(10, null) }
        coVerify(atLeast = 1) { genresUseCase.invoke() }
    }

    // -------------------------
    // Negative: LoadGenres failure -> Error
    // -------------------------
    @Test
    fun `LoadGenres failure results in Error state`() = runTest {
        every { movieListUseCase.invoke(10, null) } returns pagingDataFlow(emptyList())
        val failure = Failure.ServerError(500, "Internal")
        coEvery { genresUseCase.invoke() } returns Either.Left(failure)

        viewModel = MovieListViewModel(movieListUseCase, genresUseCase)

        // Trigger LoadGenres
        viewModel.event(MovieListContract.MovieListEvent.LoadGenres)

        val state = viewModel.state.first { it !is MovieListContract.MovieListState.Loading }
        state.shouldBeInstanceOf<MovieListContract.MovieListState.Error>()
        val error = state as MovieListContract.MovieListState.Error
        error.error shouldBe failure

        coVerify(atLeast = 1) { genresUseCase.invoke() }
    }

    // -------------------------
    // GenreSelected reloads movies with genre
    // -------------------------
    @Test
    fun `GenreSelected updates selectedGenre and reloads movies`() = runTest {
        every { movieListUseCase.invoke(10, null) } returns pagingDataFlow(listOf(MovieListItemModel("X")))

        val selectedGenre = Genre("Comedy", 8)
        every { movieListUseCase.invoke(10, selectedGenre.name) } returns pagingDataFlow(listOf(MovieListItemModel("C")))

        coEvery { genresUseCase.invoke() } returns Either.Right(emptyList())

        viewModel = MovieListViewModel(movieListUseCase, genresUseCase)

        // Fire GenreSelected
        viewModel.event(MovieListContract.MovieListEvent.GenreSelected(selectedGenre))

        val state = viewModel.state.first { it !is MovieListContract.MovieListState.Loading }
        state.shouldBeInstanceOf<MovieListContract.MovieListState.Success>()
        val success = state as MovieListContract.MovieListState.Success
        success.selectedGenre shouldBe selectedGenre

        verify(atLeast = 1) { movieListUseCase.invoke(10, selectedGenre.name) }
    }

}
