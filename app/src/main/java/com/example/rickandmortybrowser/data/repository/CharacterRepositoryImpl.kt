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
            characterDao.insertAll(fresh.map { CharacterEntity.fromModel(it, page) })
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

        val cached = characterDao.searchByFilters(name, status, species)
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
                fresh.map { CharacterEntity.fromModel(it, AppConstants.START_PAGE) },
            )
            emit(Result.Success(fresh))
        } catch (throwable: Throwable) {
            emit(Result.Error(throwable.toRepositoryMessage()))
        }
    }
}
