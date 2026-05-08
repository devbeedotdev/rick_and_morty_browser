package com.example.rickandmortybrowser.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.data.remote.model.CharacterLocation
import com.example.rickandmortybrowser.data.remote.model.CharacterOrigin
import com.example.rickandmortybrowser.util.AppConstants

@Entity(tableName = AppConstants.Tables.CHARACTERS)
data class CharacterEntity(
    @PrimaryKey
    @ColumnInfo(name = AppConstants.CharacterColumns.ID)
    val id: Int,
    @ColumnInfo(name = AppConstants.CharacterColumns.PAGE)
    val page: Int,
    @ColumnInfo(name = AppConstants.CharacterColumns.NAME)
    val name: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.GENDER)
    val gender: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.SPECIES)
    val species: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.STATUS)
    val status: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.IMAGE)
    val image: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.EPISODE_URLS)
    val episode: List<String>,
    @ColumnInfo(name = AppConstants.CharacterColumns.ORIGIN_NAME)
    val originName: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.ORIGIN_URL)
    val originUrl: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.LOCATION_NAME)
    val locationName: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.LOCATION_URL)
    val locationUrl: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.SEARCH_KEY)
    val searchKey: String,
) {
    fun toModel(): Character {
        return Character(
            id = id,
            name = name,
            gender = gender,
            species = species,
            status = status,
            image = image,
            episode = episode,
            origin = CharacterOrigin(originName, originUrl),
            location = CharacterLocation(locationName, locationUrl),
        )
    }

    companion object {
        fun fromModel(character: Character, page: Int, searchKey: String): CharacterEntity {
            return CharacterEntity(
                id = character.id,
                page = page,
                name = character.name,
                gender = character.gender,
                species = character.species,
                status = character.status,
                image = character.image,
                episode = character.episode,
                originName = character.origin.name,
                originUrl = character.origin.url,
                locationName = character.location.name,
                locationUrl = character.location.url,
                searchKey = searchKey,
            )
        }
    }
}
