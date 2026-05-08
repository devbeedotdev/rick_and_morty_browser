package com.example.rickandmortybrowser.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rickandmortybrowser.data.remote.model.Episode
import com.example.rickandmortybrowser.util.AppConstants

@Entity(tableName = AppConstants.Tables.EPISODES)
data class EpisodeEntity(
    @PrimaryKey
    @ColumnInfo(name = AppConstants.EpisodeColumns.ID)
    val id: Int,
    @ColumnInfo(name = AppConstants.EpisodeColumns.NAME)
    val name: String,
    @ColumnInfo(name = AppConstants.EpisodeColumns.CODE)
    val code: String,
    @ColumnInfo(name = AppConstants.EpisodeColumns.AIR_DATE)
    val airDate: String,
) {
    fun toModel(): Episode = Episode(id = id, name = name, code = code, airDate = airDate)

    companion object {
        fun fromModel(episode: Episode): EpisodeEntity {
            return EpisodeEntity(
                id = episode.id,
                name = episode.name,
                code = episode.code,
                airDate = episode.airDate,
            )
        }
    }
}
