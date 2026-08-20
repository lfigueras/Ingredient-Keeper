package com.lovely.pastrykeeper.viewmodel

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AddPastryViewModel : ViewModel() {
    var pastryName by mutableStateOf("")
        private set

    var category by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun onPastryNameChange(newName: String) {
        pastryName = newName
    }

    fun onCategoryChange(newCategory: String) {
        category = newCategory
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
    }

    fun onImageSelected(uri: Uri?) {
        selectedImageUri = uri
    }

}