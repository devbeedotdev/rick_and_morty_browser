package com.example.rickandmortybrowser.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortybrowser.data.repository.CharacterRepository
import com.example.rickandmortybrowser.data.repository.Result
import com.example.rickandmortybrowser.util.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterListUiState>(CharacterListUiState.Loading)
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters(page: Int = AppConstants.START_PAGE) {
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
}
