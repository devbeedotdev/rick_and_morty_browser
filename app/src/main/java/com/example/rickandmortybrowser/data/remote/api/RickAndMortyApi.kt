package com.example.rickandmortybrowser.data.remote.api

import com.example.rickandmortybrowser.data.remote.model.CharacterPageResponse
import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(@Query("page") page: Int): CharacterPageResponse

    @GET("character")
    suspend fun searchCharacters(
        @Query("name") name: String?,
        @Query("status") status: String?,
        @Query("species") species: String?,
    ): CharacterPageResponse

    @GET("episode/{ids}")
    suspend fun getEpisodesByIds(@Path("ids") ids: String): JsonElement
}
