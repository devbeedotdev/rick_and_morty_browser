package com.example.rickandmortybrowser.di

import com.example.rickandmortybrowser.data.remote.api.RickAndMortyApi
import com.example.rickandmortybrowser.data.remote.interceptor.NetworkErrorInterceptor
import com.example.rickandmortybrowser.util.AppConstants
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideNetworkErrorInterceptor(): NetworkErrorInterceptor = NetworkErrorInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        networkErrorInterceptor: NetworkErrorInterceptor,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(AppConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(AppConstants.NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(AppConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(AppConstants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(networkErrorInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideRickAndMortyApi(retrofit: Retrofit): RickAndMortyApi {
        return retrofit.create(RickAndMortyApi::class.java)
    }
}
