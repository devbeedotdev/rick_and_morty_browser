package com.example.rickandmortybrowser.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromEpisodeList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toEpisodeList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}
