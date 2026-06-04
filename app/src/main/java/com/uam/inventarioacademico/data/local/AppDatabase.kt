package com.uam.inventarioacademico.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uam.inventarioacademico.data.local.dao.EquipoDao
import com.uam.inventarioacademico.data.local.dao.PrestamoDao
import com.uam.inventarioacademico.data.local.entity.EquipoEntity
import com.uam.inventarioacademico.data.local.entity.PrestamoEntity

@Database(entities = [EquipoEntity::class, PrestamoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipoDao(): EquipoDao
    abstract fun prestamoDao(): PrestamoDao
}