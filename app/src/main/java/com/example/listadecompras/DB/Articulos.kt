package com.example.listadecompras.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Articulos(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var tarea: String,
    var realizada: Boolean
)