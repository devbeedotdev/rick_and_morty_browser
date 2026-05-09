package com.example.rickandmortybrowser.data.repository

import app.cash.turbine.test
import com.example.rickandmortybrowser.data.local.dao.CharacterDao
import com.example.rickandmortybrowser.data.local.entity.CharacterEntity
import com.example.rickandmortybrowser.data.remote.api.RickAndMortyApi
import com.example.rickandmortybrowser.data.remote.model.Character
import com.example.rickandmortybrowser.data.remote.model.CharacterLocation
import com.example.rickandmortybrowser.data.remote.model.CharacterOrigin
import com.example.rickandmortybrowser.data.remote.model.CharacterPageResponse
import com.example.rickandmortybrowser.util.AppConstants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class CharacterRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockApi = mockk<RickAndMortyApi>()
    private val mockDao = mockk<CharacterDao>()
    private val repository = CharacterRepositoryImpl(mockApi, mockDao)

    private val sep = AppConstants.SEARCH_KEY_SEPARATOR

    private val testCharacter = Character(
        id = 1,
        name = "Rick",
        gender = "Male",
        species = "Human",
        status = "Alive",
        image = "https://example.com/rick.jpg",
        episode = listOf("https://example.com/ep1", "https://example.com/ep2"),
        origin = CharacterOrigin("Earth", "https://example.com/earth"),
        location = CharacterLocation("Earth", "https://example.com/earth"),
    )

   private val defaultPageSearchKey get() = listOf("", "", "").joinToString(sep)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

 
    @Test
    fun getCharacters_whenCached_emitsLoadingThenCachedSuccessThenFreshSuccess() =
        runTest(testDispatcher) {
            val page = 1
            val cachedEntity = CharacterEntity.fromModel(testCharacter, page, defaultPageSearchKey)
            val freshCharacter = testCharacter.copy(name = "Morty")
            val apiResponse = CharacterPageResponse(listOf(freshCharacter))
            val freshEntity = CharacterEntity.fromModel(freshCharacter, page, defaultPageSearchKey)

            coEvery { mockDao.getByPage(page) } returns listOf(cachedEntity)
            coEvery { mockApi.getCharacters(page) } returns apiResponse
            coEvery { mockDao.insertAll(listOf(freshEntity)) } returns Unit

            repository.getCharacters(page).test {
                assert(awaitItem() is Result.Loading)

                val cached = awaitItem()
                assert(cached is Result.Success)
                assert((cached as Result.Success).data.first().id == 1)

                val fresh = awaitItem()
                assert(fresh is Result.Success)
                assert((fresh as Result.Success).data.first().name == "Morty")

                awaitComplete()
            }
        }

   
    @Test
    fun getCharacters_whenNotCached_apiSucceeds_emitsLoadingThenFreshSuccess() =
        runTest(testDispatcher) {
            val page = 1
            val apiResponse = CharacterPageResponse(listOf(testCharacter))
            val freshEntity = CharacterEntity.fromModel(testCharacter, page, defaultPageSearchKey)

            coEvery { mockDao.getByPage(page) } returns emptyList()
            coEvery { mockApi.getCharacters(page) } returns apiResponse
            coEvery { mockDao.insertAll(listOf(freshEntity)) } returns Unit

            repository.getCharacters(page).test {
                assert(awaitItem() is Result.Loading)

                val result = awaitItem()
                assert(result is Result.Success)
                assert((result as Result.Success).data.first().name == "Rick")

                awaitComplete()
            }
        }

    @Test
    fun getCharacters_whenNotCached_apiThrows_emitsLoadingThenError() =
        runTest(testDispatcher) {
            val page = 1

            coEvery { mockDao.getByPage(page) } returns emptyList()
            coEvery { mockApi.getCharacters(page) } throws Exception("Network error")

            repository.getCharacters(page).test {
                assert(awaitItem() is Result.Loading)

                val result = awaitItem()
                assert(result is Result.Error)
                assert((result as Result.Error).message.isNotBlank())

                awaitComplete()
            }
        }

    @Test
    fun getCharacters_whenApiReturnsEmpty_emitsError() = runTest(testDispatcher) {
        val page = 1

        coEvery { mockDao.getByPage(page) } returns emptyList()
        coEvery { mockApi.getCharacters(page) } returns CharacterPageResponse(emptyList())

        repository.getCharacters(page).test {
            assert(awaitItem() is Result.Loading)

            val result = awaitItem()
            assert(result is Result.Error)
            assert((result as Result.Error).message == AppConstants.NULL_RESPONSE_MESSAGE)

            awaitComplete()
        }
    }

   
    @Test
    fun getCharacters_whenCached_apiThrows_emitsCachedSuccessThenError() =
        runTest(testDispatcher) {
            val page = 1
            val cachedEntity = CharacterEntity.fromModel(testCharacter, page, defaultPageSearchKey)

            coEvery { mockDao.getByPage(page) } returns listOf(cachedEntity)
            coEvery { mockApi.getCharacters(page) } throws Exception("Network error")

            repository.getCharacters(page).test {
                assert(awaitItem() is Result.Loading)

                val cached = awaitItem()
                assert(cached is Result.Success)
                assert((cached as Result.Success).data.first().id == 1)

                val error = awaitItem()
                assert(error is Result.Error)

                awaitComplete()
            }
        }

    private fun searchKey(name: String?, status: String?, species: String?): String =
        listOf(
            name?.trim().orEmpty(),
            status?.trim().orEmpty(),
            species?.trim().orEmpty(),
        ).joinToString(sep)

   
    @Test
    fun searchCharacters_cacheMiss_apiSucceeds_emitsLoadingThenSuccess() =
        runTest(testDispatcher) {
            val name = "Rick"
            val status = "Alive"
            val key = searchKey(name, status, null)
            val freshEntity = CharacterEntity.fromModel(
                testCharacter, AppConstants.START_PAGE, key,
            )

            coEvery { mockDao.getBySearchKey(key) } returns emptyList()
            coEvery { mockApi.searchCharacters(name, status, null) } returns
                CharacterPageResponse(listOf(testCharacter))
            coEvery { mockDao.insertAll(listOf(freshEntity)) } returns Unit

            repository.searchCharacters(name, status, null).test {
                assert(awaitItem() is Result.Loading)

                val result = awaitItem()
                assert(result is Result.Success)
                assert((result as Result.Success).data.first().name == "Rick")

                awaitComplete()
            }
        }

   
    @Test
    fun searchCharacters_cacheHit_emitsCachedThenFreshSuccess() =
        runTest(testDispatcher) {
            val name = "Rick"
            val status = "Alive"
            val key = searchKey(name, status, null)
            val cachedEntity = CharacterEntity.fromModel(
                testCharacter, AppConstants.START_PAGE, key,
            )
            val freshCharacter = testCharacter.copy(name = "Rick Sanchez")
            val freshEntity = CharacterEntity.fromModel(
                freshCharacter, AppConstants.START_PAGE, key,
            )

            coEvery { mockDao.getBySearchKey(key) } returns listOf(cachedEntity)
            coEvery { mockApi.searchCharacters(name, status, null) } returns
                CharacterPageResponse(listOf(freshCharacter))
            coEvery { mockDao.insertAll(listOf(freshEntity)) } returns Unit

            repository.searchCharacters(name, status, null).test {
                assert(awaitItem() is Result.Loading)

                val cached = awaitItem()
                assert(cached is Result.Success)
                assert((cached as Result.Success).data.first().id == 1)

                val fresh = awaitItem()
                assert(fresh is Result.Success)
                assert((fresh as Result.Success).data.first().name == "Rick Sanchez")

                awaitComplete()
            }
        }

    
    @Test
    fun searchCharacters_apiThrows_emitsError() = runTest(testDispatcher) {
        val name = "Rick"
        val key = searchKey(name, null, null)

        coEvery { mockDao.getBySearchKey(key) } returns emptyList()
        coEvery { mockApi.searchCharacters(name, null, null) } throws Exception("Timeout")

        repository.searchCharacters(name, null, null).test {
            assert(awaitItem() is Result.Loading)

            val result = awaitItem()
            assert(result is Result.Error)
            assert((result as Result.Error).message.isNotBlank())

            awaitComplete()
        }
    }

    @Test
    fun getCharacterById_whenCached_emitsLoadingThenSuccess() = runTest(testDispatcher) {
        val cachedEntity = CharacterEntity.fromModel(testCharacter, 1, defaultPageSearchKey)

        coEvery { mockDao.getById(1) } returns cachedEntity

        repository.getCharacterById(1).test {
            assert(awaitItem() is Result.Loading)

            val result = awaitItem()
            assert(result is Result.Success)
            assert((result as Result.Success).data.id == 1)

            awaitComplete()
        }
    }

    @Test
    fun getCharacterById_whenNotCached_emitsLoadingThenError() = runTest(testDispatcher) {
        coEvery { mockDao.getById(1) } returns null

        repository.getCharacterById(1).test {
            assert(awaitItem() is Result.Loading)

            val result = awaitItem()
            assert(result is Result.Error)
            assert((result as Result.Error).message == AppConstants.NULL_RESPONSE_MESSAGE)

            awaitComplete()
        }
    }

   
    @Test
    fun getCharacters_onApiSuccess_insertsCorrectEntities() = runTest(testDispatcher) {
        val page = 2
        val key = defaultPageSearchKey
        val expected = listOf(CharacterEntity.fromModel(testCharacter, page, key))

        coEvery { mockDao.getByPage(page) } returns emptyList()
        coEvery { mockApi.getCharacters(page) } returns CharacterPageResponse(listOf(testCharacter))
        coEvery { mockDao.insertAll(expected) } returns Unit

        repository.getCharacters(page).test {
            awaitItem() 
            awaitItem() 
            awaitComplete()
        }

        coVerify(exactly = 1) { mockDao.insertAll(expected) }
    }
}