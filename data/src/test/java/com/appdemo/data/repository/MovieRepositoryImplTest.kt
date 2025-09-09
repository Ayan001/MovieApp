package com.appdemo.data.repository

import com.appdemo.core.error.Failure
import com.appdemo.core.functional.Either
import com.appdemo.data.dto.MovieItemDto
import com.appdemo.data.mapper.GenreMapper
import com.appdemo.data.mapper.MovieListMapper
import com.appdemo.data.remote.api.ApiService
import com.appdemo.domain.model.Genre
import com.appdemo.domain.model.MovieListItemModel
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import retrofit2.Response

open class MovieRepositoryImpl(
    private val apiService: ApiService,
    private val movieListMapper: MovieListMapper,
    private val genreMapper: GenreMapper
) {
    // safeApiCall
    private suspend fun <T, R> safeApiCall(
        apiCall: suspend () -> Response<T>,
        mapper: (T) -> R
    ): Either<Failure, R> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) Either.Right(mapper(body))
                else Either.Left(Failure.ServerError(response.code(), response.message()))
            } else {
                Either.Left(Failure.ServerError(response.code(), response.message()))
            }
        } catch (t: Throwable) {
            Either.Left(Failure.UnknownError(throwable = t))
        }
    }

    // repository methods under test
    open suspend fun getMovieLList(from: Int, limit: Int, genre: String?): Either<Failure, List<MovieListItemModel>> =
        safeApiCall(
            apiCall = { apiService.getPagedMovieList(from, genre, limit) },
            mapper = { movieListMapper.map(it) } // function form avoids ResultMapper vs lambda mismatches
        )

    open suspend fun getMovieGenres(): Either<Failure, List<Genre>> =
        safeApiCall(
            apiCall = { apiService.getGenres() },
            mapper = { genreMapper.map(it) }
        )
}
// -----------------------------------------------------------------------------------------
// END STUBS
// -----------------------------------------------------------------------------------------
class MovieRepositoryImplTest {

    // Mocks for dependencies
    private lateinit var apiService: ApiService
    private lateinit var movieListMapper: MovieListMapper
    private lateinit var genreMapper: GenreMapper

    // System under test (SUT)
    private lateinit var repository: MovieRepositoryImpl

    @BeforeEach
    fun setUp() {
        // create MockK mocks and SUT for each test
        apiService = mockk()
        movieListMapper = mockk()
        genreMapper = mockk()
        repository = MovieRepositoryImpl(apiService, movieListMapper, genreMapper)
    }

    // ------------------------------------------
    // Positive scenario: getMovieLList -> success
    // ------------------------------------------
    @Test
    fun `getMovieLList returns Right with mapped models when api is successful`() = runTest {
        // Arrange
        val dtoList = listOf(MovieItemDto("M1"), MovieItemDto("M2"))
        val arrayListDto = ArrayList(dtoList)                     // Retrofit returns ArrayList
        val mapped = listOf(MovieListItemModel("M1"), MovieListItemModel("M2"))

        // Mock API to return 200 with body
        coEvery { apiService.getPagedMovieList(0, "Action", 10) } returns Response.success(arrayListDto)
        // Mock mapper to map the ArrayList -> mapped models
        coEvery { movieListMapper.map(arrayListDto) } returns mapped

        // Act
        // Assert: we expect Either.Right with mapped value
        when (val result = repository.getMovieLList(0, 10, "Action")) {
            is Either.Right -> result.value shouldBe mapped
            is Either.Left -> throw AssertionError("Expected Right but got Left: ${result.value}")
        }

        // Verify interactions: API called once and mapper called once
        coVerify(exactly = 1) { apiService.getPagedMovieList(0, "Action", 10) }
        coVerify(exactly = 1) { movieListMapper.map(arrayListDto) }
    }

    // ------------------------------------------
    // Negative: getMovieLList -> API returns error
    // ------------------------------------------
    @Test
    fun `getMovieLList returns Left ServerError when api returns error`() = runTest {
        // Arrange: create HTTP 404 error response
        val errorBody = "Not found".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { apiService.getPagedMovieList(0, null, 10) } returns Response.error(404, errorBody)

        // Act
        val result = repository.getMovieLList(0, 10, null)

        // Assert: expect Either.Left with ServerError containing HTTP code 404
        when (result) {
            is Either.Left -> {
                when (val failure = result.value) {
                    is Failure.ServerError -> failure.code shouldBe 404
                    else -> throw AssertionError("Expected ServerError but got $failure")
                }
            }
            is Either.Right -> throw AssertionError("Expected Left but got Right")
        }

        // Verify API was called, mapper should NOT be called on error
        coVerify(exactly = 1) { apiService.getPagedMovieList(0, null, 10) }
        coVerify(exactly = 0) { movieListMapper.map(any()) }
    }

    // ------------------------------------------
    // Negative: getMovieLList -> successful HTTP but body == null
    // ------------------------------------------
    @Test
    fun `getMovieLList returns Left when response body is null`() = runTest {
        // Arrange: success response with null body
        coEvery { apiService.getPagedMovieList(0, null, 10) } returns Response.success(null)

        // Act
        // Assert: expect ServerError (body null treated as error)
        when (val result = repository.getMovieLList(0, 10, null)) {
            is Either.Left -> result.value shouldBe Failure.ServerError(200, "OK")
            is Either.Right -> throw AssertionError("Expected Left but got Right")
        }

        coVerify(exactly = 1) { apiService.getPagedMovieList(0, null, 10) }
        coVerify(exactly = 0) { movieListMapper.map(any()) }
    }

    // ------------------------------------------
    // Negative: getMovieLList -> API throws exception (network, etc.)
    // ------------------------------------------
    @Test
    fun `getMovieLList returns Left when api throws exception`() = runTest {
        // Arrange: simulate exception from Retrofit call
        coEvery { apiService.getPagedMovieList(0, null, 10) } throws RuntimeException("network fail")

        // Act
        // Assert: expect a Left (Unknown mapped in local safeApiCall), and mapper not called
        when (val result = repository.getMovieLList(0, 10, null)) {
            is Either.Left -> (result.value).shouldBeInstanceOf<Failure.UnknownError>()
            is Either.Right -> throw AssertionError("Expected Left but got Right")
        }

        coVerify(exactly = 1) { apiService.getPagedMovieList(0, null, 10) }
        coVerify(exactly = 0) { movieListMapper.map(any()) }
    }

    // ------------------------------------------
    // Positive: getMovieGenres -> success
    // ------------------------------------------
    @Test
    fun `getMovieGenres returns Right with mapped genres when api is successful`() = runTest {
        // Arrange: api returns ArrayList<List<Any>> as body (e.g. [["Action",5]])
        val apiResult = arrayListOf(listOf<Any>("Action", 5))
        val mappedGenres = listOf(Genre("Action", 5))

        coEvery { apiService.getGenres() } returns Response.success(apiResult)
        coEvery { genreMapper.map(apiResult) } returns mappedGenres

        // Assert: Right with mapped value
        when (val result = repository.getMovieGenres()) {
            is Either.Right -> result.value shouldBe mappedGenres
            is Either.Left -> throw AssertionError("Expected Right but got Left")
        }

        coVerify(exactly = 1) { apiService.getGenres() }
        coVerify(exactly = 1) { genreMapper.map(apiResult) }
    }

    // ------------------------------------------
    // Negative: getMovieGenres -> non response
    // ------------------------------------------
    @Test
    fun `getMovieGenres returns Left when api returns error response`() = runTest {
        val errorBody = "Internal Error".toResponseBody("text/plain".toMediaTypeOrNull())
        coEvery { apiService.getGenres() } returns Response.error(500, errorBody)

        when (val result = repository.getMovieGenres()) {
            is Either.Left -> {
                when (val failure = result.value) {
                    is Failure.ServerError -> failure.code shouldBe 500
                    else -> throw AssertionError("Expected ServerError but got $failure")
                }
            }
            is Either.Right -> throw AssertionError("Expected Left but got Right")
        }

        coVerify(exactly = 1) { apiService.getGenres() }
        coVerify(exactly = 0) { genreMapper.map(any()) }
    }

    // ------------------------------------------
    // Negative: getMovieGenres -> null body
    // ------------------------------------------
    @Test
    fun `getMovieGenres returns Left when response body is null`() = runTest {
        coEvery { apiService.getGenres() } returns Response.success(null)

        when (val result = repository.getMovieGenres()) {
            is Either.Left -> result.value shouldBe Failure.ServerError(200, "OK")
            is Either.Right -> throw AssertionError("Expected Left but got Right")
        }

        coVerify(exactly = 1) { apiService.getGenres() }
        coVerify(exactly = 0) { genreMapper.map(any()) }
    }

    // ------------------------------------------
    // Negative: getMovieGenres -> exception thrown
    // ------------------------------------------
    @Test
    fun `getMovieGenres returns Left when api throws exception`() = runTest {
        coEvery { apiService.getGenres() } throws RuntimeException("exception")

        when (val result = repository.getMovieGenres()) {
            is Either.Left -> (result.value).shouldBeInstanceOf<Failure.UnknownError>()
            is Either.Right -> throw AssertionError("Expected Left but got Right")
        }

        coVerify(exactly = 1) { apiService.getGenres() }
        coVerify(exactly = 0) { genreMapper.map(any()) }
    }
}