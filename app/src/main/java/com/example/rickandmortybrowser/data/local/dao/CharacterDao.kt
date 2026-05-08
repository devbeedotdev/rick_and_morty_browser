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

    @Query(
        "SELECT * FROM ${AppConstants.Tables.CHARACTERS} " +
            "WHERE (:name IS NULL OR ${AppConstants.CharacterColumns.NAME} LIKE '%' || :name || '%') " +
            "AND (:status IS NULL OR ${AppConstants.CharacterColumns.STATUS} = :status) " +
            "AND (:species IS NULL OR ${AppConstants.CharacterColumns.SPECIES} LIKE '%' || :species || '%') " +
            "ORDER BY ${AppConstants.CharacterColumns.ID} ASC",
    )
    suspend fun searchByFilters(
        name: String?,
        status: String?,
        species: String?,
    ): List<CharacterEntity>
}
