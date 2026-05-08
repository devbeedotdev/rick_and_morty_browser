package com.example.rickandmortybrowser.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortybrowser.data.local.entity.EpisodeEntity
import com.example.rickandmortybrowser.util.AppConstants

@Dao
interface EpisodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<EpisodeEntity>)

    @Query("SELECT * FROM ${AppConstants.Tables.EPISODES} WHERE ${AppConstants.EpisodeColumns.ID} IN (:ids) ORDER BY ${AppConstants.EpisodeColumns.ID} ASC")
    suspend fun getByIds(ids: List<Int>): List<EpisodeEntity>
}
