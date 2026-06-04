package com.uam.inventarioacademico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.uam.inventarioacademico.data.local.AppDatabase
import com.uam.inventarioacademico.ui.features.dashboard.DashboardScreen
import com.uam.inventarioacademico.ui.features.dashboard.DashboardViewModel
import com.uam.inventarioacademico.ui.features.dashboard.DashboardViewModelFactory
import com.uam.inventarioacademico.ui.features.equipos.EquiposListScreen
import com.uam.inventarioacademico.ui.features.equipos.EquiposViewModel
import com.uam.inventarioacademico.ui.features.equipos.EquiposViewModelFactory
import com.uam.inventarioacademico.ui.features.prestamos.PrestamosListScreen
import com.uam.inventarioacademico.ui.features.prestamos.PrestamosViewModel
import com.uam.inventarioacademico.ui.features.prestamos.PrestamosViewModelFactory
import com.uam.inventarioacademico.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "inventario-db"
        ).build()
        
        val equipoDao = db.equipoDao()
        val prestamoDao = db.prestamoDao()

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf("dashboard") }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                                label = { Text("Resumen") },
                                selected = currentScreen == "dashboard",
                                onClick = { currentScreen = "dashboard" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Build, contentDescription = "Equipos") },
                                label = { Text("Equipos") },
                                selected = currentScreen == "equipos",
                                onClick = { currentScreen = "equipos" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.List, contentDescription = "Préstamos") },
                                label = { Text("Préstamos") },
                                selected = currentScreen == "prestamos",
                                onClick = { currentScreen = "prestamos" }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(innerPadding), 
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (currentScreen) {
                            "dashboard" -> {
                                val dashboardVM: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(equipoDao))
                                DashboardScreen(viewModel = dashboardVM)
                            }
                            "equipos" -> {
                                val equiposVM: EquiposViewModel = viewModel(factory = EquiposViewModelFactory(equipoDao))
                                EquiposListScreen(viewModel = equiposVM)
                            }
                            "prestamos" -> {
                                val prestamosVM: PrestamosViewModel = viewModel(factory = PrestamosViewModelFactory(prestamoDao, equipoDao))
                                PrestamosListScreen(viewModel = prestamosVM)
                            }
                        }
                    }
                }
            }
        }
    }
}