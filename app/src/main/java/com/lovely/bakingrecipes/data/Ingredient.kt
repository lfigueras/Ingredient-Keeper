package com.lovely.bakingrecipes.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class IngredientUnit(val label: String, val displayName: String) {
    PIECES("pcs", "Pieces"),
    GRAMS("g", "Grams"),
    KILOGRAMS("kg", "Kilograms"),
    OUNCES("oz", "Ounces"),
    POUNDS("lb", "Pounds"),
    MILLILITERS("ml", "Milliliters"),
    LITERS("L", "Liters"),
    TEASPOONS("tsp", "Teaspoons"),
    TABLESPOONS("tbsp", "Tablespoons"),
    CUPS("cup", "Cups"),
    PINCH("pinch", "Pinch")
}

@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Pastry::class,
            parentColumns = ["id"],
            childColumns = ["pastryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pastryId")]
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val pastryId: Int,
    val name: String,
    val amount: Double,
    val unit: IngredientUnit
)

// Drops trailing ".0" so whole amounts read as "250 g" rather than "250.0 g".
fun formatAmount(amount: Double): String =
    if (amount % 1.0 == 0.0) amount.toLong().toString() else amount.toString()
