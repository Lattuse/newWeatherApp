package com.example.weatherapp.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapp.domain.model.FavoriteCity
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    vm: FavoritesViewModel,
    onBack: () -> Unit
) {
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()

    var showAdd by remember { mutableStateOf(false) }
    var editItem by remember { mutableStateOf<FavoriteCity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } },
                actions = {
                    TextButton(
                        onClick = { showAdd = true },
                        enabled = state.uid != null
                    ) { Text("Add") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
                return@Column
            }

            // UID indicator
            state.uid?.let {
                Text("uid: $it", style = MaterialTheme.typography.labelSmall)
            } ?: run {
                Text("Signing in...", style = MaterialTheme.typography.bodySmall)
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (state.favorites.isEmpty()) {
                Text("No favorites yet. Tap Add.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.favorites, key = { it.id }) { item ->
                        FavoriteRow(
                            item = item,
                            onEdit = { editItem = item },
                            onDelete = { vm.delete(item.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddFavoriteDialog(
            errorText = state.error,
            onDismiss = { showAdd = false },
            onAdd = { city, note ->
                scope.launch {
                    val ok = vm.addAndReturnSuccess(city, note)
                    if (ok) showAdd = false
                }
            }
        )
    }

    editItem?.let { item ->
        EditNoteDialog(
            cityName = item.cityName,
            initialNote = item.note,
            onDismiss = { editItem = null },
            onSave = { newNote ->
                vm.editNote(item.id, newNote)
                editItem = null
            }
        )
    }
}

@Composable
private fun FavoriteRow(
    item: FavoriteCity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.cityName, style = MaterialTheme.typography.titleMedium)
            Text(
                if (item.note.isBlank()) "No note" else item.note,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) { Text("Edit note") }
                OutlinedButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}

@Composable
private fun AddFavoriteDialog(
    errorText: String?,
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    var city by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add favorite city") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") }
                )

                errorText?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(city, note) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun EditNoteDialog(
    cityName: String,
    initialNote: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var note by remember { mutableStateOf(initialNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit note: $cityName") },
        text = {
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(note) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}