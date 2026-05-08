package com.example.rickandmortybrowser.data.repository

import com.example.rickandmortybrowser.data.remote.model.Episode
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    fun getEpisodesFromUrls(episodeUrls: List<String>): Flow<Result<List<Episode>>>
}
