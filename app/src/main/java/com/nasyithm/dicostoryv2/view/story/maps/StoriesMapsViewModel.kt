package com.nasyithm.dicostoryv2.view.story.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasyithm.dicostoryv2.data.StoryRepository
import com.nasyithm.dicostoryv2.data.remote.response.ListStoryItem

class StoriesMapsViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _storiesWithLocationData = MutableLiveData<List<ListStoryItem>>()
    val storiesWithLocationData: LiveData<List<ListStoryItem>> = _storiesWithLocationData

    private val _dataLoaded = MutableLiveData<Boolean>()
    val dataLoaded: LiveData<Boolean> = _dataLoaded

    init {
        setDataLoaded(false)
    }

    fun setDataLoaded(isDataLoaded: Boolean) {
        _dataLoaded.value = isDataLoaded
    }

    fun setStoriesWithLocationData(stories: List<ListStoryItem>) {
        _storiesWithLocationData.value = stories
    }

    fun getStoriesWithLocation() = repository.getStoriesWithLocation()
}