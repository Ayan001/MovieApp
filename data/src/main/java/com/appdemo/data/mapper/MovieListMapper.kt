package com.appdemo.data.mapper

import com.appdemo.data.dto.MovieItemDto
import com.appdemo.domain.model.MovieListItemModel
import com.appdemo.core.mapper.ResultMapper
import javax.inject.Inject

class MovieListMapper @Inject constructor() :
    ResultMapper<List<MovieItemDto>, List<MovieListItemModel>> {
    override fun map(input: List<MovieItemDto>): List<MovieListItemModel> =input.sortedWith(
        compareBy<MovieItemDto> { it ->
            // true if after cleaning no letters remain (numbers-only / specials-only)
            it.title.orEmpty().dropWhile { !it.isLetter() }.isEmpty()
        }.thenBy { it ->
            // sort alphabetically by removing all non-letters from the start
            it.title.orEmpty()
                .dropWhile { !it.isLetter() }   // ignore leading digits & specials
                .lowercase()
        }
    ).map { it.toModel() }

    private fun MovieItemDto.toModel() =
        MovieListItemModel(
            title = title.orEmpty(),
            year = releaseDate.orEmpty(),
            overview = overview.orEmpty(),
            genres = genres
        )
}
