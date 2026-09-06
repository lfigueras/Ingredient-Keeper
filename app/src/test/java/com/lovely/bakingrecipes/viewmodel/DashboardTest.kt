package com.lovely.bakingrecipes.viewmodel

import com.lovely.bakingrecipes.data.Ingredient
import com.lovely.bakingrecipes.data.IngredientUnit
import com.lovely.bakingrecipes.data.Pastry
import com.lovely.bakingrecipes.data.PastryWithIngredients
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardTest {

    private fun recipe(id: Int, name: String, category: String, ingredients: List<Ingredient>) =
        PastryWithIngredients(
            pastry = Pastry(id = id, name = name, category = category, description = "", imageUri = null),
            ingredients = ingredients
        )

    private fun ing(name: String, unit: IngredientUnit = IngredientUnit.GRAMS) =
        Ingredient(pastryId = 0, name = name, amount = 1.0, unit = unit)

    @Test
    fun ingredientsTile_countsUniqueNames() {
        val data = listOf(
            recipe(1, "Choc Cake", "Cakes", listOf(ing("Flour"), ing("Sugar"))),
            recipe(2, "Vanilla Cake", "Cakes", listOf(ing("flour"), ing("Eggs")))
        )
        val tile = buildDashboardTiles(data).first { it.target == DashboardTarget.Ingredients }
        assertEquals(3, tile.count) // Flour, Sugar, Eggs (case-insensitive)
        assertEquals("Ingredients", tile.label)
    }

    @Test
    fun allRecipesTile_countsRecipes() {
        val data = listOf(
            recipe(1, "A", "Cakes", emptyList()),
            recipe(2, "B", "Cookies", emptyList())
        )
        val tile = buildDashboardTiles(data).first { it.target == DashboardTarget.AllPastries }
        assertEquals(2, tile.count)
    }

    @Test
    fun categoryTile_isSingularForOne() {
        val data = listOf(recipe(1, "A", "Cakes", emptyList()))
        val tile = buildDashboardTiles(data).first { it.target == DashboardTarget.Category("Cakes") }
        assertEquals(1, tile.count)
        assertEquals("Cake", tile.label)
    }

    @Test
    fun categoryTile_isPluralForMany() {
        val data = listOf(
            recipe(1, "A", "Cookies", emptyList()),
            recipe(2, "B", "Cookies", emptyList())
        )
        val tile = buildDashboardTiles(data).first { it.target == DashboardTarget.Category("Cookies") }
        assertEquals(2, tile.count)
        assertEquals("Cookies", tile.label)
    }
}
