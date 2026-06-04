package com.uam.inventarioacademico.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.uam.inventarioacademico.data.local.entity.EquipoEntity

@Dao
interface EquipoDao{
    @Query("SELECT * FROM equipo")
    fun getAllEquipos(): List<EquipoEntity>

    @Query("SELECT * FROM equipo WHERE id = :id")
    fun loadEquipoById(id: Int): EquipoEntity

    @Query("INSERT INTO equipo (nombre, categoria, marca, numeroSerie, disponible) VALUES (:nombre, :categoria, :marca, :numeroSerie, :disponible)")
    fun insertEquipo(nombre: String, categoria: String, marca: String, numeroSerie: String, disponible: Boolean)

    @Query("UPDATE equipo SET nombre = :nombre, categoria = :categoria, marca = :marca, numeroSerie = :numeroSerie, disponible = :disponible WHERE id = :id")
    fun updateEquipo(id: Int, nombre: String, categoria: String, marca: String, numeroSerie: String, disponible: Boolean)

    @Query("DELETE FROM equipo WHERE id = :id")
    fun deleteEquipo(id: Int)
}