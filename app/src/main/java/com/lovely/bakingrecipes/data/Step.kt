package com.lovely.bakingrecipes.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "steps",
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
data class Step(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val pastryId: Int,
    val position: Int,
    val instruction: String
)
