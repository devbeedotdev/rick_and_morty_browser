package com.example.rickandmortybrowser.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.data.remote.model.CharacterLocation
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
    @ColumnInfo(name = AppConstants.CharacterColumns.SPECIES)
    val species: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.STATUS)
    val status: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.IMAGE)
    val image: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.EPISODE_URLS)
    val episode: List<String>,
    @ColumnInfo(name = AppConstants.CharacterColumns.LOCATION_NAME)
    val locationName: String,
    @ColumnInfo(name = AppConstants.CharacterColumns.LOCATION_URL)
    val locationUrl: String,
) {
    fun toModel(): Character {
        return Character(
            id = id,
            name = name,
            species = species,
            status = status,
            image = image,
            episode = episode,
            location = CharacterLocation(locationName, locationUrl),
        )
    }

    companion object {
        fun fromModel(character: Character, page: Int): CharacterEntity {
            return CharacterEntity(
                id = character.id,
                page = page,
                name = character.name,
                species = character.species,
                status = character.status,
                image = character.image,
                episode = character.episode,
                locationName = character.location.name,
                locationUrl = character.location.url,
            )
        }
    }
}
