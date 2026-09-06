package com.lovely.bakingrecipes.ui.screens.detail

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.lovely.bakingrecipes.data.Pastry
import com.lovely.bakingrecipes.data.PastryWithIngredients
import com.lovely.bakingrecipes.data.formatAmount
import com.lovely.bakingrecipes.ui.components.brandedTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastryDetailScreen(
    pastry: PastryWithIngredients?,
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onDeleteConfirmed: (Pastry) -> Unit,
    editedMessage: String? = null,
    onEditedMessageShown: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showImageViewer by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val baseServings = pastry?.pastry?.servings ?: 0
    var servings by remember(pastry?.pastry?.id) { mutableIntStateOf(baseServings) }
    val scale = if (baseServings > 0) servings.toDouble() / baseServings else 1.0

    // Show a one-time confirmation after returning from an edit.
    LaunchedEffect(editedMessage) {
        editedMessage?.let {
            snackbarHostState.showSnackbar("$it has been edited")
            onEditedMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = brandedTopAppBarColors(),
                title = {
                    Text(pastry?.pastry?.name ?: "Recipe")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (pastry != null) {
                        IconButton(onClick = {
                            val text = buildShareText(pastry, scale, servings)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, pastry.pastry.name)
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share recipe"))
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share recipe"
                            )
                        }
                        IconButton(onClick = { onEditClick(pastry.pastry.id) }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit recipe"
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete recipe"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        if (pastry == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Recipe not found")
            }
            return@Scaffold
        }

        val details = pastry.pastry

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (details.imageUri != null) {
                AsyncImage(
                    model = details.imageUri,
                    contentDescription = details.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showImageViewer = true }
                )
            }

            Text(
                text = details.name,
                style = MaterialTheme.typography.headlineSmall
            )

            RecipeMeta(details)

            if (baseServings > 0) {
                ServingsStepper(
                    servings = servings,
                    onDecrease = { if (servings > 1) servings-- },
                    onIncrease = { servings++ }
                )
            }

            if (details.category.isNotBlank()) {
                DetailSection(label = "Category", value = details.category)
            }

            if (details.description.isNotBlank()) {
                DetailSection(label = "Description", value = details.description)
            }

            IngredientsSection(pastry.ingredients, scale)

            if (pastry.steps.isNotEmpty()) {
                StepsSection(pastry.steps.sortedBy { it.position })
            }
        }
    }

    val fullImageUri = pastry?.pastry?.imageUri
    if (showImageViewer && fullImageUri != null) {
        Dialog(
            onDismissRequest = { showImageViewer = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showImageViewer = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = fullImageUri,
                    contentDescription = pastry.pastry.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    if (showDeleteDialog && pastry != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete recipe?") },
            text = { Text("Are you sure you want to permanently remove \"${pastry.pastry.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirmed(pastry.pastry)
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun IngredientsSection(
    ingredients: List<com.lovely.bakingrecipes.data.Ingredient>,
    scale: Double = 1.0
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (ingredients.isEmpty()) {
                Text(
                    text = "No ingredients added",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                ingredients.forEach { ingredient ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = ingredient.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${formatAmount(ingredient.amount * scale)} ${ingredient.unit.label}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// Builds a plain-text version of the recipe for sharing.
private fun buildShareText(
    data: PastryWithIngredients,
    scale: Double,
    displayServings: Int
): String {
    val p = data.pastry
    return buildString {
        appendLine(p.name)
        if (p.category.isNotBlank()) appendLine(p.category)
        val meta = buildList {
            if (p.servings > 0) add("$displayServings servings")
            if (p.prepMinutes > 0) add("Prep ${p.prepMinutes} min")
            if (p.cookMinutes > 0) add("Bake ${p.cookMinutes} min")
            if (p.difficulty.isNotBlank()) add(p.difficulty)
        }
        if (meta.isNotEmpty()) appendLine(meta.joinToString(" · "))
        if (p.description.isNotBlank()) {
            appendLine()
            appendLine(p.description)
        }
        if (data.ingredients.isNotEmpty()) {
            appendLine()
            appendLine("Ingredients:")
            data.ingredients.forEach { ing ->
                appendLine("- ${formatAmount(ing.amount * scale)} ${ing.unit.label} ${ing.name}")
            }
        }
        if (data.steps.isNotEmpty()) {
            appendLine()
            appendLine("Baking Procedure:")
            data.steps.sortedBy { it.position }.forEachIndexed { index, step ->
                appendLine("${index + 1}. ${step.instruction}")
            }
        }
    }.trim()
}

@Composable
private fun StepsSection(
    steps: List<com.lovely.bakingrecipes.data.Step>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Baking Procedure",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = step.instruction,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeMeta(pastry: com.lovely.bakingrecipes.data.Pastry) {
    val items = buildList {
        if (pastry.prepMinutes > 0) add("Prep ${pastry.prepMinutes} min")
        if (pastry.cookMinutes > 0) add("Bake ${pastry.cookMinutes} min")
        if (pastry.difficulty.isNotBlank()) add(pastry.difficulty)
    }
    if (items.isEmpty()) return
    Text(
        text = items.joinToString("  •  "),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ServingsStepper(
    servings: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Servings",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDecrease) {
                Icon(Icons.Filled.Remove, contentDescription = "Fewer servings")
            }
            Text(
                text = "$servings",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onIncrease) {
                Icon(Icons.Filled.Add, contentDescription = "More servings")
            }
        }
    }
}

@Composable
private fun DetailSection(
    label: String,
    value: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
