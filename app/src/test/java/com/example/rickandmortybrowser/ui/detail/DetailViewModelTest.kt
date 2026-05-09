package com.example.rickandmortybrowser.ui.detail

import app.cash.turbine.test
import androidx.lifecycle.SavedStateHandle
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.data.remote.model.CharacterLocation
import com.example.rickandmortybrowser.data.remote.model.CharacterOrigin
import com.example.rickandmortybrowser.data.remote.model.Episode
import com.example.rickandmortybrowser.data.repository.CharacterRepository
import com.example.rickandmortybrowser.data.repository.EpisodeRepository
import com.example.rickandmortybrowser.data.repository.Result
import com.example.rickandmortybrowser.util.AppConstants
import com.example.rickandmortybrowser.util.NetworkMonitor
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val mockCharacterRepository = mockk<CharacterRepository>()
    private val mockEpisodeRepository = mockk<EpisodeRepository>()
    private val mockNetworkMonitor = mockk<NetworkMonitor>()
    private val savedStateHandle = SavedStateHandle().apply {
        set("characterId", 1)
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val testCharacter = Character(
        id = 1,
        name = "Rick",
        gender = "Male",
        species = "Human",
        status = "Alive",
        image = "https://example.com/rick.jpg",
        episode = listOf(
            "https://example.com/ep1",
            "https://example.com/ep2",
            "https://example.com/ep3",
        ),
        origin = CharacterOrigin("Earth", "https://example.com/earth"),
        location = CharacterLocation("Earth", "https://example.com/earth"),
    )

    private val testEpisodes = listOf(
        Episode(1, "Pilot", "S01E01", "December 2, 2013"),
        Episode(2, "Lawnmower Dog", "S01E02", "December 9, 2013"),
        Episode(3, "Anatomy Park", "S01E03", "December 16, 2013"),
    )

    private fun createViewModel(): DetailViewModel {
        return DetailViewModel(
            savedStateHandle,
            mockCharacterRepository,
            mockEpisodeRepository,
            mockNetworkMonitor,
        )
    }

    @Test
    fun loadCharacterAndEpisodes_emitsSuccessWithCharacterAndEpisodes() = runTest(testDispatcher) {
        every { mockCharacterRepository.getCharacterById(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Success(testCharacter))
        }
        every {
            mockEpisodeRepository.getEpisodesFromUrls(
                listOf(
                    "https://example.com/ep1",
                    "https://example.com/ep2",
                    "https://example.com/ep3",
                ),
            )
        } returns flow {
            emit(Result.Loading)
            emit(Result.Success(testEpisodes))
        }
        every { mockNetworkMonitor.isConnected } returns flow { emit(true) }

        val viewModel = createViewModel()
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assert(loadingState is CharacterDetailUiState.Loading)

            val successWithLoadingState = awaitItem()
            assert(successWithLoadingState is CharacterDetailUiState.Success)
            assert((successWithLoadingState as CharacterDetailUiState.Success).character.name == "Rick")
            assert(successWithLoadingState.isEpisodesLoading)

            val successState = awaitItem()
            assert(successState is CharacterDetailUiState.Success)
            assert((successState as CharacterDetailUiState.Success).episodes.size == 3)
            assert(!successState.isEpisodesLoading)
            assert(successState.episodes.first().name == "Pilot")
        }
    }

    @Test
    fun loadCharacterAndEpisodes_handlesMissingCacheOffline() = runTest(testDispatcher) {
        every { mockCharacterRepository.getCharacterById(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(AppConstants.NULL_RESPONSE_MESSAGE))
        }
        every { mockNetworkMonitor.isConnected } returns flow { emit(false) }

        val viewModel = createViewModel()
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assert(loadingState is CharacterDetailUiState.Loading)

            val offlineState = awaitItem()
            assert(offlineState is CharacterDetailUiState.OfflineNoCache)
        }
    }

    @Test
    fun loadCharacterAndEpisodes_emitsErrorWhenOnline() = runTest(testDispatcher) {
        val errorMessage = "Network error"
        every { mockCharacterRepository.getCharacterById(1) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(errorMessage))
        }
        every { mockNetworkMonitor.isConnected } returns flow { emit(true) }

        val viewModel = createViewModel()
        viewModel.uiState.test {
            val loadingState = awaitItem()
            assert(loadingState is CharacterDetailUiState.Loading)

            val errorState = awaitItem()
            assert(errorState is CharacterDetailUiState.Error)
            assert((errorState as CharacterDetailUiState.Error).message == errorMessage)
        }
    }

    @Test
    fun retry_reloadsCharacterAndEpisodes() = runTest(testDispatcher) {
        var callCount = 0
        every { mockCharacterRepository.getCharacterById(1) } returns flow {
            callCount++
            emit(Result.Success(testCharacter))
        }
        every {
            mockEpisodeRepository.getEpisodesFromUrls(
                listOf(
                    "https://example.com/ep1",
                    "https://example.com/ep2",
                    "https://example.com/ep3",
                ),
            )
        } returns flow {
            emit(Result.Success(testEpisodes))
        }
        every { mockNetworkMonitor.isConnected } returns flow { emit(true) }

        val viewModel = createViewModel()
        viewModel.uiState.test {
            skipItems(2) // Skip Loading and first Success states

            // Verify initial load
            assert(callCount == 1)

            // Call retry
            viewModel.retry()

            skipItems(2) // Skip new states

            // Verify second load
            assert(callCount == 2)
        }
    }

    @Test
    fun loadCharacterAndEpisodes_takesFirst3Episodes() = runTest(testDispatcher) {
        val characterWith5Episodes = testCharacter.copy(
            episode = listOf(
                "https://example.com/ep1",
                "https://example.com/ep2",
                "https://example.com/ep3",
                "https://example.com/ep4",
                "https://example.com/ep5",
            ),
        )

        every { mockCharacterRepository.getCharacterById(1) } returns flow {
            emit(Result.Success(characterWith5Episodes))
        }
        every {
            mockEpisodeRepository.getEpisodesFromUrls(
                listOf(
                    "https://example.com/ep1",
                    "https://example.com/ep2",
                    "https://example.com/ep3",
                ),
            )
        } returns flow {
            emit(Result.Success(testEpisodes))
        }
        every { mockNetworkMonitor.isConnected } returns flow { emit(true) }

        val viewModel = createViewModel()
        viewModel.uiState.test {
            skipItems(1)

            skipItems(1) // Skip first Success

            val finalState = awaitItem()
            assert(finalState is CharacterDetailUiState.Success)
            assert((finalState as CharacterDetailUiState.Success).episodes.size == 3)
        }
    }

    @Test
    fun loadCharacterAndEpisodes_emitsErrorWhenEpisodeLoadingFails() = runTest(testDispatcher) {
        val episodeError = "Failed to load episodes"
        every { mockCharacterRepository.getCharacterById(1) } returns flow {
            emit(Result.Success(testCharacter))
        }
        every {
            mockEpisodeRepository.getEpisodesFromUrls(
                listOf(
                    "https://example.com/ep1",
                    "https://example.com/ep2",
                    "https://example.com/ep3",
                ),
            )
        } returns flow {
            emit(Result.Error(episodeError))
        }
        every { mockNetworkMonitor.isConnected } returns flow { emit(true) }

        val viewModel = createViewModel()
        viewModel.uiState.test {
            skipItems(1) // Skip Loading
            skipItems(1) // Skip first Success with episodes loading

            val errorState = awaitItem()
            assert(errorState is CharacterDetailUiState.Error)
            assert((errorState as CharacterDetailUiState.Error).message == episodeError)
        }
    }

    @Test
    fun loadCharacterAndEpisodes_usesUnknownErrorWhenBlank() = runTest(testDispatcher) {
        every { mockCharacterRepository.getCharacterById(1) } returns flow {
            emit(Result.Success(testCharacter))
        }
        every {
            mockEpisodeRepository.getEpisodesFromUrls(
                listOf(
                    "https://example.com/ep1",
                    "https://example.com/ep2",
                    "https://example.com/ep3",
                ),
            )
        } returns flow {
            emit(Result.Error("")) // Empty error message
        }
        every { mockNetworkMonitor.isConnected } returns flow { emit(true) }

        val viewModel = createViewModel()
        viewModel.uiState.test {
            skipItems(1)
            skipItems(1)

            val errorState = awaitItem()
            assert(errorState is CharacterDetailUiState.Error)
            assert((errorState as CharacterDetailUiState.Error).message == AppConstants.UNKNOWN_ERROR_MESSAGE)
        }
    }
}
