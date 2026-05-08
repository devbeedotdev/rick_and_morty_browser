package com.example.rickandmortybrowser.data.remote.model

import com.google.gson.annotations.SerializedName

data class Character(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("species") val species: String,
    @SerializedName("status") val status: String,
    @SerializedName("image") val image: String,
    @SerializedName("episode") val episode: List<String>,
    @SerializedName("origin") val origin: CharacterOrigin,
    @SerializedName("location") val location: CharacterLocation,
)

data class CharacterOrigin(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
)

data class CharacterLocation(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
)

data class CharacterPageResponse(
    @SerializedName("results") val results: List<Character>,
)

data class Episode(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("episode") val code: String,
    @SerializedName("air_date") val airDate: String,
)
