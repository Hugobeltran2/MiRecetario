package com.example.mirecetario

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecetaDao {

    @Insert
    suspend fun insertar(receta: Receta): Long

    @Query("SELECT * FROM recetas ORDER BY id DESC")
    suspend fun obtenerTodas(): List<Receta>

    @Update
    suspend fun actualizar(receta: Receta)

    @Delete
    suspend fun eliminar(receta: Receta)
}