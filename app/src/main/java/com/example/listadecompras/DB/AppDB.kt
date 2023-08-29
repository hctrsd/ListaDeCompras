package com.example.listadecompras.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Articulos::class], version = 1)

abstract class AppDataBase : RoomDatabase() {
    abstract fun tareaDao(): ArticulosDao

    companion object {
        // Volatile asegura que sea actualizada la propiedad
        // atómicamente - singleton - para crear un solo objeto de base de datos en memoria
        @Volatile
        private var BASE_DATOS: AppDataBase? = null

        fun getInstance(contexto: Context): AppDataBase {
            // synchronized previene el acceso de múltiples threads de manera simultánea
            return BASE_DATOS ?: synchronized(this) {
                Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDataBase::class.java,
                    "tareas.bd"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { BASE_DATOS = it }
            }

        }
    }
}