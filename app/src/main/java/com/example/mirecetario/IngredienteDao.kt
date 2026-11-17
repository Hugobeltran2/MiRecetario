package com.example.mirecetario

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IngredienteDao {

    @Insert
    suspend fun insertar(ingrediente: Ingrediente)

    @Query(
        "SELECT * FROM ingredientes " +
                "WHERE recetaId = :recetaId " +
                "ORDER BY orden ASC"
    )
    suspend fun obtenerPorReceta(recetaId: Int): List<Ingrediente>
    @Query("DELETE FROM ingredientes WHERE recetaId = :recetaId")
    suspend fun eliminarPorReceta(recetaId: Int)
}