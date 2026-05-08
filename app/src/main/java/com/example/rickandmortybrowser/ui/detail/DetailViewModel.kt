package com.example.rickandmortybrowser.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.data.remote.model.Episode
import com.example.rickandmortybrowser.data.repository.CharacterRepository
import com.example.rickandmortybrowser.data.repository.EpisodeRepository
import com.example.rickandmortybrowser.data.repository.Result
import com.example.rickandmortybrowser.util.AppConstants
import com.example.rickandmortybrowser.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val characterRepository: CharacterRepository,
    private val episodeRepository: EpisodeRepository,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private val characterId: Int = checkNotNull(savedStateHandle["characterId"])

    init {
        loadCharacterAndEpisodes()
    }

    fun retry() {
        loadCharacterAndEpisodes()
    }

    private fun loadCharacterAndEpisodes() {
        viewModelScope.launch {
            characterRepository.getCharacterById(characterId).collectLatest { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = CharacterDetailUiState.Loading
                    is Result.Success -> {
                        val character = result.data
                        _uiState.value = CharacterDetailUiState.Success(
                            character = character,
                            episodes = emptyList(),
                            isEpisodesLoading = true,
                        )
                        loadFirstThreeEpisodes(character)
                    }
                    is Result.Error -> {
                        val isConnected = networkMonitor.isConnected.first()
                        _uiState.value = if (!isConnected) {
                            CharacterDetailUiState.OfflineNoCache
                        } else {
                            CharacterDetailUiState.Error(result.message)
                        }
                    }
                }
            }
        }
    }

    private fun loadFirstThreeEpisodes(character: Character) {
        viewModelScope.launch {
            val firstThreeEpisodeUrls = character.episode.take(3)
            if (firstThreeEpisodeUrls.isEmpty()) {
                _uiState.value = CharacterDetailUiState.Success(
                    character = character,
                    episodes = emptyList(),
                    isEpisodesLoading = false,
                )
                return@launch
            }

            episodeRepository.getEpisodesFromUrls(firstThreeEpisodeUrls).collectLatest { result ->
                when (result) {
                    is Result.Loading -> Unit
                    is Result.Success -> updateSuccessState(character, result.data)
                    is Result.Error -> {
                        _uiState.value = CharacterDetailUiState.Error(
                            result.message.ifBlank { AppConstants.UNKNOWN_ERROR_MESSAGE },
                        )
                    }
                }
            }
        }
    }

    private fun updateSuccessState(character: Character, episodes: List<Episode>) {
        _uiState.value = CharacterDetailUiState.Success(
            character = character,
            episodes = episodes.take(3),
            isEpisodesLoading = false,
        )
    }
}
