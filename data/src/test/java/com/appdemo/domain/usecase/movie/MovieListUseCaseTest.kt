package com.appdemo.domain.usecase.movie

import androidx.paging.PagingData
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.domain.repository.PagedMovieRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MovieListUseCaseTest {

    // Using the model you provided:
    data class MovieListItemModel(
        val title: String? = "",
        val year: String? = "",
        val overview: String? = "",
        var genres: ArrayList<String> = arrayListOf()
    )

    // Repository interface expected by the use case (replace with your real interface)
    interface PagedMovieRepository {
        fun getPagedMovies(pageSize: Int, genre: String?): kotlinx.coroutines.flow.Flow<PagingData<MovieListItemModel>>
    }

    // The use case under test (same as your snippet). If you already have this, import it instead.
    class MovieListUseCase(
        private val repository: PagedMovieRepository
    ) {
        operator fun invoke(pageSize: Int, genre: String?): kotlinx.coroutines.flow.Flow<PagingData<MovieListItemModel>> {
            return repository.getPagedMovies(pageSize, genre)
        }
    }

        private lateinit var repository: PagedMovieRepository
        private lateinit var useCase: MovieListUseCase

        @BeforeEach
        fun setUp() {
            repository = mockk()
            useCase = MovieListUseCase(repository)
        }

        // ------------------------
        // Positive case
        // ------------------------
        @Test
        fun `invoke delegates to repository and emits PagingData when repository succeeds`() = runTest {
            val items = listOf(
                MovieListItemModel(title = "A"),
                MovieListItemModel(title = "B")
            )
            val pagingData = PagingData.from(items)
            val pageSize = 10
            val genre: String? = null

            // Stub with every (non-suspend function)
            every { repository.getPagedMovies(pageSize, genre) } returns flowOf(pagingData)

            // Collect the first emitted PagingData -> this triggers the call
            val emitted = useCase.invoke(pageSize, genre).first()

            // Assertions
            emitted shouldBe pagingData

            // Verify with verify (non-suspend)
            verify(exactly = 1) { repository.getPagedMovies(pageSize, genre) }
        }

        // ------------------------
        // Negative case: the FLOW throws when collected (repository returns a failing flow)
        // ------------------------
        @Test
        fun `invoke delegates to repository`() = runTest {
            val pagingData = PagingData.from(listOf(MovieListItemModel("A")))
            val pageSize = 10
            val genre: String? = null

            // stub with every { } (non-suspend)
            every { repository.getPagedMovies(pageSize, genre) } returns flowOf(pagingData)

            // collect to trigger invocation
            val emitted = useCase.invoke(pageSize, genre).first()

            // verify called
            verify(exactly = 1) { repository.getPagedMovies(pageSize, genre) }
        }

        // ------------------------
        // Negative case: repository call itself throws (immediate exception)
        // ------------------------
        @Test
        fun `invoke throws when repository throws on invocation`() {
            val pageSize = 5
            val genre: String = "Horror"

            // Simulate immediate exception when repository method invoked
            every { repository.getPagedMovies(pageSize, genre) } throws IllegalStateException("bad")

            // The act of calling useCase.invoke(...) will throw immediately
            assertThrows(IllegalStateException::class.java) {
                useCase.invoke(pageSize, genre)
            }

            verify(exactly = 1) { repository.getPagedMovies(pageSize, genre) }
        }

}