package com.example.rickandmortybrowser.di

import android.content.Context
import androidx.room.Room
import com.example.rickandmortybrowser.data.local.AppDatabase
import com.example.rickandmortybrowser.data.local.dao.CharacterDao
import com.example.rickandmortybrowser.data.local.dao.EpisodeDao
import com.example.rickandmortybrowser.util.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppConstants.ROOM_DB_NAME,
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCharacterDao(database: AppDatabase): CharacterDao = database.characterDao()

    @Provides
    fun provideEpisodeDao(database: AppDatabase): EpisodeDao = database.episodeDao()
}
