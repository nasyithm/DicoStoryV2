package com.nasyithm.dicostoryv2.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nasyithm.dicostoryv2.data.StoryRepository
import com.nasyithm.dicostoryv2.di.Injection
import com.nasyithm.dicostoryv2.view.auth.login.LoginViewModel
import com.nasyithm.dicostoryv2.view.auth.register.RegisterViewModel
import com.nasyithm.dicostoryv2.view.main.MainViewModel
import com.nasyithm.dicostoryv2.view.story.add.AddStoryViewModel
import com.nasyithm.dicostoryv2.view.story.detail.StoryDetailViewModel
import com.nasyithm.dicostoryv2.view.story.maps.StoriesMapsViewModel

class ViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StoriesMapsViewModel::class.java) -> {
                StoriesMapsViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) = ViewModelFactory(Injection.provideRepository(context))
    }
}