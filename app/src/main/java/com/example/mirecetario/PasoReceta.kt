package com.example.mirecetario

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pasos_receta",
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
data class PasoReceta(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val recetaId: Int,

    val numeroPaso: Int,

    val descripcion: String,

    val multimediaUri: String? = null,

    val tipoMultimedia: String? = null
)