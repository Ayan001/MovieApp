package com.appdemo.data.di


import com.appdemo.data.repository.MovieRepositoryImpl
import com.appdemo.data.repository.PagedMovieRepositoryImpl
import com.appdemo.domain.repository.MovieRepository
import com.appdemo.domain.repository.PagedMovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMovieRepository(movieRepositoryImpl: MovieRepositoryImpl): MovieRepository

    @Binds
    abstract fun bindPagedMovieRepository(pagedMovieRepositoryImpl: PagedMovieRepositoryImpl): PagedMovieRepository
}
