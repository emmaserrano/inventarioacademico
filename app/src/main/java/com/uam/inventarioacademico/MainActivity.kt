package com.uam.inventarioacademico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.uam.inventarioacademico.data.local.AppDatabase
import com.uam.inventarioacademico.ui.features.equipos.EquiposListScreen
import com.uam.inventarioacademico.ui.features.equipos.EquiposViewModel
import com.uam.inventarioacademico.ui.features.equipos.EquiposViewModelFactory
import com.uam.inventarioacademico.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos de manera sencilla para este ejemplo
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "inventario-db"
        ).build()
        val equipoDao = db.equipoDao()

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel: EquiposViewModel = viewModel(factory = EquiposViewModelFactory(equipoDao))
                    EquiposListScreen(viewModel = viewModel)
                }
            }
        }
    }
}