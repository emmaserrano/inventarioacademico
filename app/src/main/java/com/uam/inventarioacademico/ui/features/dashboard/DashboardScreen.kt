package com.uam.inventarioacademico.ui.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val state by viewModel.dashboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardCard(title = "Total de Equipos", value = state.totalEquipos.toString(), modifier = Modifier.fillMaxWidth())
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                DashboardCard(title = "Disponibles", value = state.equiposDisponibles.toString(), modifier = Modifier.weight(1f))
                DashboardCard(title = "Prestados", value = state.equiposPrestados.toString(), modifier = Modifier.weight(1f))
            }
            
            DashboardCard(title = "Categoría Principal", value = state.categoriaPrincipal, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
