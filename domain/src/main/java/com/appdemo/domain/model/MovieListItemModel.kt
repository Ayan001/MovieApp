package com.appdemo.domain.model

data class MovieListItemModel(val title: String? = "",
                              val year: String? = "",
                              val overview: String ?= "",
                              var genres : ArrayList<String> = arrayListOf())
