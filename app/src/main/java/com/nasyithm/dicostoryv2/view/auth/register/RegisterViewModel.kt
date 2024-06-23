package com.nasyithm.dicostoryv2.view.auth.register

import androidx.lifecycle.ViewModel
import com.nasyithm.dicostoryv2.data.StoryRepository

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}