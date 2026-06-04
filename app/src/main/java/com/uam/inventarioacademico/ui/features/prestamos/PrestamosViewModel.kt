package com.uam.inventarioacademico.ui.features.prestamos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uam.inventarioacademico.data.local.dao.EquipoDao
import com.uam.inventarioacademico.data.local.dao.PrestamoDao
import com.uam.inventarioacademico.data.local.entity.EquipoEntity
import com.uam.inventarioacademico.data.local.entity.PrestamoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrestamosViewModel(
    private val prestamoDao: PrestamoDao,
    private val equipoDao: EquipoDao
) : ViewModel() {

    val prestamos: StateFlow<List<PrestamoEntity>> = prestamoDao.getAllPrestamos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val equipos: StateFlow<List<EquipoEntity>> = equipoDao.getAllEquipos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun registrarPrestamo(equipoId: Int, solicitante: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            prestamoDao.insertPrestamo(equipoId, solicitante, fechaActual, null)
            
            // Actualizar el equipo a NO disponible
            val equipo = equipoDao.loadEquipoById(equipoId)
            if (equipo != null) {
                equipoDao.updateEquipo(equipo.id, equipo.nombre, equipo.categoria, equipo.marca, equipo.numeroSerie, false)
            }
        }
    }

    fun registrarDevolucion(prestamo: PrestamoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            // Actualizar prestamo con fecha de devolución
            prestamoDao.updatePrestamo(
                prestamo.id, 
                prestamo.equipoId, 
                prestamo.solicitante, 
                prestamo.fechaPrestamo, 
                fechaActual
            )
            
            // Actualizar el equipo a DISPONIBLE
            val equipo = equipoDao.loadEquipoById(prestamo.equipoId)
            if (equipo != null) {
                equipoDao.updateEquipo(equipo.id, equipo.nombre, equipo.categoria, equipo.marca, equipo.numeroSerie, true)
            }
        }
    }
}

class PrestamosViewModelFactory(
    private val prestamoDao: PrestamoDao,
    private val equipoDao: EquipoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrestamosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrestamosViewModel(prestamoDao, equipoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
