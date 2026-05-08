package com.example.rickandmortybrowser.util

object AppConstants {
    const val BASE_URL = "https://rickandmortyapi.com/api/"

    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val NETWORK_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L
    const val TIMEOUT_HTTP_STATUS_CODE = 408
    const val NETWORK_ERROR_HTTP_STATUS_CODE = 503
    const val EMPTY_ERROR_RESPONSE_BODY = "{}"

    const val HTTP_CACHE_SIZE_BYTES = 10L * 1024L * 1024L
    const val ROOM_DB_NAME = "rick_and_morty.db"

    const val START_PAGE = 1
    const val DEFAULT_PAGE_SIZE = 20
    const val SEARCH_DEBOUNCE_MS = 500L
    const val SEARCH_KEY_SEPARATOR = "|"

    const val NETWORK_TIMEOUT_MESSAGE = "Request timed out. Please try again."
    const val NETWORK_ERROR_MESSAGE = "Network error. Check your connection and try again."
    const val UNKNOWN_ERROR_MESSAGE = "Something went wrong. Please try again."
    const val NULL_RESPONSE_MESSAGE = "Server returned an empty response."

    object Tables {
        const val CHARACTERS = "characters"
        const val EPISODES = "episodes"
    }

    object CharacterColumns {
        const val ID = "id"
        const val PAGE = "page"
        const val NAME = "name"
        const val GENDER = "gender"
        const val SPECIES = "species"
        const val STATUS = "status"
        const val IMAGE = "image"
        const val EPISODE_URLS = "episode_urls"
        const val ORIGIN_NAME = "origin_name"
        const val ORIGIN_URL = "origin_url"
        const val LOCATION_NAME = "location_name"
        const val LOCATION_URL = "location_url"
        const val SEARCH_KEY = "search_key"
    }

    object Filters {
        const val STATUS_ALIVE = "alive"
        const val STATUS_DEAD = "dead"
        const val STATUS_UNKNOWN = "unknown"
        const val SPECIES_HUMAN = "human"
        const val SPECIES_ALIEN = "alien"
    }

    object EpisodeColumns {
        const val ID = "id"
        const val NAME = "name"
        const val CODE = "code"
        const val AIR_DATE = "air_date"
    }
}
