package com.example.mirecetario

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredientes",
    foreignKeys = [
        ForeignKey(
            entity = Receta::class,
            parentColumns = ["id"],
            childColumns = ["recetaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["recetaId"])
    ]
)
data class Ingrediente(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val recetaId: Int,

    val descripcion: String,

    val orden: Int
)