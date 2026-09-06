package com.lovely.bakingrecipes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lovely.bakingrecipes.data.ImageStorage
import com.lovely.bakingrecipes.data.Pastry
import com.lovely.bakingrecipes.data.PastryWithIngredients
import com.lovely.bakingrecipes.repository.PastryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PastryDetailViewModel(
    application: Application,
    private val repository: PastryRepository,
    pastryId: Int
) : AndroidViewModel(application) {

    val pastry: StateFlow<PastryWithIngredients?> =
        repository.getPastryById(pastryId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun deletePastry(pastry: Pastry) {
        viewModelScope.launch {
            repository.deletePastry(pastry)
            ImageStorage.deleteIfLocal(getApplication(), pastry.imageUri)
        }
    }
}
