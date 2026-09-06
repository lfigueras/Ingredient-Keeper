package com.lovely.bakingrecipes.data

import androidx.room.Embedded
import androidx.room.Relation

data class PastryWithIngredients(
    @Embedded val pastry: Pastry,
    @Relation(
        parentColumn = "id",
        entityColumn = "pastryId"
    )
    val ingredients: List<Ingredient>,
    @Relation(
        parentColumn = "id",
        entityColumn = "pastryId"
    )
    val steps: List<Step> = emptyList()
)
