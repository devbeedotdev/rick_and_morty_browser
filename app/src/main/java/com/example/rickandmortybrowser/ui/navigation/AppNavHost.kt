package com.example.rickandmortybrowser.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rickandmortybrowser.ui.characters.CharacterListScreen
import com.example.rickandmortybrowser.ui.characters.CharacterListUiState

private object Routes {
    const val CharacterList = "character_list"
}

@Composable
fun AppNavHost(
    characterListUiState: CharacterListUiState,
    onRetryCharacters: () -> Unit,
    onLoadNextCharactersPage: () -> Unit,
    onRetryLoadNextCharactersPage: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.CharacterList,
    ) {
        composable(Routes.CharacterList) {
            CharacterListScreen(
                uiState = characterListUiState,
                onRetry = onRetryCharacters,
                onLoadNextPage = onLoadNextCharactersPage,
                onRetryLoadNextPage = onRetryLoadNextCharactersPage,
            )
        }
    }
}
