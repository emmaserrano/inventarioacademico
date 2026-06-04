package com.uam.inventarioacademico.ui.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.uam.inventarioacademico.data.local.dao.EquipoDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DashboardState(
    val totalEquipos: Int = 0,
    val equiposDisponibles: Int = 0,
    val equiposPrestados: Int = 0,
    val categoriaPrincipal: String = "N/A"
)

class DashboardViewModel(private val equipoDao: EquipoDao) : ViewModel() {

    val dashboardState: StateFlow<DashboardState> = equipoDao.getAllEquipos()
        .map { equipos ->
            val total = equipos.size
            val disponibles = equipos.count { it.disponible }
            val prestados = total - disponibles
            
            // Calculate most frequent category
            val categoriaMax = equipos.groupingBy { it.categoria }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: "N/A"

            DashboardState(
                totalEquipos = total,
                equiposDisponibles = disponibles,
                equiposPrestados = prestados,
                categoriaPrincipal = categoriaMax
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardState()
        )
}

class DashboardViewModelFactory(private val equipoDao: EquipoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(equipoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
