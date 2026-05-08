package com.example.rickandmortybrowser.data.repository

import com.example.rickandmortybrowser.data.local.dao.CharacterDao
import com.example.rickandmortybrowser.data.local.entity.CharacterEntity
import com.example.rickandmortybrowser.data.remote.api.RickAndMortyApi
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.util.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
    private val characterDao: CharacterDao,
) : CharacterRepository {

    override fun getCharacters(page: Int): Flow<Result<List<Character>>> = flow {
        emit(Result.Loading)
        val searchKey = buildSearchKey(
            name = null,
            status = null,
            species = null,
        )

        val cached = characterDao.getByPage(page)
        if (cached.isNotEmpty()) {
            emit(Result.Success(cached.map { it.toModel() }))
        }

        try {
            val response = api.getCharacters(page)
            val fresh = response.results
            if (fresh.isEmpty()) {
                emit(Result.Error(AppConstants.NULL_RESPONSE_MESSAGE))
                return@flow
            }
            characterDao.insertAll(fresh.map { CharacterEntity.fromModel(it, page, searchKey) })
            emit(Result.Success(fresh))
        } catch (throwable: Throwable) {
            emit(Result.Error(throwable.toRepositoryMessage()))
        }
    }

    override fun searchCharacters(
        name: String?,
        status: String?,
        species: String?,
    ): Flow<Result<List<Character>>> = flow {
        emit(Result.Loading)
        val searchKey = buildSearchKey(name, status, species)

        val cached = characterDao.getBySearchKey(searchKey)
        if (cached.isNotEmpty()) {
            emit(Result.Success(cached.map { it.toModel() }))
        }

        try {
            val response = api.searchCharacters(name, status, species)
            val fresh = response.results
            if (fresh.isEmpty()) {
                emit(Result.Error(AppConstants.NULL_RESPONSE_MESSAGE))
                return@flow
            }
            characterDao.insertAll(
                fresh.map {
                    CharacterEntity.fromModel(
                        character = it,
                        page = AppConstants.START_PAGE,
                        searchKey = searchKey,
                    )
                },
            )
            emit(Result.Success(fresh))
        } catch (throwable: Throwable) {
            emit(Result.Error(throwable.toRepositoryMessage()))
        }
    }

    override fun getCharacterById(characterId: Int): Flow<Result<Character>> = flow {
        emit(Result.Loading)

        val cached = characterDao.getById(characterId)
        if (cached != null) {
            emit(Result.Success(cached.toModel()))
        } else {
            emit(Result.Error(AppConstants.NULL_RESPONSE_MESSAGE))
        }
    }
}

private fun buildSearchKey(
    name: String?,
    status: String?,
    species: String?,
): String {
    val normalizedName = name?.trim().orEmpty()
    val normalizedStatus = status?.trim().orEmpty()
    val normalizedSpecies = species?.trim().orEmpty()
    return listOf(normalizedName, normalizedStatus, normalizedSpecies)
        .joinToString(AppConstants.SEARCH_KEY_SEPARATOR)
}
