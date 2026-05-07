package com.example.rickandmortybrowser.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rickandmortybrowser.data.local.entity.CharacterEntity
import com.example.rickandmortybrowser.util.AppConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Query(QUERY_BY_PAGE)
    fun getCharactersByPage(page: Int): Flow<List<CharacterEntity>>

    @Query(QUERY_BY_FILTERS)
    fun searchCharacters(
        name: String?,
        status: String?,
        species: String?,
    ): Flow<List<CharacterEntity>>

    companion object {
        private const val QUERY_BY_PAGE =
            "SELECT * FROM ${AppConstants.Tables.CHARACTERS} " +
                "WHERE ${AppConstants.CharacterColumns.PAGE} = :page " +
                "ORDER BY ${AppConstants.CharacterColumns.ID} ASC"

        private const val QUERY_BY_FILTERS =
            "SELECT * FROM ${AppConstants.Tables.CHARACTERS} " +
                "WHERE (:name IS NULL OR ${AppConstants.CharacterColumns.NAME} LIKE '%' || :name || '%') " +
                "AND (:status IS NULL OR ${AppConstants.CharacterColumns.STATUS} = :status) " +
                "AND (:species IS NULL OR ${AppConstants.CharacterColumns.SPECIES} LIKE '%' || :species || '%') " +
                "ORDER BY ${AppConstants.CharacterColumns.ID} ASC"
    }
}
