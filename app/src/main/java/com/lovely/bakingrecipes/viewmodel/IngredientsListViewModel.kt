package com.lovely.bakingrecipes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lovely.bakingrecipes.data.IngredientUnit
import com.lovely.bakingrecipes.data.PastryWithIngredients
import com.lovely.bakingrecipes.repository.PastryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class IngredientEntry(
    val pastryId: Int,
    val pastryName: String,
    val amount: Double,
    val unit: IngredientUnit
)

data class UnitTotal(
    val unit: IngredientUnit,
    val amount: Double
)

data class IngredientGroup(
    val name: String,
    val pastryCount: Int,
    val totalsByUnit: List<UnitTotal>,
    val entries: List<IngredientEntry>
)

class IngredientsListViewModel(
    repository: PastryRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    val searchQuery: StateFlow<String> = query

    val isLoading: StateFlow<Boolean> =
        repository.allPastries.map { false }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    val groups: StateFlow<List<IngredientGroup>> =
        combine(repository.allPastries, query) { pastries, search ->
            groupIngredients(pastries).filter {
                search.isBlank() || it.name.contains(search, ignoreCase = true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onSearchChange(newQuery: String) {
        query.value = newQuery
    }
}

// Top-level so it can be unit-tested without a ViewModel.
fun groupIngredients(pastries: List<PastryWithIngredients>): List<IngredientGroup> =
    pastries
        .flatMap { pw ->
            pw.ingredients.map { ingredient ->
                IngredientEntry(
                    pastryId = pw.pastry.id,
                    pastryName = pw.pastry.name,
                    amount = ingredient.amount,
                    unit = ingredient.unit
                ) to ingredient.name.trim()
            }
        }
        // Group case-insensitively so "Flour" and "flour" merge.
        .groupBy { it.second.lowercase() }
        .map { (_, pairs) ->
            val entries = pairs
                .map { it.first }
                .sortedBy { it.pastryName.lowercase() }
            val totals = entries
                .groupBy { it.unit }
                .map { (unit, list) -> UnitTotal(unit, list.sumOf { it.amount }) }
                .sortedBy { it.unit.ordinal }
            IngredientGroup(
                name = pairs.first().second,
                pastryCount = entries.map { it.pastryName }.distinct().size,
                totalsByUnit = totals,
                entries = entries
            )
        }
        .sortedBy { it.name.lowercase() }
