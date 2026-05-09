package com.example.rickandmortybrowser.ui.characters

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.rickandmortybrowser.R
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.data.remote.model.CharacterLocation
import com.example.rickandmortybrowser.data.remote.model.CharacterOrigin
import org.junit.Rule
import org.junit.Test

class CharacterListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCharacters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            gender = "Male",
            species = "Human",
            status = "Alive",
            image = "https://example.com/rick.jpg",
            episode = listOf("https://example.com/ep1"),
            origin = CharacterOrigin("Earth", "https://example.com/earth"),
            location = CharacterLocation("Earth", "https://example.com/earth"),
        ),
        Character(
            id = 2,
            name = "Morty Smith",
            gender = "Male",
            species = "Human",
            status = "Alive",
            image = "https://example.com/morty.jpg",
            episode = listOf("https://example.com/ep1"),
            origin = CharacterOrigin("Earth", "https://example.com/earth"),
            location = CharacterLocation("Earth", "https://example.com/earth"),
        ),
    )

    @Test
    fun characterListScreen_showsLoadingState() {
        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.Loading,
                isOffline = false,
                query = "",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText("Searching for characters...")
            .assertIsDisplayed()
    }

    @Test
    fun characterListScreen_showsCardsOnSuccess() {
        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.Success(testCharacters),
                isOffline = false,
                query = "",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Morty Smith")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Human")
            .assertIsDisplayed()
    }

    @Test
    fun characterListScreen_showsEmptyState() {
        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.Empty,
                isOffline = false,
                query = "",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.empty_characters_message),
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.retry))
            .assertIsDisplayed()
    }

    @Test
    fun characterListScreen_showsErrorViewWithRetry() {
        val errorMessage = "Network connection failed"

        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.Error(errorMessage),
                isOffline = false,
                query = "",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.retry))
            .assertIsDisplayed()
    }

    @Test
    fun characterListScreen_showsSearchEmptyState() {
        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.SearchEmpty("NonexistentCharacter"),
                isOffline = false,
                query = "NonexistentCharacter",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.search_empty_characters_message,
                    "NonexistentCharacter",
                ),
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.clear_search))
            .assertIsDisplayed()
    }

    @Test
    fun characterListScreen_tapCharacterCard_invokesCallback() {
        var clickedCharacterId: Int? = null

        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.Success(testCharacters),
                isOffline = false,
                query = "",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = { clickedCharacterId = it },
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .performClick()

        assert(clickedCharacterId == 1)
    }

    @Test
    fun characterListScreen_retryButton_invokesRetryCallback() {
        var retryClicked = false

        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.Error("Error message"),
                isOffline = false,
                query = "",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = { retryClicked = true },
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.retry))
            .performClick()

        assert(retryClicked)
    }

    @Test
    fun characterListScreen_clearSearchButton_invokesClearSearchCallback() {
        var clearSearchClicked = false

        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.SearchEmpty("test"),
                isOffline = false,
                query = "test",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = { clearSearchClicked = true },
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.clear_search))
            .performClick()

        assert(clearSearchClicked)
    }

    @Test
    fun characterListScreen_displaysSearchBar() {
        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.Loading,
                isOffline = false,
                query = "",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(R.string.search_characters_placeholder),
            )
            .assertIsDisplayed()
    }

    @Test
    fun characterListScreen_showsMultipleCharacters() {
        val manyCharacters = testCharacters + testCharacters + testCharacters

        composeTestRule.setContent {
            CharacterListScreen(
                uiState = CharacterListUiState.Success(manyCharacters),
                isOffline = false,
                query = "",
                selectedStatus = null,
                selectedSpecies = null,
                onRetry = {},
                onClearSearch = {},
                onQueryChanged = {},
                onStatusSelected = {},
                onSpeciesSelected = {},
                onCharacterClick = {},
                onLoadNextPage = {},
                onRetryLoadNextPage = {},
            )
        }

        // Should display first character
        composeTestRule
            .onNodeWithText("Rick Sanchez")
            .assertIsDisplayed()

        // Should display Morty
        composeTestRule
            .onNodeWithText("Morty Smith")
            .assertIsDisplayed()
    }
}
