package com.example.mirecetario

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MultimediaPasoDao {

    @Insert
    suspend fun insertar(multimedia: MultimediaPaso): Long

    @Query(
        """
        SELECT * 
        FROM multimedia_paso 
        WHERE pasoId = :pasoId 
        ORDER BY orden ASC
        """
    )
    suspend fun obtenerPorPaso(
        pasoId: Int
    ): List<MultimediaPaso>

    @Query(
        """
        DELETE FROM multimedia_paso
        WHERE pasoId = :pasoId
        """
    )
    suspend fun eliminarPorPaso(
        pasoId: Int
    )

    @Query(
        """
        DELETE FROM multimedia_paso
        WHERE id = :multimediaId
        """
    )
    suspend fun eliminarPorId(
        multimediaId: Int
    )

    @Query(
        """
        SELECT COUNT(*)
        FROM multimedia_paso
        WHERE pasoId = :pasoId
        """
    )
    suspend fun contarPorPaso(
        pasoId: Int
    ): Int
}
