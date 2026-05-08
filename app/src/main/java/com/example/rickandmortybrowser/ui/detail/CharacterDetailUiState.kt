package com.example.rickandmortybrowser.ui.detail

import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.data.remote.model.Episode

sealed class CharacterDetailUiState {
    data object Loading : CharacterDetailUiState()
    data class Success(
        val character: Character,
        val episodes: List<Episode>,
        val isEpisodesLoading: Boolean,
    ) : CharacterDetailUiState()
    data class Error(val message: String) : CharacterDetailUiState()
    data object OfflineNoCache : CharacterDetailUiState()
}
