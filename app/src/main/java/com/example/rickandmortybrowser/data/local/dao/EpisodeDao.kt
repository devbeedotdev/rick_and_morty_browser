package com.example.rickandmortybrowser.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortybrowser.data.local.entity.EpisodeEntity
import com.example.rickandmortybrowser.util.AppConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)

    @Query(QUERY_BY_IDS)
    fun getEpisodesByIds(ids: List<Int>): Flow<List<EpisodeEntity>>

    companion object {
        private const val QUERY_BY_IDS =
            "SELECT * FROM ${AppConstants.Tables.EPISODES} " +
                "WHERE ${AppConstants.EpisodeColumns.ID} IN (:ids) " +
                "ORDER BY ${AppConstants.EpisodeColumns.ID} ASC"
    }
}
