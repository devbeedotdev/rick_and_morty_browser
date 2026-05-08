package com.example.rickandmortybrowser.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmortybrowser.ui.characters.CharacterListScreen
import com.example.rickandmortybrowser.ui.characters.CharacterListUiState
import com.example.rickandmortybrowser.ui.detail.CharacterDetailScreen
import com.example.rickandmortybrowser.ui.detail.DetailViewModel

private object Routes {
    const val CharacterList = "character_list"
    const val CharacterDetail = "character_detail/{characterId}"
    fun characterDetailRoute(characterId: Int): String = "character_detail/$characterId"
}

@Composable
fun AppNavHost(
    characterListUiState: CharacterListUiState,
    isCharacterListOffline: Boolean,
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
                isOffline = isCharacterListOffline,
                onRetry = onRetryCharacters,
                onCharacterClick = { characterId ->
                    navController.navigate(Routes.characterDetailRoute(characterId))
                },
                onLoadNextPage = onLoadNextCharactersPage,
                onRetryLoadNextPage = onRetryLoadNextCharactersPage,
            )
        }

        composable(
            route = Routes.CharacterDetail,
            arguments = listOf(navArgument("characterId") { type = NavType.IntType }),
        ) {
            val viewModel: DetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            CharacterDetailScreen(uiState = uiState)
        }
    }
}
