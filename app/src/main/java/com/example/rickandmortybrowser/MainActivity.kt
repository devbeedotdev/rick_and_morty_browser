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
            RickAndMortyBrowserTheme {
                AppNavHost(
                    characterListUiState = uiState,
                    onRetryCharacters = characterListViewModel::loadCharacters,
                )
            }
        }
    }
}
