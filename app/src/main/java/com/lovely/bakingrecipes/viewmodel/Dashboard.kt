package com.lovely.bakingrecipes.viewmodel

import com.lovely.bakingrecipes.data.PastryCategories
import com.lovely.bakingrecipes.data.PastryWithIngredients

sealed interface DashboardTarget {
    data object Ingredients : DashboardTarget
    data object AllPastries : DashboardTarget
    data class Category(val name: String) : DashboardTarget
}

data class DashboardTile(
    val label: String,
    val count: Int,
    val target: DashboardTarget
)

// Ingredients and all-recipes first, then categories ordered by count then name.
fun buildDashboardTiles(pastries: List<PastryWithIngredients>): List<DashboardTile> {
    val tiles = mutableListOf<DashboardTile>()
    // Count unique ingredient names so the tile matches the grouped Ingredients screen.
    val uniqueIngredients = pastries
        .flatMap { it.ingredients }
        .map { it.name.trim().lowercase() }
        .filter { it.isNotEmpty() }
        .distinct()
        .size
    tiles += DashboardTile(
        label = if (uniqueIngredients == 1) "Ingredient" else "Ingredients",
        count = uniqueIngredients,
        target = DashboardTarget.Ingredients
    )
    tiles += DashboardTile(
        label = "All Recipes",
        count = pastries.size,
        target = DashboardTarget.AllPastries
    )
    pastries
        .filter { it.pastry.category.isNotBlank() }
        .groupBy { it.pastry.category }
        .entries
        .sortedWith(
            compareByDescending<Map.Entry<String, List<PastryWithIngredients>>> { it.value.size }
                .thenBy { it.key }
        )
        .forEach { (category, list) ->
            // Singular label when only one recipe uses this category.
            val label = if (list.size == 1) PastryCategories.singular(category) else category
            tiles += DashboardTile(label, list.size, DashboardTarget.Category(category))
        }
    return tiles
}
