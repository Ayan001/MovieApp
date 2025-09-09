package com.appdemo.data.mapper

import com.appdemo.domain.model.Genre
import javax.inject.Inject


class GenreMapper @Inject constructor() {

    fun map(from: List<List<Any>>): List<Genre> {
        return from.mapNotNull { item ->
            if (item.size == 2 && item[0] is String && item[1] is Number) {
                Genre(
                    name = item[0] as String,
                    count = (item[1] as Number).toInt()
                )
            } else null
        }
    }
}