package com.nasyithm.dicostoryv2.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nasyithm.dicostoryv2.data.local.entity.RemoteKeys
import com.nasyithm.dicostoryv2.data.local.entity.Story

@Database(entities = [Story::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        fun getInstance(context: Context): StoryDatabase = Room.databaseBuilder(
            context.applicationContext,
            StoryDatabase::class.java, "StoryDatabase"
        ).build()
    }
}