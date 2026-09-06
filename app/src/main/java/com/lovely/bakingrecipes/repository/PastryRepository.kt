package com.lovely.bakingrecipes.repository

import com.lovely.bakingrecipes.data.Ingredient
import com.lovely.bakingrecipes.data.Pastry
import com.lovely.bakingrecipes.data.PastryDao
import com.lovely.bakingrecipes.data.PastryWithIngredients
import com.lovely.bakingrecipes.data.Step
import kotlinx.coroutines.flow.Flow

class PastryRepository(
    private val pastryDao: PastryDao
) {

    val allPastries: Flow<List<PastryWithIngredients>> =
        pastryDao.getAllPastriesWithIngredients()

    fun getPastryById(id: Int): Flow<PastryWithIngredients?> =
        pastryDao.getPastryWithIngredients(id)

    suspend fun insertPastryWithIngredients(
        pastry: Pastry,
        ingredients: List<Ingredient>,
        steps: List<Step>
    ) {
        val pastryId = pastryDao.insertPastry(pastry).toInt()
        if (ingredients.isNotEmpty()) {
            pastryDao.insertIngredients(ingredients.map { it.copy(pastryId = pastryId) })
        }
        if (steps.isNotEmpty()) {
            pastryDao.insertSteps(
                steps.mapIndexed { index, step -> step.copy(pastryId = pastryId, position = index) }
            )
        }
    }

    suspend fun updatePastryWithIngredients(
        pastry: Pastry,
        ingredients: List<Ingredient>,
        steps: List<Step>
    ) {
        pastryDao.updatePastry(pastry)
        pastryDao.deleteIngredientsForPastry(pastry.id)
        if (ingredients.isNotEmpty()) {
            pastryDao.insertIngredients(ingredients.map { it.copy(pastryId = pastry.id) })
        }
        pastryDao.deleteStepsForPastry(pastry.id)
        if (steps.isNotEmpty()) {
            pastryDao.insertSteps(
                steps.mapIndexed { index, step -> step.copy(pastryId = pastry.id, position = index) }
            )
        }
    }

    suspend fun deletePastry(pastry: Pastry) {
        pastryDao.deletePastry(pastry)
    }
}