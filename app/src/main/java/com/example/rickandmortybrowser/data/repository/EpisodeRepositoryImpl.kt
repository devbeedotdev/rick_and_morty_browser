package com.example.rickandmortybrowser.data.repository

import com.example.rickandmortybrowser.data.local.dao.EpisodeDao
import com.example.rickandmortybrowser.data.local.entity.EpisodeEntity
import com.example.rickandmortybrowser.data.remote.api.RickAndMortyApi
import com.example.rickandmortybrowser.data.remote.model.Episode
import com.example.rickandmortybrowser.util.AppConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EpisodeRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
    private val episodeDao: EpisodeDao,
    private val gson: Gson,
) : EpisodeRepository {

    override fun getEpisodesFromUrls(episodeUrls: List<String>): Flow<Result<List<Episode>>> = flow {
        emit(Result.Loading)

        val ids = episodeUrls.mapNotNull { it.extractEpisodeId() }.distinct()
        if (ids.isEmpty()) {
            emit(Result.Error(AppConstants.NULL_RESPONSE_MESSAGE))
            return@flow
        }

        val cached = episodeDao.getByIds(ids)
        if (cached.isNotEmpty()) {
            emit(Result.Success(cached.map { it.toModel() }))
        }

        try {
            val fresh = api.getEpisodesByIds(ids.joinToString(",")).toEpisodes(gson)
            if (fresh.isEmpty()) {
                emit(Result.Error(AppConstants.NULL_RESPONSE_MESSAGE))
                return@flow
            }
            episodeDao.insertAll(fresh.map { EpisodeEntity.fromModel(it) })
            emit(Result.Success(fresh))
        } catch (throwable: Throwable) {
            emit(Result.Error(throwable.toRepositoryMessage()))
        }
    }
}

private fun String.extractEpisodeId(): Int? {
    return substringAfterLast('/').toIntOrNull()
}

private fun com.google.gson.JsonElement.toEpisodes(gson: Gson): List<Episode> {
    return when {
        isJsonArray -> {
            gson.fromJson(this, object : TypeToken<List<Episode>>() {}.type)
        }
        isJsonObject -> {
            listOf(gson.fromJson(this, Episode::class.java))
        }
        else -> emptyList()
    }
}
