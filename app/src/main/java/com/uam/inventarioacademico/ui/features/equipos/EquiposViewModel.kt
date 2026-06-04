package com.uam.inventarioacademico.ui.features.equipos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uam.inventarioacademico.data.local.dao.EquipoDao
import com.uam.inventarioacademico.data.local.entity.EquipoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EquiposViewModel(private val equipoDao: EquipoDao) : ViewModel() {

    val equipos: StateFlow<List<EquipoEntity>> = equipoDao.getAllEquipos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addEquipo(nombre: String, categoria: String, marca: String, numeroSerie: String, disponible: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            equipoDao.insertEquipo(nombre, categoria, marca, numeroSerie, disponible)
        }
    }

    fun updateEquipo(id: Int, nombre: String, categoria: String, marca: String, numeroSerie: String, disponible: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            equipoDao.updateEquipo(id, nombre, categoria, marca, numeroSerie, disponible)
        }
    }

    fun deleteEquipo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            equipoDao.deleteEquipo(id)
        }
    }
}

class EquiposViewModelFactory(private val equipoDao: EquipoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EquiposViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EquiposViewModel(equipoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
