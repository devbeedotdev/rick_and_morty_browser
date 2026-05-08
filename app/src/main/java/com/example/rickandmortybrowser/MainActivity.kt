package com.example.rickandmortybrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.rickandmortybrowser.ui.characters.CharacterListViewModel
import com.example.rickandmortybrowser.ui.navigation.AppNavHost
import com.example.rickandmortybrowser.ui.theme.RickAndMortyBrowserTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val characterListViewModel: CharacterListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by characterListViewModel.uiState.collectAsState()
            val isOffline by characterListViewModel.isOffline.collectAsState()
            val searchQuery by characterListViewModel.query.collectAsState()
            val statusFilter by characterListViewModel.statusFilter.collectAsState()
            val speciesFilter by characterListViewModel.speciesFilter.collectAsState()
            RickAndMortyBrowserTheme {
                AppNavHost(
                    characterListUiState = uiState,
                    isCharacterListOffline = isOffline,
                    characterSearchQuery = searchQuery,
                    characterStatusFilter = statusFilter,
                    characterSpeciesFilter = speciesFilter,
                    onRetryCharacters = characterListViewModel::loadCharacters,
                    onClearCharacterSearch = characterListViewModel::clearSearch,
                    onCharacterSearchQueryChanged = characterListViewModel::onSearchQueryChanged,
                    onCharacterStatusFilterChanged = characterListViewModel::onStatusFilterSelected,
                    onCharacterSpeciesFilterChanged = characterListViewModel::onSpeciesFilterSelected,
                    onLoadNextCharactersPage = characterListViewModel::loadNextPage,
                    onRetryLoadNextCharactersPage = characterListViewModel::retryLoadNextPage,
                )
            }
        }
    }
}
