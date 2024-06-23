package com.nasyithm.dicostoryv2.view.story.add

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nasyithm.dicostoryv2.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _imageUriData = MutableLiveData<Uri>()
    val imageUriData: LiveData<Uri> = _imageUriData

    fun setImageUriData(imageUri: Uri) {
        _imageUriData.value = imageUri
    }

    fun addStory(file: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?) =
        repository.addStory(file, description, lat, lon)
}