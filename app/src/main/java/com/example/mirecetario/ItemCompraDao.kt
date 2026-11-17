package com.example.mirecetario

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemCompraDao {

    @Insert
    suspend fun insertar(item: ItemCompra)

    @Insert
    suspend fun insertarTodos(items: List<ItemCompra>)

    @Query(
        "SELECT * FROM lista_compras " +
                "ORDER BY comprado ASC, id DESC"
    )
    suspend fun obtenerTodos(): List<ItemCompra>

    @Update
    suspend fun actualizar(item: ItemCompra)

    @Delete
    suspend fun eliminar(item: ItemCompra)

    @Query("DELETE FROM lista_compras")
    suspend fun eliminarTodos()

    @Query(
        """
        SELECT COUNT(*) 
        FROM lista_compras
        WHERE LOWER(TRIM(descripcion)) = LOWER(TRIM(:descripcion))
        """
    )
    suspend fun existeDescripcion(
        descripcion: String
    ): Int
}