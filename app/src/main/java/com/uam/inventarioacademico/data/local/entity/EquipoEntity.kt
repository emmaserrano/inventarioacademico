package com.uam.inventarioacademico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipo")
data class EquipoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,

    val categoria: String,

    val marca: String,

    val numeroSerie: String,

    val disponible: Boolean = true
)