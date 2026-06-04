package com.uam.inventarioacademico.ui.features.equipos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uam.inventarioacademico.data.local.entity.EquipoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquiposListScreen(viewModel: EquiposViewModel) {
    val equipos by viewModel.equipos.collectAsState()
    
    var showDialog by remember { mutableStateOf(false) }
    var equipoToEdit by remember { mutableStateOf<EquipoEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Equipos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                equipoToEdit = null
                showDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Equipo")
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
            items(equipos) { equipo ->
                EquipoItem(
                    equipo = equipo,
                    onEdit = {
                        equipoToEdit = it
                        showDialog = true
                    },
                    onDelete = { viewModel.deleteEquipo(it.id) }
                )
            }
            item { Spacer(modifier = Modifier.height(88.dp)) }
        }
    }

    if (showDialog) {
        EquipoFormDialog(
            equipo = equipoToEdit,
            onDismiss = { showDialog = false },
            onSave = { nombre, categoria, marca, numeroSerie, disponible ->
                if (equipoToEdit == null) {
                    viewModel.addEquipo(nombre, categoria, marca, numeroSerie, disponible)
                } else {
                    viewModel.updateEquipo(equipoToEdit!!.id, nombre, categoria, marca, numeroSerie, disponible)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun EquipoItem(
    equipo: EquipoEntity,
    onEdit: (EquipoEntity) -> Unit,
    onDelete: (EquipoEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Categoría: ${equipo.categoria} | Marca: ${equipo.marca}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "S/N: ${equipo.numeroSerie}", style = MaterialTheme.typography.bodySmall)
                val estado = if (equipo.disponible) "Disponible" else "Prestado"
                Text(text = "Estado: $estado", style = MaterialTheme.typography.bodySmall, color = if (equipo.disponible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
            Row {
                IconButton(onClick = { onEdit(equipo) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onDelete(equipo) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun EquipoFormDialog(
    equipo: EquipoEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Boolean) -> Unit
) {
    var nombre by remember { mutableStateOf(equipo?.nombre ?: "") }
    var categoria by remember { mutableStateOf(equipo?.categoria ?: "") }
    var marca by remember { mutableStateOf(equipo?.marca ?: "") }
    var numeroSerie by remember { mutableStateOf(equipo?.numeroSerie ?: "") }
    var disponible by remember { mutableStateOf(equipo?.disponible ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (equipo == null) "Agregar Equipo" else "Editar Equipo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it },
                    label = { Text("Marca") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = numeroSerie,
                    onValueChange = { numeroSerie = it },
                    label = { Text("Número de Serie") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = disponible,
                        onCheckedChange = { disponible = it }
                    )
                    Text("Disponible")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(nombre, categoria, marca, numeroSerie, disponible) },
                enabled = nombre.isNotBlank() && categoria.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
