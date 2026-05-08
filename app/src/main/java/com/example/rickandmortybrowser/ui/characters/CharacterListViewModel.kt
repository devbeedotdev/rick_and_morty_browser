package com.example.rickandmortybrowser.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortybrowser.data.repository.CharacterRepository
import com.example.rickandmortybrowser.data.repository.Result
import com.example.rickandmortybrowser.util.AppConstants
import com.example.rickandmortybrowser.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterListUiState>(CharacterListUiState.Loading)
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()
    val isOffline: StateFlow<Boolean> = networkMonitor.isConnected
        .map { connected -> !connected }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = false,
        )
    private var currentPage = AppConstants.START_PAGE
    private var isPageRequestInProgress = false

    init {
        loadCharacters()
    }

    fun loadCharacters(page: Int = AppConstants.START_PAGE) {
        currentPage = page
        isPageRequestInProgress = false
        viewModelScope.launch {
            characterRepository.getCharacters(page).collectLatest { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> CharacterListUiState.Loading
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            CharacterListUiState.Empty
                        } else {
                            CharacterListUiState.Success(result.data)
                        }
                    }
                    is Result.Error -> CharacterListUiState.Error(result.message)
                }
            }
        }
    }

    fun loadNextPage() {
        if (isPageRequestInProgress) return
        val currentState = _uiState.value as? CharacterListUiState.Success ?: return
        if (currentState.characters.isEmpty()) return

        isPageRequestInProgress = true
        _uiState.value = currentState.copy(
            isAppending = true,
            appendErrorMessage = null,
        )
        val nextPage = currentPage + 1

        viewModelScope.launch {
            characterRepository.getCharacters(nextPage).collectLatest { result ->
                when (result) {
                    is Result.Loading -> Unit
                    is Result.Success -> {
                        val merged = (currentState.characters + result.data).distinctBy { it.id }
                        _uiState.value = CharacterListUiState.Success(
                            characters = merged,
                            isAppending = false,
                            appendErrorMessage = null,
                        )
                        if (result.data.isNotEmpty()) {
                            currentPage = nextPage
                        }
                        isPageRequestInProgress = false
                    }
                    is Result.Error -> {
                        _uiState.value = currentState.copy(
                            isAppending = false,
                            appendErrorMessage = result.message,
                        )
                        isPageRequestInProgress = false
                    }
                }
            }
        }
    }

    fun retryLoadNextPage() {
        loadNextPage()
    }
}
