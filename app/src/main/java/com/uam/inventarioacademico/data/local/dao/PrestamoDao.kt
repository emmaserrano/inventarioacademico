package com.uam.inventarioacademico.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.uam.inventarioacademico.data.local.entity.PrestamoEntity

@Dao
interface PrestamoDao{
    @Query("SELECT * FROM prestamo")
    fun getAllPrestamos(): kotlinx.coroutines.flow.Flow<List<PrestamoEntity>>

    @Query("SELECT * FROM prestamo WHERE id = :id")
    fun loadPrestamoById(id: Int): PrestamoEntity

    @Query("INSERT INTO prestamo (equipoId, solicitante, fechaPrestamo, fechaDevolucion) VALUES (:equipoId, :solicitante, :fechaPrestamo, :fechaDevolucion)")
    fun insertPrestamo(equipoId: Int, solicitante: String, fechaPrestamo: String, fechaDevolucion: String?)

    @Query("UPDATE prestamo SET equipoId = :equipoId, solicitante = :solicitante, fechaPrestamo = :fechaPrestamo, fechaDevolucion = :fechaDevolucion WHERE id = :id")
    fun updatePrestamo(id: Int, equipoId: Int, solicitante: String, fechaPrestamo: String, fechaDevolucion: String?)

    @Query("DELETE FROM prestamo WHERE id = :id")
    fun deletePrestamo(id: Int)
}