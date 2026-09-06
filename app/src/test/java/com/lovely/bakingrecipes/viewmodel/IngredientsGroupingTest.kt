package com.lovely.bakingrecipes.viewmodel

import com.lovely.bakingrecipes.data.Ingredient
import com.lovely.bakingrecipes.data.IngredientUnit
import com.lovely.bakingrecipes.data.Pastry
import com.lovely.bakingrecipes.data.PastryWithIngredients
import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientsGroupingTest {

    private fun recipe(id: Int, name: String, ingredients: List<Ingredient>) =
        PastryWithIngredients(
            pastry = Pastry(id = id, name = name, category = "", description = "", imageUri = null),
            ingredients = ingredients
        )

    private fun ing(name: String, amount: Double, unit: IngredientUnit) =
        Ingredient(pastryId = 0, name = name, amount = amount, unit = unit)

    @Test
    fun mergesSameNameCaseInsensitively_andSumsPerUnit() {
        val data = listOf(
            recipe(1, "Cake", listOf(ing("Flour", 200.0, IngredientUnit.GRAMS))),
            recipe(2, "Cookies", listOf(ing("flour", 100.0, IngredientUnit.GRAMS)))
        )
        val groups = groupIngredients(data)
        val flour = groups.first { it.name.equals("flour", ignoreCase = true) }
        assertEquals(2, flour.pastryCount)
        assertEquals(1, flour.totalsByUnit.size)
        assertEquals(300.0, flour.totalsByUnit.first().amount, 0.001)
    }

    @Test
    fun keepsUnitsSeparateWhenMixed() {
        val data = listOf(
            recipe(1, "A", listOf(ing("Milk", 250.0, IngredientUnit.MILLILITERS))),
            recipe(2, "B", listOf(ing("Milk", 1.0, IngredientUnit.CUPS)))
        )
        val groups = groupIngredients(data)
        val milk = groups.first { it.name == "Milk" }
        assertEquals(2, milk.totalsByUnit.size)
    }

    @Test
    fun sortsGroupsAlphabetically() {
        val data = listOf(
            recipe(1, "A", listOf(ing("Sugar", 1.0, IngredientUnit.GRAMS), ing("Butter", 1.0, IngredientUnit.GRAMS)))
        )
        val names = groupIngredients(data).map { it.name }
        assertEquals(listOf("Butter", "Sugar"), names)
    }
}
