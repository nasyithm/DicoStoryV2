package com.nasyithm.dicostoryv2.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.nasyithm.dicostoryv2.data.local.pref.UserModel
import com.nasyithm.dicostoryv2.data.local.pref.UserPreference
import com.nasyithm.dicostoryv2.data.local.room.StoryDatabase
import com.nasyithm.dicostoryv2.data.remote.response.StoryDetailResponse
import com.nasyithm.dicostoryv2.data.remote.response.ErrorResponse
import com.nasyithm.dicostoryv2.data.remote.response.ListStoryItem
import com.nasyithm.dicostoryv2.data.remote.response.LoginResponse
import com.nasyithm.dicostoryv2.data.remote.response.StoriesResponse
import com.nasyithm.dicostoryv2.data.remote.retrofit.ApiService
import com.nasyithm.dicostoryv2.util.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
){
    fun register(name: String, email: String, password: String): LiveData<Result<ErrorResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message

            Log.d("StoryRepository", "register: $errorMessage")
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message

            Log.d("StoryRepository", "login: $errorMessage")
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun getStoryDetail(storyId: String): LiveData<Result<StoryDetailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoryDetail(storyId)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message

            Log.d("StoryRepository", "getStoryDetail: $errorMessage")
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun addStory(file: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?): LiveData<Result<ErrorResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = apiService.addStory(file, description, lat, lon)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message

                Log.d("StoryRepository", "addStory: $errorMessage")
                emit(Result.Error(errorMessage.toString()))
            }
        }
    }

    fun getStoriesWithLocation(): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message

            Log.d("StoryRepository", "getStoriesWithLocation: $errorMessage")
            emit(Result.Error(errorMessage.toString()))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository = StoryRepository(userPreference, apiService, storyDatabase)
    }
}