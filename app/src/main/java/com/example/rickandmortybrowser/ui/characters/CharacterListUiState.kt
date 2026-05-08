package com.example.rickandmortybrowser.ui.characters

import com.example.rickandmortybrowser.data.remote.model.Character

sealed class CharacterListUiState {
    data object Loading : CharacterListUiState()
    data class Success(val characters: List<Character>) : CharacterListUiState()
    data object Empty : CharacterListUiState()
    data class Error(val message: String) : CharacterListUiState()
}
