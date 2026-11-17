package com.example.mirecetario

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PasoRecetaDao {

    @Insert
    suspend fun insertar(paso: PasoReceta)

    @Query(
        "SELECT * FROM pasos_receta " +
                "WHERE recetaId = :recetaId " +
                "ORDER BY numeroPaso ASC"
    )
    suspend fun obtenerPorReceta(recetaId: Int): List<PasoReceta>
    @Query("DELETE FROM pasos_receta WHERE recetaId = :recetaId")
    suspend fun eliminarPorReceta(recetaId: Int)
}