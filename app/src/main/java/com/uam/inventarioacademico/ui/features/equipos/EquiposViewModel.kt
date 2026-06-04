package com.uam.inventarioacademico.ui.features.equipos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uam.inventarioacademico.data.local.dao.EquipoDao
import com.uam.inventarioacademico.data.local.entity.EquipoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EquiposViewModel(private val equipoDao: EquipoDao) : ViewModel() {

    private val _equipos = MutableStateFlow<List<EquipoEntity>>(emptyList())
    val equipos: StateFlow<List<EquipoEntity>> = _equipos.asStateFlow()

    init {
        loadEquipos()
    }

    private fun loadEquipos() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = equipoDao.getAllEquipos()
            _equipos.value = list
        }
    }

    fun addEquipo(nombre: String, categoria: String, marca: String, numeroSerie: String, disponible: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            equipoDao.insertEquipo(nombre, categoria, marca, numeroSerie, disponible)
            loadEquipos()
        }
    }

    fun updateEquipo(id: Int, nombre: String, categoria: String, marca: String, numeroSerie: String, disponible: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            equipoDao.updateEquipo(id, nombre, categoria, marca, numeroSerie, disponible)
            loadEquipos()
        }
    }

    fun deleteEquipo(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            equipoDao.deleteEquipo(id)
            loadEquipos()
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
