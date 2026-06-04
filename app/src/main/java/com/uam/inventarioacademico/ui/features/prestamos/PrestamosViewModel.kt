package com.uam.inventarioacademico.ui.features.prestamos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uam.inventarioacademico.data.local.dao.EquipoDao
import com.uam.inventarioacademico.data.local.dao.PrestamoDao
import com.uam.inventarioacademico.data.local.entity.EquipoEntity
import com.uam.inventarioacademico.data.local.entity.PrestamoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrestamosViewModel(
    private val prestamoDao: PrestamoDao,
    private val equipoDao: EquipoDao
) : ViewModel() {

    private val _prestamos = MutableStateFlow<List<PrestamoEntity>>(emptyList())
    val prestamos: StateFlow<List<PrestamoEntity>> = _prestamos.asStateFlow()

    private val _equipos = MutableStateFlow<List<EquipoEntity>>(emptyList())
    val equipos: StateFlow<List<EquipoEntity>> = _equipos.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _prestamos.value = prestamoDao.getAllPrestamos()
            _equipos.value = equipoDao.getAllEquipos()
        }
    }

    fun registrarPrestamo(equipoId: Int, solicitante: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            prestamoDao.insertPrestamo(equipoId, solicitante, fechaActual, null)
            
            // Actualizar el equipo a NO disponible
            val equipo = equipoDao.loadEquipoById(equipoId)
            if (equipo != null) {
                equipoDao.updateEquipo(equipo.id, equipo.nombre, equipo.categoria, equipo.marca, equipo.numeroSerie, false)
            }
            
            loadData()
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
            
            loadData()
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
