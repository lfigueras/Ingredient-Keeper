package com.lovely.pastrykeeper.ui.screens.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lovely.pastrykeeper.viewmodel.AddPastryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPastryScreen(
    onBackClick: () -> Unit, viewModel: AddPastryViewModel = viewModel()
) {

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.onImageSelected(uri)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Add Pastry")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Pastry Name *"
            )

            OutlinedTextField(
                value = viewModel.pastryName,
                onValueChange = {
                    viewModel.onPastryNameChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("e.g. Chocolate Moist Cake")
                }
            )

            Text(
                text = "Category"
            )

            OutlinedTextField(
                value = viewModel.category,
                onValueChange = {
                    viewModel.onCategoryChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("e.g. Cakes")
                }
            )

            Text(
                text = "Short Description"
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = {
                    viewModel.onDescriptionChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Tell us a little about this pastry")
                },
                minLines = 3
            )

            Text(
                text = "Pastry Photo"
            )

            OutlinedButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Choose Photo")
            }

            if (viewModel.selectedImageUri != null) {
                Text("Photo selected ✓")
            }
        }
    }
}
