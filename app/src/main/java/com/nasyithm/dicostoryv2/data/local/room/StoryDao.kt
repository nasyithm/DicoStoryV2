package com.nasyithm.dicostoryv2.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nasyithm.dicostoryv2.data.local.entity.Story
import com.nasyithm.dicostoryv2.data.remote.response.ListStoryItem

@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getStories(): List<Story>

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int, ListStoryItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStories(stories: Story)

    @Query("DELETE FROM story")
    suspend fun deleteAllStories()
}
