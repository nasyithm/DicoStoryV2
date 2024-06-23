package com.nasyithm.dicostoryv2.di

import android.content.Context
import com.nasyithm.dicostoryv2.data.StoryRepository
import com.nasyithm.dicostoryv2.data.local.room.StoryDatabase
import com.nasyithm.dicostoryv2.data.local.pref.UserPreference
import com.nasyithm.dicostoryv2.data.local.pref.dataStore
import com.nasyithm.dicostoryv2.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(pref, apiService, storyDatabase)
    }
}