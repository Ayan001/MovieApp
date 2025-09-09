package com.appdemo.data.mapper

import com.appdemo.domain.model.Genre
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenreMapperTest{
    private lateinit var mapper: GenreMapper

    @BeforeEach
    fun setUp() {
        mapper = GenreMapper()
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    fun mapConvertsValidPairsToGenre(input: List<List<Any>>, expected: List<Genre>) {
        val result = mapper.map(input)
        // compare size and exact list equality (Genre is a data class -> equality works)
        result.size shouldBe expected.size
        result shouldBe expected
    }

    private fun validInputProvider(): Stream<Arguments> = Stream.of(
        Arguments.of(
            listOf<Any>(
                listOf<Any>("Action", 5),
                listOf<Any>("Drama", 3L)
            ).map { it as List<*> }, // ensure types are List<List<Any>>
            listOf(Genre("Action", 5), Genre("Drama", 3))
        ),
        Arguments.of(
            listOf<Any>(listOf<Any>("SciFi", 2.0)).map { it as List<*> },
            listOf(Genre("SciFi", 2))
        )
    )

    @ParameterizedTest
    @MethodSource("mixedInputProvider")
    fun mapFiltersOutInvalidEntries(input: List<List<Any>>, expected: List<Genre>) {
        val result = mapper.map(input)
        result shouldBe expected
    }

    private fun mixedInputProvider(): Stream<Arguments> = Stream.of(
        Arguments.of(
            listOf<Any>(
                listOf<Any>("OnlyOne"),
                listOf<Any>("TooMany", 1, "extra"),
                listOf<Any>("Valid", 7)
            ).map { it as List<*> },
            listOf(Genre("Valid", 7))
        ),
        Arguments.of(
            listOf<Any>(
                listOf<Any>(123, 5),               // first not String
                listOf<Any>("Name", "notANumber"), // second not Number
                listOf<Any>("Valid", 10)
            ).map { it as List<*> },
            listOf(Genre("Valid", 10))
        )
    )

    @ParameterizedTest
    @MethodSource("numericEdgeCasesProvider")
    fun mapHandlesNumericEdgeCases(input: List<List<Any>>, expected: List<Genre>) {
        val result = mapper.map(input)
        result shouldBe expected
    }

    private fun numericEdgeCasesProvider(): Stream<Arguments> = Stream.of(
        Arguments.of(
            listOf<Any>(
                listOf<Any>("FloatVal", 4.9f),
                listOf<Any>("Negative", -2.0),
                listOf<Any>("Zero", 0L)
            ).map { it as List<Any> },
            listOf(Genre("FloatVal", 4), Genre("Negative", -2), Genre("Zero", 0))
        )
    )

    @Test
    fun mapReturnsEmptyWhenNoValidEntries() {
        val input: List<List<Any>> = listOf(
            listOf<Any>(1, 2),
            listOf<Any>("onlyOne"),
            emptyList(),
            listOf<Any>("name", "stringNumber")
        )

        val result = mapper.map(input)
        result.size shouldBe 0
    }
}