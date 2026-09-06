package com.lovely.bakingrecipes.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lovely.bakingrecipes.repository.PastryRepository

class PastryDetailViewModelFactory(
    private val application: Application,
    private val repository: PastryRepository,
    private val pastryId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(PastryDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PastryDetailViewModel(application, repository, pastryId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
