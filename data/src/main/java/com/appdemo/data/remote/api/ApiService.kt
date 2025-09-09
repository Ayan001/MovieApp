package com.appdemo.data.remote.api

import com.appdemo.data.dto.MovieItemDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("genres")
    suspend fun getGenres(): Response<ArrayList<List<Any>>>

    @GET("movies")
    suspend fun getPagedMovieList(@Query("from") from: Int,
                                  @Query("genre") genre: String?=null,
                                  @Query("limit") limit: Int): Response<ArrayList<MovieItemDto>>
}
