package com.example.rickandmortybrowser.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortybrowser.data.local.entity.CharacterEntity
import com.example.rickandmortybrowser.util.AppConstants

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CharacterEntity>)

    @Query("SELECT * FROM ${AppConstants.Tables.CHARACTERS} WHERE ${AppConstants.CharacterColumns.PAGE} = :page ORDER BY ${AppConstants.CharacterColumns.ID} ASC")
    suspend fun getByPage(page: Int): List<CharacterEntity>

    @Query("SELECT * FROM ${AppConstants.Tables.CHARACTERS} WHERE ${AppConstants.CharacterColumns.ID} = :id LIMIT 1")
    suspend fun getById(id: Int): CharacterEntity?

    @Query(
        "SELECT * FROM ${AppConstants.Tables.CHARACTERS} " +
            "WHERE ${AppConstants.CharacterColumns.SEARCH_KEY} = :searchKey " +
            "ORDER BY ${AppConstants.CharacterColumns.ID} ASC",
    )
    suspend fun getBySearchKey(searchKey: String): List<CharacterEntity>
}
