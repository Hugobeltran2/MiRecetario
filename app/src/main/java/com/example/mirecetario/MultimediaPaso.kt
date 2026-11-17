package com.example.mirecetario

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "multimedia_paso",
    foreignKeys = [
        ForeignKey(
            entity = PasoReceta::class,
            parentColumns = ["id"],
            childColumns = ["pasoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["pasoId"])
    ]
)
data class MultimediaPaso(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val pasoId: Int,

    val uri: String,

    val tipo: String,

    val orden: Int
)