package com.example.rickandmortybrowser.data.repository

import com.example.rickandmortybrowser.data.remote.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacters(page: Int): Flow<Result<List<Character>>>
    fun getCharacterById(characterId: Int): Flow<Result<Character>>
    fun searchCharacters(
        name: String?,
        status: String?,
        species: String?,
    ): Flow<Result<List<Character>>>
}
