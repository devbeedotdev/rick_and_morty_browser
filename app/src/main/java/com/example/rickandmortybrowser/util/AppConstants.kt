package com.example.rickandmortybrowser.util

object AppConstants {
    const val BASE_URL = "https://rickandmortyapi.com/api/"

    const val NETWORK_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L

    const val HTTP_CACHE_SIZE_BYTES = 10L * 1024L * 1024L
    const val ROOM_DB_NAME = "rick_and_morty.db"

    const val START_PAGE = 1
    const val DEFAULT_PAGE_SIZE = 20
}
