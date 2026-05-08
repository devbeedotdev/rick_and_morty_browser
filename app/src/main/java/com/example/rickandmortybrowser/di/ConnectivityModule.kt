package com.example.rickandmortybrowser.di

import android.content.Context
import com.example.rickandmortybrowser.util.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {
    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context,
    ): NetworkMonitor = NetworkMonitor(context)
}
