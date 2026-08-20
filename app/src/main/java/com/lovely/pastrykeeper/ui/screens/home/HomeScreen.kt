package com.lovely.pastrykeeper.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.lovely.pastrykeeper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddPastryClick: () -> Unit // Callback function triggered when the FloatingActionButton is clicked
) {
    // Scaffold provides a basic structure for the screen, including a TopAppBar and FloatingActionButton
    Scaffold(
        topBar = {
            // TopAppBar displays the title of the screen
            TopAppBar(
                title = {
                    Text("Pastry Ingredients Keeper") // Title text for the app bar
                }
            )
        },
        floatingActionButton = {
            // FloatingActionButton allows the user to add a new pastry
            FloatingActionButton(
                onClick = onAddPastryClick // Trigger the callback when clicked
            ) {
                // Icon displayed inside the FloatingActionButton
                Icon(
                    painter = painterResource(R.drawable.ic_add), // Icon resource for the "add" action
                    contentDescription = "Add pastry" // Content description for accessibility
                )
            }
        }
    ) { innerPadding ->
        // Content of the screen, displayed within the padding provided by the Scaffold
        Text(
            text = "No pastries yet", // Placeholder text when no pastries are available
            modifier = Modifier.padding(innerPadding) // Apply padding to avoid overlapping with other UI elements
        )
    }
}
