package com.uam.inventarioacademico.ui.features.prestamos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uam.inventarioacademico.data.local.entity.EquipoEntity
import com.uam.inventarioacademico.data.local.entity.PrestamoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamosListScreen(viewModel: PrestamosViewModel) {
    val prestamos by viewModel.prestamos.collectAsState()
    val equipos by viewModel.equipos.collectAsState()
    
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Préstamos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Préstamo")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(prestamos) { prestamo ->
                // Buscar el nombre del equipo para mostrarlo bonito
                val equipoNombre = equipos.find { it.id == prestamo.equipoId }?.nombre ?: "Equipo Desconocido"
                
                PrestamoItem(
                    prestamo = prestamo,
                    equipoNombre = equipoNombre,
                    onDevolver = { viewModel.registrarDevolucion(it) }
                )
            }
            if (prestamos.isEmpty()) {
                item {
                    Text("No hay préstamos registrados.", modifier = Modifier.padding(16.dp))
                }
            }
            item { Spacer(modifier = Modifier.height(88.dp)) }
        }
    }

    if (showDialog) {
        val equiposDisponibles = equipos.filter { it.disponible }
        
        PrestamoFormDialog(
            equiposDisponibles = equiposDisponibles,
            onDismiss = { showDialog = false },
            onSave = { equipoId, solicitante ->
                viewModel.registrarPrestamo(equipoId, solicitante)
                showDialog = false
            }
        )
    }
}

@Composable
fun PrestamoItem(
    prestamo: PrestamoEntity,
    equipoNombre: String,
    onDevolver: (PrestamoEntity) -> Unit
) {
    val activo = prestamo.fechaDevolucion == null
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activo) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equipoNombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Solicitante: ${prestamo.solicitante}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Préstamo: ${prestamo.fechaPrestamo}", style = MaterialTheme.typography.bodySmall)
                if (!activo) {
                    Text(text = "Devuelto: ${prestamo.fechaDevolucion}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                } else {
                    Text(text = "Estado: EN CURSO", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
            if (activo) {
                IconButton(onClick = { onDevolver(prestamo) }) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Registrar Devolución", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoFormDialog(
    equiposDisponibles: List<EquipoEntity>,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var solicitante by remember { mutableStateOf("") }
    var selectedEquipo by remember { mutableStateOf<EquipoEntity?>(null) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Préstamo") },
        text = {
            if (equiposDisponibles.isEmpty()) {
                Text("No hay equipos disponibles para prestar en este momento.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Selector de Equipo (Dropdown sencillo)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedEquipo?.nombre ?: "Selecciona un equipo",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Equipo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            equiposDisponibles.forEach { equipo ->
                                DropdownMenuItem(
                                    text = { Text("${equipo.nombre} (S/N: ${equipo.numeroSerie})") },
                                    onClick = {
                                        selectedEquipo = equipo
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = solicitante,
                        onValueChange = { solicitante = it },
                        label = { Text("Nombre del Solicitante") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (equiposDisponibles.isNotEmpty()) {
                TextButton(
                    onClick = { onSave(selectedEquipo!!.id, solicitante) },
                    enabled = solicitante.isNotBlank() && selectedEquipo != null
                ) {
                    Text("Registrar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
