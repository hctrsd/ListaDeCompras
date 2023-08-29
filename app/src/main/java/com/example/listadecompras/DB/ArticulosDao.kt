package com.example.listadecompras.DB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ArticulosDao {
    @Query("SELECT * FROM Articulos ORDER BY realizada")
    fun findAll(): List<Articulos>

    @Query("SELECT COUNT(*) FROM Articulos ")
    fun contar(): Int // video 3

    @Insert
    fun  insertar(articulos: Articulos): Long

    @Update
    fun actualizar(articulos: Articulos)

    @Delete
    fun eliminar(articulos:Articulos)

}