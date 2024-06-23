package com.nasyithm.dicostoryv2.view.story.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasyithm.dicostoryv2.data.StoryRepository
import com.nasyithm.dicostoryv2.data.remote.response.StoryDetail

class StoryDetailViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _storyDetailData = MutableLiveData<StoryDetail>()
    val storyDetailData: LiveData<StoryDetail> = _storyDetailData

    private val _dataLoaded = MutableLiveData<Boolean>()
    val dataLoaded: LiveData<Boolean> = _dataLoaded

    init {
        setDataLoaded(false)
    }

    fun setDataLoaded(isDataLoaded: Boolean) {
        _dataLoaded.value = isDataLoaded
    }

    fun setStoryDetailData(storyDetail: StoryDetail) {
        _storyDetailData.value = storyDetail
    }

    fun getStoryDetail(storyId: String) = repository.getStoryDetail(storyId)
}