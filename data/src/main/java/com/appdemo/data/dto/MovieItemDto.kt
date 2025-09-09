package com.appdemo.data.dto

import com.google.gson.annotations.SerializedName


data class MovieItemDto(
    @SerializedName("id"           ) var id          : String?           = null,
    @SerializedName("genres"       ) var genres      : ArrayList<String> = arrayListOf(),
    @SerializedName("release_date" ) var releaseDate : String?           = null,
    @SerializedName("title"        ) var title       : String?           = null,
    @SerializedName("tagline"      ) var tagline     : String?           = null,
    @SerializedName("overview"     ) var overview    : String?           = null,
    @SerializedName("url"          ) var url         : String?           = null
)
