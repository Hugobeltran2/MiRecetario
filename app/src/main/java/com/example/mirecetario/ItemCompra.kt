package com.example.mirecetario

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lista_compras")
data class ItemCompra(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val descripcion: String,

    val comprado: Boolean = false
)
