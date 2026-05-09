package com.example.rickandmortybrowser.ui.characters

import app.cash.turbine.test
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.data.remote.model.CharacterLocation
import com.example.rickandmortybrowser.data.remote.model.CharacterOrigin
import com.example.rickandmortybrowser.data.repository.CharacterRepository
import com.example.rickandmortybrowser.data.repository.Result
import com.example.rickandmortybrowser.util.AppConstants
import com.example.rickandmortybrowser.util.NetworkMonitor
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<CharacterRepository>()
    private val mockNetworkMonitor = mockk<NetworkMonitor>()

    private val testCharacter1 = Character(
        id = 1,
        name = "Rick",
        gender = "Male",
        species = "Human",
        status = "Alive",
        image = "https://example.com/rick.jpg",
        episode = listOf("https://example.com/ep1"),
        origin = CharacterOrigin("Earth", "https://example.com/earth"),
        location = CharacterLocation("Earth", "https://example.com/earth"),
    )

    private val testCharacter2 = Character(
        id = 2,
        name = "Morty",
        gender = "Male",
        species = "Human",
        status = "Alive",
        image = "https://example.com/morty.jpg",
        episode = listOf("https://example.com/ep1"),
        origin = CharacterOrigin("Earth", "https://example.com/earth"),
        location = CharacterLocation("Earth", "https://example.com/earth"),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): CharacterListViewModel {
        every { mockNetworkMonitor.isConnected } returns flow { emit(true) }
        return CharacterListViewModel(mockRepository, mockNetworkMonitor)
    }

    @Test
    fun clearSearch_resetsFilters() = runTest(testDispatcher) {
        every { mockRepository.getCharacters(AppConstants.START_PAGE) } returns flow {
            emit(Result.Success(listOf(testCharacter1, testCharacter2)))
        }
        every { mockNetworkMonitor.isConnected } returns flow { emit(true) }

        val viewModel = createViewModel()

        viewModel.onSearchQueryChanged("test")
        viewModel.onStatusFilterSelected("Alive")
        viewModel.onSpeciesFilterSelected("Human")

        assert(viewModel.query.value == "test")
        assert(viewModel.statusFilter.value == "Alive")
        assert(viewModel.speciesFilter.value == "Human")

        viewModel.clearSearch()

        assert(viewModel.query.value.isEmpty())
        assert(viewModel.statusFilter.value == null)
        assert(viewModel.speciesFilter.value == null)
    }
}
