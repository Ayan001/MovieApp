package com.appdemo.data.mapper

import com.appdemo.data.dto.MovieItemDto
import com.appdemo.domain.model.MovieListItemModel
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovieListMapperTest {

    private lateinit var mapper: MovieListMapper

    @BeforeEach
    fun setUp() {
        mapper = MovieListMapper()
    }

    @ParameterizedTest
    @MethodSource("conversionProvider")
    fun mapConvertsDtoToModel(input: List<MovieItemDto>, expected: List<MovieListItemModel>) {
        val result = mapper.map(input)
        result shouldBe expected
    }

    private fun conversionProvider(): Stream<Arguments> = Stream.of(
        Arguments.of(
            listOf(
                MovieItemDto(title = "My Movie", releaseDate = "2020", overview = "Overview", genres = arrayListOf("Action"))
            ),
            listOf(
                MovieListItemModel(title = "My Movie", year = "2020", overview = "Overview", genres = arrayListOf("Action"))
            )
        ),
        Arguments.of(
            // empty input -> empty output
            emptyList<MovieItemDto>(),
            emptyList<MovieListItemModel>()
        )
    )

    @ParameterizedTest
    @MethodSource("orderingProvider")
    fun mapOrdersTitlesCorrectly(input: List<MovieItemDto>, expectedTitles: List<String>) {
        val result = mapper.map(input)
        result.map { it.title } shouldBe expectedTitles
    }

    private fun orderingProvider(): Stream<Arguments> = Stream.of(
        // numbers-only should go last; ignore leading non-letters for alphabetical order; case-insensitive
        Arguments.of(
            listOf(
                MovieItemDto(title = "123"),
                MovieItemDto(title = "Apple"),
                MovieItemDto(title = "!Banana"),
                MovieItemDto(title = "2apples"),
                MovieItemDto(title = "apple")
            ),
            listOf("Apple", "apple", "2apples", "!Banana", "123")
        ),
        // symbols-only and empty/null titles considered no-letters -> placed at end preserving relative order
        Arguments.of(
            listOf(
                MovieItemDto(title = null),
                MovieItemDto(title = ""),
                MovieItemDto(title = "Zebra"),
                MovieItemDto(title = "!@#")
            ),
            listOf("Zebra", null.orEmpty(), "".orEmpty(), "!@#")
        )
    )

    @ParameterizedTest
    @MethodSource("caseInsensitiveProvider")
    fun mapIsCaseInsensitiveForAlphabeticalPart(input: List<MovieItemDto>, expectedTitles: List<String>) {
        val result = mapper.map(input)
        result.map { it.title } shouldBe expectedTitles
    }

    private fun caseInsensitiveProvider(): Stream<Arguments> = Stream.of(
        Arguments.of(
            listOf(
                MovieItemDto(title = "banana"),
                MovieItemDto(title = "Apple"),
                MovieItemDto(title = "apple"),
                MovieItemDto(title = "Banana")
            ),
            listOf("Apple", "apple", "banana", "Banana")
        )
    )

    @ParameterizedTest
    @MethodSource("filteringProvider")
    fun mapFiltersInvalidEntriesAndPreservesStableOrder(input: List<MovieItemDto>, expected: List<MovieListItemModel>) {
        val result = mapper.map(input)
        result shouldBe expected
    }

    private fun filteringProvider(): Stream<Arguments> = Stream.of(
        Arguments.of(
            listOf(
                MovieItemDto(title = "A"),
                MovieItemDto(title = "123"),
                MovieItemDto(title = "!@#"),
                MovieItemDto(title = "B")
            ),
            listOf(
                MovieListItemModel("A", "", "", arrayListOf()),
                MovieListItemModel("B", "", "", arrayListOf()),
                MovieListItemModel("123", "", "", arrayListOf()),
                MovieListItemModel("!@#", "", "", arrayListOf())
            )
        )
    )
}