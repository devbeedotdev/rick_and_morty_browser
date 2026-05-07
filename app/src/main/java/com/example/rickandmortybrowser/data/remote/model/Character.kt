package com.example.rickandmortybrowser.data.remote.model

import com.google.gson.annotations.SerializedName

data class Character(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("species") val species: String,
    @SerializedName("status") val status: String,
    @SerializedName("image") val image: String,
    @SerializedName("episode") val episode: List<String>,
    @SerializedName("location") val location: CharacterLocation,
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
)
