package com.example.rickandmortybrowser.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rickandmortybrowser.data.local.converter.RoomConverters
import com.example.rickandmortybrowser.data.local.dao.CharacterDao
import com.example.rickandmortybrowser.data.local.dao.EpisodeDao
import com.example.rickandmortybrowser.data.local.entity.CharacterEntity
import com.example.rickandmortybrowser.data.local.entity.EpisodeEntity

@Database(
    entities = [CharacterEntity::class, EpisodeEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun episodeDao(): EpisodeDao
}
