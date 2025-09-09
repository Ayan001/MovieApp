package com.appdemo.domain.usecase.genres

import com.appdemo.core.error.Failure
import com.appdemo.core.functional.Either
import com.appdemo.domain.model.Genre
import com.appdemo.domain.repository.MovieRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GenresUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: GenresUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GenresUseCase(repository)
    }

    @Test
    fun `invoke returns Right when repository succeeds`() = runTest {
        // Arrange
        val genres = listOf(Genre("Action", 10), Genre("Drama", 5))
        val expected = Either.Right(genres)

        coEvery { repository.getMovieGenres() } returns expected

        // Act
        val result = useCase()

        // Assert
        result shouldBe expected
        coVerify(exactly = 1) { repository.getMovieGenres() }
    }

    @Test
    fun `invoke returns Left when repository fails`() = runTest {
        // Arrange
        val expected = Either.Left(Failure.ServerError(500, "Internal Server Error"))

        coEvery { repository.getMovieGenres() } returns expected

        // Act
        val result = useCase()

        // Assert
        result shouldBe expected
        coVerify(exactly = 1) { repository.getMovieGenres() }
    }
}
