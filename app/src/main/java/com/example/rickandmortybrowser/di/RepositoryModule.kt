package com.example.rickandmortybrowser.di

import com.example.rickandmortybrowser.data.repository.CharacterRepository
import com.example.rickandmortybrowser.data.repository.CharacterRepositoryImpl
import com.example.rickandmortybrowser.data.repository.EpisodeRepository
import com.example.rickandmortybrowser.data.repository.EpisodeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        implementation: CharacterRepositoryImpl,
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindEpisodeRepository(
        implementation: EpisodeRepositoryImpl,
    ): EpisodeRepository
}
