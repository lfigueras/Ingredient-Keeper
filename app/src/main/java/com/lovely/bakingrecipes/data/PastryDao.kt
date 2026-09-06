package com.lovely.bakingrecipes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PastryDao {

    @Insert
    suspend fun insertPastry(pastry: Pastry): Long

    @Update
    suspend fun updatePastry(pastry: Pastry)

    @Insert
    suspend fun insertIngredients(ingredients: List<Ingredient>)

    @Query("DELETE FROM ingredients WHERE pastryId = :pastryId")
    suspend fun deleteIngredientsForPastry(pastryId: Int)

    @Insert
    suspend fun insertSteps(steps: List<Step>)

    @Query("DELETE FROM steps WHERE pastryId = :pastryId")
    suspend fun deleteStepsForPastry(pastryId: Int)

    @Delete
    suspend fun deletePastry(pastry: Pastry)

    @Transaction
    @Query("SELECT * FROM pastries ORDER BY id DESC")
    fun getAllPastriesWithIngredients(): Flow<List<PastryWithIngredients>>

    @Transaction
    @Query("SELECT * FROM pastries WHERE id = :id")
    fun getPastryWithIngredients(id: Int): Flow<PastryWithIngredients?>
}